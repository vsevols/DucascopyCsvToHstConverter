package symbolhistoryutil.processor;

import lombok.Data;
import lombok.NonNull;
import symbolhistoryutil.dto.Bar;
import symbolhistoryutil.dto.MetaTraderTimeframe;
import sysutils.ControlFlow;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import static sysutils.LogFormat.formatValues;

@Data
class SpyConsumer {// implements BiConsumer<Bar, Consumer<Bar>>{
    Bar first;
    Bar last;
    long count;
    long addedCnt;
    Optional<Date> firstOriginalDate = Optional.empty();
    Optional<Date> firstInjectedDate = Optional.empty();
    @NonNull
    MetaTraderTimeframe timeframe;

    private void accept(Bar bar, Consumer<? super Bar> barConsumer) {
        ControlFlow.assertInfo(last == null || bar.getDatetime() - last.getDatetime() == timeframe.getMs(),
                ()->formatValues(
                "bar.getDatetime() - last.getDatetime() == timeframe.getMs()",
                bar::getDate, last::getDate, this::getTimeframe)
        );
        barConsumer.accept(bar);
        first = first != null ? first : bar;
        last = bar;
        count++;
    }

    public void acceptInjected(Bar bar, Consumer<Bar> barConsumer) {
        firstInjectedDate = Optional.of(firstOriginalDate.orElse(bar.getDate()));
        addedCnt++;
        accept(bar, barConsumer);
    }

    public long getOrigCnt() {
        return getCount() - getAddedCnt();
    }

    public void acceptOriginal(Bar bar, Consumer<Bar> barConsumer) {
        firstOriginalDate = Optional.of(firstOriginalDate.orElse(bar.getDate()));
        accept(bar, barConsumer);
    }

    public void acceptAppended(Bar bar, Consumer<? super Bar> action) {
        addedCnt++;
        accept(bar, action);
    }
}
