package unsorted;

import static unsorted.SystemService.throwNotImplemented;

public class WithSl4jLogAdapter {
    protected Log log=new Log();

    public class Log{
        public void debug(String s, String toString) {
            //TODO: implement
        }

        public void trace(String s, long recordNo) {
            //TODO: implement
        }

        public void warn(String s, long recordNo, int colCount, int length) {
            //TODO: implement
            throwNotImplemented();
        }
    }
}
