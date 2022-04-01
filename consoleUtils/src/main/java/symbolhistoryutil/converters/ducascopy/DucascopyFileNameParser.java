package symbolhistoryutil.converters.ducascopy;

import lombok.Value;
import symbolhistoryutil.dto.MetaTraderTimeframe;

import java.io.File;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static unsorted.SystemService.illegalState;

@Value
public class DucascopyFileNameParser {
    File file;
    /**
     * 12 symbols max: name+"_"+suffix
     */
    String targetSymbolNameSuffix;
    private final String srcSymbolName;
    private final MetaTraderTimeframe timeframe;

    public DucascopyFileNameParser(File file, String targetSymbolNameSuffix) throws ParseException {
        this.file = file;
        this.targetSymbolNameSuffix = targetSymbolNameSuffix;

        //https://regexr.com/6fnph
        final Matcher matcher = Pattern.compile("([\\D]+)_Candlestick_([\\d]+)_([\\D]+?)_").matcher(file.getName());
        if(!matcher.find())
            throw new ParseException(file.getName(), 0);
        srcSymbolName=matcher.group(1);
        timeframe = resolveTimeframe(matcher.group(2), matcher.group(3));
    }

    private MetaTraderTimeframe resolveTimeframe(String count, String periodMultiplier) {
        return MetaTraderTimeframe.ofMinutes(
                Integer.parseInt(count)*(Integer) switch (periodMultiplier) {
                    case "M" -> 1;
                    case "H" -> 60;
                    case "D" -> 24 * 60;
                    case "W" -> 24 * 60 * 7;
                    case "MN" -> 24 * 60 * 30;
                    default -> illegalState(periodMultiplier);
        });
    }

    public String resolveTargetHstFileName() {
        return format("%s%d.hst", resolveTargetSymbolName(), getTimeframe().getMinutes());
    }

    public String resolveTargetSymbolName() {
        return getSrcSymbolName()+getTargetSymbolNameSuffix();
    }
}
