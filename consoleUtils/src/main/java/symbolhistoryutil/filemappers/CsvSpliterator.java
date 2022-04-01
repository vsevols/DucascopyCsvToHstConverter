package symbolhistoryutil.filemappers;


import com.csvreader.CsvReader;
import unsorted.WithSl4jLogAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class CsvSpliterator extends WithSl4jLogAdapter implements Spliterator<Map<String,String>>
{
    private CsvReader reader;

    private final boolean hasHeaders;

    private final char sepChar;

    private String[] headers;


    /**
     * Primary constructor
     *
     * @param fileName
     * @param hasHeaders
     * @param sepChar
     */

    public CsvSpliterator(String fileName, boolean hasHeaders, char sepChar)
    {
        this.hasHeaders=hasHeaders;
        this.sepChar=sepChar;
        try
        {
            reader = new CsvReader(fileName);
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalArgumentException("Unable to find the CSV file", e);
        }

        /**
         * Read headers if necessary.
         */

        if (hasHeaders)
        {
            try
            {
                reader.readHeaders();
                headers = reader.getHeaders();

                StringJoiner joiner = new StringJoiner(",");
                Arrays.stream(headers).forEach(x -> joiner.add(x));

                log.debug("CSV Header : {}", joiner.toString());
            }
            catch (IOException e)
            {
                throw new IllegalArgumentException("Unable to read the CSV headers", e);
            }
        }
    }

    @Override
    public int characteristics()
    {
        return DISTINCT | NONNULL | IMMUTABLE;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Map<String, String>> action)
    {
        boolean hasRecord = false;

        try
        {
            hasRecord = reader.readRecord();

            if (hasRecord)
            {
                action.accept(load());
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }

        return hasRecord;
    }

    @Override
    public Spliterator<Map<String, String>> trySplit()
    {
        return null;
    }

    @Override
    public long estimateSize()
    {
        return Long.MAX_VALUE;
    }


    /**
     *  Load a row of a CSV as a Map<String,String>
     */

    private Map<String,String> load()
    {
        long recordNo = reader.getCurrentRecord();
        int colCount = reader.getColumnCount();

        log.trace("Reading CSV record {}", recordNo);

        Map<String,String> map = new HashMap<>();

        if (hasHeaders)
        {
            /**
             * If it has headers create a map of header -> value
             */

            if ( colCount != headers.length )
            {
                log.warn("CSV Record {} has {} columns and we expected {}", recordNo, colCount, headers.length);
            }

            /**
             * Read all the existing header elements
             */

            try
            {
                for (String colName : headers)
                {
                    map.put(colName, reader.get(colName));
                }
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Problem reading the content of record " + recordNo, e);
            }

        }
        else
        {

            try
            {
                for (int i = 0; i < colCount; i++)
                {
                    map.put(Integer.toUnsignedString(i), reader.get(i));
                }

            }
            catch (IOException e)
            {
                throw new IllegalStateException("Problem reading the content of record " + recordNo, e);
            }

        }

        return map;
    }



}