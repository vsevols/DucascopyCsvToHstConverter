import symbolhistoryutil.processor.SymbolHistoryGapFillerDirectoryProcessor;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args){
        //TODO: вызов тулзов отсюда. Внутри-запрос имени файла
        // здесь - выводить список тулзов и порядковый номер
        if(args.length<2)
            throw new IllegalArgumentException(
                    "expected cmdLine args:" +
                    "\nabsolute path to source directory containing .csv files" +
                    "\nstandaloneTargetDirPath (like C:\\Users\\Vsev\\AppData\\Roaming\\MetaQuotes\\Terminal\\EFA2FA992221F78E012406EB76D6B6BC\\history\\RoboForex-Demo )");
        new SymbolHistoryGapFillerDirectoryProcessor().process(Path.of(args[0]), Path.of(args[1]));
    }
}
