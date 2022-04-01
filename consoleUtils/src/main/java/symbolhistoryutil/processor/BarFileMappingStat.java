package symbolhistoryutil.processor;

import lombok.Value;
import lombok.With;
import lombok.experimental.Wither;
import symbolhistoryutil.dto.MetaTraderTimeframe;

import java.util.Date;

/**
 * TODO: finalFileSize
 */
@With
@Value
public class BarFileMappingStat {
    BarFileStat src;
    BarFileStat target;

    @Value
    static
    class BarFileStat {
        long barCnt;
        Date firstBarDate;
        Date lastBarDate;
        MetaTraderTimeframe timeframe;
    }
}
