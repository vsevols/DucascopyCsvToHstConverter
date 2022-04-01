package unsorted;

import java.io.IOException;

import static unsorted.SystemService.throwNotImplemented;

public class Unsorted {
    public static void processException(Throwable e) {
        StaticLogger.warn(e);
    }
//    public static <T extends Throwable> void processAndRethrow(T e) {
//        StaticLogger.warn(e);
//    }
}
