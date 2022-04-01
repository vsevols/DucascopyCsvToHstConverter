package symbolhistoryutil.processor;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;

class ShexLocateUrlTest {

    @Test
    void testToString() throws MalformedURLException {
        //idea debug output console clickability test
        out.println(new ShexLocateUrl("d:/tmp")); //not clickable
        //out.println(new URL("shex://asdf.com")); //MalformedURLException: unknown protocol: shex
        out.println(new URL("http://asdf.com")); //clickable
        out.println("ws:///asdf.com"); //not clickable
        out.println("tg://asdf.com"); //not clickable
        out.println("ftp://asdf.com"); //clickable
        out.println("sh://asdf.com"); //not clickable
        out.println("ssh://asdf.com"); //not clickable
        out.println("ftp://shex://d:/tmp?loc"); //clickable
        //TODO: остаётся тогда такой варинат:
        // http://shex/
        // либо ftp://shex/
        // меньше букв и реже используется. Можно подвесить свой прокси-обработчик на протокол ftp-клиентского обработчика
        // либо - плагин к intellij, если возможно


        out.println("http://shex://d:/tmp?"); //отбрасывает вопросительный знак и двоеточие после shex
        out.println("http://shex://d:/tmp&"); //отбрасывает двоеточие после shex
    }
}