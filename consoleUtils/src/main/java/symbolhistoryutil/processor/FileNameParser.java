package symbolhistoryutil.processor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import symbolhistoryutil.dto.MetaTraderTimeframe;

import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNameParser {
    private final String symbol;
    private final MetaTraderTimeframe timeframe;
    private final File file;

    public FileNameParser(File file) throws ParseException {
        this.file = file;
        final Matcher matcher = Pattern.compile("([\\D]+)([\\d]+)(\\.|$)").matcher(file.getName());
        if(!matcher.find())
            throw new ParseException(file.getName(), 0);
        symbol=matcher.group(1);
        timeframe = MetaTraderTimeframe.ofMinutes(Integer.parseInt(matcher.group(2)));
    }

    public FileNameParser(Path directory, String symbol, MetaTraderTimeframe timeframe) throws ParseException {
        this(new File(directory.resolve(symbol+String.valueOf(timeframe.getMinutes())).toString()));
    }

    public String resolveWithExtension(String extension) {
        String stripped = stripExtension();
        return stripped.concat(extension);
    }

    private String stripExtension() {
        int index = file.getName().lastIndexOf(".");
        if(index<0)
            return file.getName();
        String stripped = file.getName().substring(0, index);
        return stripped;
    }

    public FileNameParser withSymbolNameSuffix(String symbolNameSuffix) throws ParseException {
        return new FileNameParser(Path.of(getFile().getParent()), getSymbol().concat(symbolNameSuffix), getTimeframe());
    }
}
