package symbolhistoryutil.filemappers;

import org.junit.jupiter.api.Test;
import symbolhistoryutil.dto.Bar;
import symbolhistoryutil.dto.MetaTraderTimeframe;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static java.util.Calendar.*;
import static unsorted.SystemService.newUtcDate;

class HstFileWriterTest {

    @Test
    void setMT4_H1USDRUB_ctf_scratch() throws IOException {
        try(HstFileWriter writer = new HstFileWriter(newHstFileParams())) {
            writer.write(
                    Stream.iterate(
                            newTestBar(newUtcDate(2011, OCTOBER, 01)),
                            bar -> bar.getDatetime()<newUtcDate(2021, OCTOBER, 01).getTime(),
                            bar -> newTestBar(incHour(bar.getDate()))
                            )
            );
        }
    }

    private Date incHour(Date date) {
        return new Date(date.getTime()+60*60000);
    }

    private Bar newTestBar(Date date) {
        return new Bar(date, 50, 51, 50, 51, 0);
    }

    private Stream<Bar> getH1DayStream() {
        return Stream.of(
                newTestBar(AUGUST, 13, 0),
                newTestBar(AUGUST, 13, 1),
                newTestBar(AUGUST, 13, 2),
                newTestBar(AUGUST, 13, 3),
                newTestBar(AUGUST, 13, 4),
                newTestBar(AUGUST, 13, 5),
                newTestBar(AUGUST, 13, 6),
                newTestBar(AUGUST, 13, 7),
                newTestBar(AUGUST, 13, 8),
                newTestBar(AUGUST, 13, 10),
                newTestBar(AUGUST, 13, 11),
                newTestBar(AUGUST, 13, 12),
                newTestBar(AUGUST, 13, 13),
                newTestBar(AUGUST, 13, 14),
                newTestBar(AUGUST, 13, 15),
                newTestBar(AUGUST, 13, 16),
                newTestBar(AUGUST, 13, 17),
                newTestBar(AUGUST, 13, 18),
                newTestBar(AUGUST, 13, 19),
                newTestBar(AUGUST, 13, 20),
                newTestBar(AUGUST, 13, 21),
                newTestBar(AUGUST, 13, 22),
                newTestBar(AUGUST, 13, 23)
                );
    }

    private Bar newTestBar(int month, int day, int hourOfDay) {
        return new Bar(newUtcDate(2016, month, day, hourOfDay, 0), 50, 51, 50, 51, 0);
    }

    private HstFileWriter.HstFileParams newHstFileParams() {
        return new HstFileWriter.HstFileParams(
                "c:\\Users\\Vsev\\AppData\\Roaming\\MetaQuotes\\Terminal\\EFA2FA992221F78E012406EB76D6B6BC\\history\\RoboForex-Demo\\USDRUB_ctf60.hst",
                "USDRUB_ctf",
                MetaTraderTimeframe.H1, 8,
                0,
                0
        );
    }
}