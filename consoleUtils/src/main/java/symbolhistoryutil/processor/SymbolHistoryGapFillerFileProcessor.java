package symbolhistoryutil.processor;

import lombok.Value;
import symbolhistoryutil.dto.Bar;
import symbolhistoryutil.dto.MetaTraderTimeframe;
import symbolhistoryutil.filemappers.HstFileWriter;
import symbolhistoryutil.filemappers.SymbolHistoryCsvFileReader;
import unsorted.StaticLogger;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static unsorted.StaticLogger.info;
import static unsorted.StaticLogger.infoHardCoded;
import static unsorted.SystemService.newUtcDate;

@Value
public class SymbolHistoryGapFillerFileProcessor {
    //https://www.evernote.com/l/AAodCDwfG8lP6YH5wzDOA92gSJgTZmH70Mk/
    final static Optional<Date> targetEldestBarDate =empty();//of(newUtcDate(2010, Calendar.JANUARY, 01));
    final static Optional<Date> targetYoungestBarFence =empty();//of(newUtcDate(2023, Calendar.DECEMBER, 31));
    static {
        targetEldestBarDate.ifPresent(date -> StaticLogger.infoHardCoded(format("static targetOldestBarDate: %s", targetEldestBarDate)));
        targetYoungestBarFence.ifPresent(date -> StaticLogger.infoHardCoded(format("static targetYoungestBarFence: %s", targetYoungestBarFence)));
    }

    SymbolHistoryCsvFileReader.CsvFileParams inParams;
    ProcessingParams processingParams;

    public BarFileMappingStat process() throws IOException {
        final BarStreamSpy upStreamSpy = new BarStreamSpy();
        final AtomicReference<Optional<Bar>> prevBar= new AtomicReference<>(empty());
        final BarStreamSpy downStreamSpy = new BarStreamSpy(bar -> {
            prevBar.get().ifPresent(pb -> {
                if(bar.getDatetime()-pb.getDatetime()!= processingParams.getTimeframe().getMs())
                    info(format("anomalous bar distance passing to target file (%d minutes!=%d):  prev: %s current: %s",
                            toMinutes(bar.getDatetime()-pb.getDatetime()),
                            processingParams.getTimeframe().getMinutes(),
                            pb.getDate(), bar.getDate()));
            });
            prevBar.set(of(bar));
        });

        final SymbolHistoryGapFiller symbolHistoryGapFiller = new SymbolHistoryGapFiller(
                processingParams.getTimeframe(), new TargetBarCntJustification(getTargetEldestBarDate(processingParams.getTimeframe()), targetYoungestBarFence));

        //wrapping with try to make sure the buffer is flushed
        try(HstFileWriter writer = new HstFileWriter(processingParams.getHstFileParams())) {
            writer.write(
                    symbolHistoryGapFiller.process(
                            new SymbolHistoryCsvFileReader(inParams).read().peek(upStreamSpy::hit)
                    ).peek(downStreamSpy::hit)
            );
        }

        //TODO: move to assertNormalized
        symbolHistoryGapFiller.countPerTimeCheck();

        return assertNormalized(new BarFileMappingStat(upStreamSpy.getStat(processingParams.getTimeframe()), downStreamSpy.getStat(processingParams.getTimeframe())));
    }

    private long toMinutes(long time) {
        return time/60000;
    }

    private Optional<Date> getTargetEldestBarDate(MetaTraderTimeframe timeframe) {
        return switch (timeframe){
            //case W1 -> infoHardCoded(of(newUtcDate(1999, JANUARY, 1)), "targetOldestBarDate overridden");
            default -> targetEldestBarDate;
        };
    }

    private BarFileMappingStat assertNormalized(BarFileMappingStat barFileMappingStat) {
        final BarFileMappingStat.BarFileStat target = barFileMappingStat.getTarget();
        if(target.getBarCnt()==0)
            return barFileMappingStat;

        targetYoungestBarFence.map(date ->{ assert target.getLastBarDate().getTime()-date.getTime()==target.getTimeframe().getMs();
            return date;
        });

        return barFileMappingStat;
    }
}
