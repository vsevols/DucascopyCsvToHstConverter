package symbolhistoryutil.dto;

import lombok.Getter;
import unsorted.SystemService;

import java.util.Arrays;

public enum MetaTraderTimeframe {
    CURRENT(0),
    M1(minutesToMs(1)),
    M5(minutesToMs(5)),
    M15(minutesToMs(15)),
    M30(minutesToMs(30)),
    H1(minutesToMs(60)),
    H4(minutesToMs(240)),
    D1(minutesToMs(1440)),
    W1(minutesToMs(10080)),
    MN1(minutesToMs(43200));

    @Getter
    private final long ms;

    MetaTraderTimeframe(long ms) {
        this.ms=ms;
    }

    private static long minutesToMs(int minutes) {
        return minutes*60*1000L;
    }

    public static MetaTraderTimeframe ofMinutes(int minutes) {
        return Arrays.stream(MetaTraderTimeframe.values())
                .filter(metaTraderTimeframe -> metaTraderTimeframe.getMinutes()==minutes)
                .findAny().orElseThrow();
    }

    public int getMinutes() {
        return Math.toIntExact(getMs() / 60000);
    }
}
