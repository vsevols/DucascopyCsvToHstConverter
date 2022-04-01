package unsorted;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static unsorted.Unsorted.processException;

public class SystemService {
    public static String promptString(String prompt) {
        System.out.print(prompt+ System.lineSeparator());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        try {
            str = reader.readLine();
        } catch (IOException e) {
            processException(e);
        }
        return str;
    }

    public static LocalDateTime unixTimeToLocalDateTime(int unixTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(
                unixTime),
                TimeZone.getDefault().toZoneId()
        );
    }

    public static <RETURN_IMPOSSIBLE> RETURN_IMPOSSIBLE unsupportedOperation(Object object) {
        throw new UnsupportedOperationException(String.valueOf(object));
    }

    public static void writeFile(String fileName, String content) {
        writeFile(fileName, content, false);
    }

    public static void appendFile(String fileName, String content) {
        writeFile(fileName, content, true);
    }

    public static void writeFile(String fileName, String content, boolean append) {
        try {
            Files.createDirectories(Path.of(fileName).getParent());
        } catch (IOException ioException) {
            processException(ioException);
        }

        try(FileWriter writer = new FileWriter(fileName, append))
        {
            writer.write(content);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }

    public static <RETURN_IMPOSSIBLE> RETURN_IMPOSSIBLE unsupportedOperation() {
        return unsupportedOperation(null);
    }

    public static <RETURN_IMPOSSIBLE> RETURN_IMPOSSIBLE throwNotImplemented() {
        return unsupportedOperation();
    }
    public static <IMPOSSIBLE_RETURN> IMPOSSIBLE_RETURN illegalState(Object value) {
        throw new IllegalStateException("Unexpected value: " + value);
    }

    public static long parseUtcDatetime(String source, String pattern) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return dateFormat.parse(source).getTime();
    }

    /**
     * TODO: создать Enum-обёртку к константам месяцев Calendar.XXXX , !!!т.к. они zero-based
     * или юзать какую-то библиотеку из этих https://stackoverflow.com/questions/16499228/creating-java-date-object-from-year-month-day
     */
    public static Date newUtcDate(int year, int month, int day) {
        return newUtcDate(year, month, day, 0,0);
    }

    /**
     * TODO: создать Enum-обёртку к константам месяцев Calendar.XXXX , !!!т.к. они zero-based
     * или юзать какую-то библиотеку из этих https://stackoverflow.com/questions/16499228/creating-java-date-object-from-year-month-day
     */
    public static Date newUtcDate(int year, int month, int day, int hourOfDay, int minute) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
        calendar.set(year, month, day, hourOfDay, minute);
        final long TRUNCATE_PERIOD = 1000 * 60;
        return new Date(calendar.getTime().getTime()/ TRUNCATE_PERIOD * TRUNCATE_PERIOD);
    }
}
