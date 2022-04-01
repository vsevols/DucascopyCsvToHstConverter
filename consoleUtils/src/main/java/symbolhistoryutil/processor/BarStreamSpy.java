package symbolhistoryutil.processor;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import symbolhistoryutil.dto.Bar;
import symbolhistoryutil.dto.MetaTraderTimeframe;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.Optional.*;

@Value
@RequiredArgsConstructor
public class BarStreamSpy {
    Optional<Consumer<Bar>> callback;
    ConcurrentLinkedQueue<Bar> hits=new ConcurrentLinkedQueue<>();

    public BarStreamSpy(Consumer<Bar> callback) {
        this(of(callback));
    }

    public BarStreamSpy() {
        this(empty());
    }

    public void hit(Bar bar) {
        callback.ifPresent(barConsumer -> barConsumer.accept(bar));
        hits.add(bar);
    }

    public BarFileMappingStat.BarFileStat getStat(MetaTraderTimeframe timeframe) {
        if(0==getHits().size())
            return new BarFileMappingStat.BarFileStat(0, new Date(0), new Date(0), timeframe);

        return new BarFileMappingStat.BarFileStat(getHits().size(),
                ofNullable(getHits().peek()).map(Bar::getDate).orElse(new Date(0)),
                getHits().stream().skip(getHits().size()-1).map(Bar::getDate).findAny().orElse(new Date(0))
                , timeframe
        );
    }
}
