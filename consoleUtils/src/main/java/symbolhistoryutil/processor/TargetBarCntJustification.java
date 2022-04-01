package symbolhistoryutil.processor;

import lombok.Value;

import java.util.Date;
import java.util.Optional;

@Value
public class TargetBarCntJustification {
    Optional<Date> targetFirstBarDate;
    Optional<Date> targetLastBarDateFence;
}
