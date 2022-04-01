package symbolhistoryutil.filemappers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import symbolhistoryutil.dto.Bar;
import lombok.Value;
import symbolhistoryutil.dto.MetaTraderTimeframe;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static unsorted.SystemService.throwNotImplemented;


@Value
@RequiredArgsConstructor
public class HstFileWriter implements AutoCloseable{
    @NonFinal
    private DataOutputStream outputStream;

    @Override
    public void close() throws IOException {
        getOutputStream().close();
    }

    @Value
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static
    class HstFileParams{
        String path;
        String symbolName;
        MetaTraderTimeframe timeframe;
        int digits;
        /**
         * Time of sign (database creation)
         */
        long timesign;
        /**
         * Time of last synchronization
         */
        long lastSync;
        String copyright="Copyright © 2016-2022, ASPTO";
    };
    HstFileParams params;
    public void write(Stream<Bar> stream) throws IOException {
        Files.createDirectories(Path.of(getParams().getPath()).getParent());
        outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(getParams().getPath())));
        writeHeader400(outputStream);
        stream.forEachOrdered(bar -> writeBar400(bar, outputStream));
    }

    private void writeHeader400(DataOutputStream out) throws IOException {

        //TODO persp: write to array, then assertSize(148) and flush

//      https://www.mql5.com/en/forum/149178
//        .hst file format valid as of MT4 Build 509
//        The database header first . . . total 148 bytes
        new ByteArrayBuilder(148)
//        int	version;	// database version - 400	4 bytes
                .putInt(400)
//        string	copyright[64];	// copyright info	64 bytes
                .putSized(64, toWin1251Bytes(getParams().getCopyright()))
//        string	symbol[12];	// symbol name	12 bytes
                .putSized(12, toWin1251Bytes(getParams().getSymbolName()))
//        int	period;	// symbol timeframe	4 bytes
                .putInt(getParams().getTimeframe().getMinutes())
//        int	digits;	// the amount of digits after decimal point	4 bytes
                .putInt(getParams().getDigits())
//        datetime	timesign;	// timesign of the database creation	4 bytes
                .putInt(intTime(getParams().getTimesign()))
//        datetime	last_sync;	// the last synchronization time	4 bytes
                .putInt(intTime(getParams().getLastSync()))
//        int	unused[13];	// to be used in future	52 bytes
                .putSized(52, toWin1251Bytes(""))
                .assertTargetSizeFilled()
                .flushToStream(out);
    }

    private int intTime(long time) {
        return Math.toIntExact(time/1000);
    }

    @SneakyThrows
    private void writeBar400(Bar bar, DataOutputStream out) {
//        then the bars array (single-byte justification) . . . total 44 bytes
        new ByteArrayBuilder(44)
//        datetime	ctm;	// bar start time	4 bytes
                .putInt(intTime(bar.getDate().getTime()))
//        double	open;	// open price	8 bytes
                .putDouble(bar.getOpen())
//        double	low;	// lowest price	8 bytes
                .putDouble(bar.getLow())
//        double	high;	// highest price	8 bytes
                .putDouble(bar.getHigh())
//        double	close;	// close price	8 bytes
                .putDouble(bar.getClose())
//        double	volume;	// tick count	8 bytes
                .putDouble(bar.getVolume())
                .assertTargetSizeFilled()
                .flushToStream(out);
    }

    private byte[] toWin1251Bytes(String s) {
        return s.getBytes(Charset.forName("windows-1251"));
    }


    private void writeHeader401(DataOutputStream out) {
//        .hst file format valid as of MT4 574 and later
//
//        The database header is the same . . . total 148 bytes
//
//        int	version;	// database version - 401	4 bytes
//        string	copyright[64];	// copyright info	64 bytes
//        string	symbol[12];	// symbol name	12 bytes
//        int	period;	// symbol timeframe	4 bytes
//        int	digits;	// the amount of digits after decimal point	4 bytes
//        datetime	timesign;	// timesign of the database creation	4 bytes
//        datetime	last_sync;	// the last synchronization time	4 bytes
//        int	unused[13];	// to be used in future	52 bytes
        //TODO: implement
        throwNotImplemented();
    }

    private void writeBar401(Bar bar, DataOutputStream out) {
//        then the bars array (single-byte justification) . . . total 60 bytes
//
//        datetime	ctm;	// bar start time	8 bytes
//        double	open;	// open price	8 bytes
//        double	high;	// highest price	8 bytes
//        double	low;	// lowest price	8 bytes
//        double	close;	// close price	8 bytes
//        long	volume;	// tick count	8 bytes
//        int	spread;	// spread	4 bytes
//        long	real_volume;	// real volume	8 bytes


        //TODO: implement
        throwNotImplemented();
    }
}
