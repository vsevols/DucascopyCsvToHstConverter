package unsorted;

import java.io.PrintStream;

import static java.lang.String.format;
import static java.lang.System.out;

/**
 * TODO: investigate Lightrun
 * Debugging Live Java Applications with Lightrun
 * Start for free
 * https://app.lightrun.com:8443/auth/realms/lightrun/protocol/openid-connect/registrations?client_id=web_app&response_type=code&redirect_uri=https://app.lightrun.com
 */
public class StaticLogger {
    public static void warn(Throwable e) {
        new Exception(e).printStackTrace();
    }

    public static <T> T peekTrace(T value) {
        trace(value);
        return value;
    }

    private static <T> void trace(Object value) {
        //TODO: if(isDebuggerPresent())
        //out.println("TRACE:\t"+value);
    }

    public static void warn(String s) {
        out.print("WARN:\t");
        new Exception(s).printStackTrace(new PrintStream(out));
    }

    public static void info(String s) {
        out.println("INFO:\t"+s);
    }

    public static void withDirectoryLogAppender(Runnable runnable) {
        try {
            //TODO: implement
            //try{.pushAppender();...}finally{popAppender()}
            //persp: move to ThreadCtx()
            runnable.run();
        } catch (Throwable t) {
            warn(t);
            throw new RuntimeException(t);
        }
    }

    public static void infoHardCoded(String msg) {
        infoHardCoded(msg, 1);
    }

    public static void infoHardCoded(String msg, int skipFramesCnt) {
        final StackWalker.StackFrame stackFrame = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stackFrameStream -> stackFrameStream.skip(skipFramesCnt).findFirst().orElseThrow());

        info(format("%s\nhardcoded at %s%s(%s:%d)",
                msg, stackFrame.getDeclaringClass(), stackFrame.getMethodName(), stackFrame.getFileName(), stackFrame.getLineNumber())
        );
    }

    public static <T> T infoHardCoded(T value, String msg) {
        infoHardCoded(msg+": "+value.toString(), 1);
        return value;
    }
}
