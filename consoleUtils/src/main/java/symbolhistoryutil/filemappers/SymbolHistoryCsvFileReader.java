package symbolhistoryutil.filemappers;

import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import symbolhistoryutil.dto.Bar;
import unsorted.SystemService;

import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static symbolhistoryutil.filemappers.KeyName.*;

@Value
public class SymbolHistoryCsvFileReader {
    public SymbolHistoryCsvFileReader(CsvFileParams params) {
        this.params = params;
    }

    @Delegate
    private CsvFileParams params;

    public interface KeyMapper {
        /**
         * TODO: move to HeaderlessKeyMapper
         */
        default String get(KeyName name){
            return switch (name){
                case DATE -> valueOf(0);
                case TIME -> valueOf(1);
                case OPEN -> valueOf(2);
                case HIGH -> valueOf(3);
                case LOW -> valueOf(4);
                case CLOSE -> valueOf(5);
                case VOLUME -> valueOf(6);
            };
        }

        default String getDatePattern(){
            return "yyyy.MM.dd";
        };

        default String getTimePattern(){
            return "HH:mm";
        }
    }
    public Stream<Bar> read() {
        return StreamSupport.stream(
                        new CsvSpliterator(params.getFileName(), params.isHasHeaders(), params.getSepChar()), false
                )
                .skip(getParams().isHasHeaders()?1:0)
                .map(this::mapRow);
    }

    private Bar mapRow(Map<String, String> fieldMap) {
        return new Bar(
                mapDate(mapRowFieldValue(DATE, fieldMap), mapRowFieldValue(TIME, fieldMap),
                        getKeyMapper().getDatePattern(), getKeyMapper().getTimePattern()),
                dbl(mapRowFieldValue(OPEN, fieldMap)),
                dbl(mapRowFieldValue(HIGH, fieldMap)),
                dbl(mapRowFieldValue(LOW, fieldMap)),
                dbl(mapRowFieldValue(CLOSE, fieldMap)),
                dbl(mapRowFieldValue(VOLUME, fieldMap))
        );
    }

    private String mapRowFieldValue(KeyName keyName, Map<String, String> fieldMap) {
        final String key = getKeyMapper().get(keyName);
        return isNull(key)?"":requireNonNull(fieldMap.get(key));
    }

    private double dbl(String s) {
        return Double.parseDouble(s);
    }

    @SneakyThrows
    private Date mapDate(String date, String time, String datePattern, String timePattern) {
        return new Date(SystemService.parseUtcDatetime(date+time, datePattern+timePattern));
    }

    @Value
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CsvFileParams {
        String fileName;
        boolean hasHeaders;
        char sepChar;
        KeyMapper keyMapper;

        public CsvFileParams(String absolutePath, boolean hasHeaders, char sepChar) {
            this(absolutePath, hasHeaders, sepChar, new KeyMapper() {});
        }
    }
}
