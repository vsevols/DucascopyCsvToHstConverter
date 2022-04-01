package sysutils;

import unsorted.StaticLogger;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static unsorted.StaticLogger.info;

public class ControlFlow {
    public static void assertInfo(boolean condition, Supplier<String> descriptionSupplier) {
        if(!condition)
            info(descriptionSupplier.get());
    }
}
