package symbolhistoryutil.processor;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import symbolhistoryutil.dto.Bar;
import symbolhistoryutil.dto.MetaTraderTimeframe;
import unsorted.StaticLogger;

import java.util.Date;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Spliterator.*;
import static unsorted.StreamSupplier.of;

@Value
public class SymbolHistoryGapFiller {
    MetaTraderTimeframe timeframe;
    TargetBarCntJustification justification;
    @Getter(lazy = true)
    SpyConsumer spyConsumer =new SpyConsumer(timeframe);

    public Stream<Bar> process(Stream<Bar> in) {

        @Data
        class GapFiller implements BiConsumer<Bar, Consumer<Bar>>{
            @NonNull
            TargetBarCntJustification justification;
            Optional<Bar> prevBar =Optional.empty();

            @Override
            public void accept(Bar bar, Consumer<Bar> barConsumer) {
                processAccept(bar, barConsumer);
            }

            private void processAccept(Bar bar, Consumer<Bar> barConsumer) {

                //before source stream first bar: let's fill null-volume bars
                // since firstBarDate till first bar in source
                if(prevBar.isEmpty()&&bar.getDatetime()> getTargetFirstBarMoment()) {
                    getSpyConsumer().acceptInjected(
                            (prevBar = Optional.of(new Bar(new Date(getTargetFirstBarMoment()),
                                    bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose(), 0))).get(),
                            barConsumer
                    );
                }

                prevBar.ifPresent(prev -> {
                    long gapingCnt
                            =(bar.getDatetime()- prev.getDatetime())
                            /timeframe.getMs()
                            -1;
//                    StaticLogger.peekTrace((double)(bar.getDatetime()- prev.getDatetime())
//                            /timeframe.getMs()
//                            -1);
                    if(bar.getDatetime()- prev.getDatetime()!=timeframe.getMs())
                        StaticLogger.peekTrace(bar.getDatetime()- prev.getDatetime());

                    for (long i = 1; i <= gapingCnt; i++) {
                        getSpyConsumer().acceptInjected(new Bar(new Date(prev.getDatetime()+i*timeframe.getMs()),
                                prev.getClose(), prev.getClose(), prev.getClose(), prev.getClose(), 0
                                ), barConsumer);
                    }
                });

                getSpyConsumer().acceptOriginal(bar, barConsumer);
                prevBar =Optional.of(bar);
            }

            private long getTargetFirstBarMoment() {
                return getJustification().getTargetFirstBarDate().orElse(new Date(Long.MAX_VALUE)).getTime();
            }

            public Stream<Bar> getTailAppendingStream() {
                if(getJustification().getTargetLastBarDateFence().isEmpty())
                    return Stream.empty();

                return StreamSupport.stream(
                        new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, DISTINCT | NONNULL | IMMUTABLE) {
                            Optional<Spliterator<Bar>> generator=Optional.empty();

                            @Override
                            public boolean tryAdvance(Consumer<? super Bar> action) {
                                if (generator.isEmpty())
                                    generator = Optional.of(
                                            Stream.iterate(
                                                    getPrevBar().map(bar -> bar.withDatetime(bar.getDatetime()+timeframe.getMs())).orElse(
                                                            //заглушка: для пустого массива - пустой стрим
                                                            new Bar(getJustification().getTargetLastBarDateFence().get(),
                                                                    0, 0, 0, 0, 0)
                                                    ),
                                                    bar -> bar.getDatetime() < getJustification().getTargetLastBarDateFence().get().getTime(),
                                                    bar -> new Bar(new Date(bar.getDatetime() + timeframe.getMs()),
                                                            bar.getClose(), bar.getClose(), bar.getClose(), bar.getClose(), 0)
                                            ).spliterator()
                                    );

                                return generator.get().tryAdvance(new Consumer<Bar>() {
                                    @Override
                                    public void accept(Bar bar) {
                                        getSpyConsumer().acceptAppended(bar, action);
                                    }
                                });
                            }
                        }, false);

            }
        }

        final GapFiller gapFiller = new GapFiller(getJustification());
        return Stream.concat(
                //source order is notional
                in.sequential().mapMulti(gapFiller),
                gapFiller.getTailAppendingStream()
        );
    }

//    public BarFileMappingStat.BarFileStat getStat() {
//        return new BarFileMappingStat.BarFileStat(getSpyConsumer().getOrigCnt(),
//         getSpyConsumer().getCount(), getSpyConsumer().getFirstOriginalDate().orElse(new Date(0)));
//    }


    public void countPerTimeCheck() {
        final long last = getSpyConsumer().getLast().getDatetime();
        final long first = getSpyConsumer().getFirst().getDatetime();
        final long period = timeframe.getMs();
        final long count = getSpyConsumer().getCount();
        try {
            assert (last - first) / period == count - 1
                    : format("(last-first)/period==count-1 -> (%d-%d)/%d==%d-1 -> %d==%d",
                    last, first, period, count,
                    (last - first) / period, count - 1
            );
        }catch (Throwable e){
            throw new RuntimeException(format("outFile stat: first: %s, last: %s, frame: %s",
                    new Date(first), new Date(last), timeframe), e);
        }
    }
}
