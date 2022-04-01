package symbolhistoryutil.processor;

import symbolhistoryutil.filemappers.HstFileWriter;
import symbolhistoryutil.filemappers.SymbolHistoryCsvFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static java.lang.String.format;
import static symbolhistoryutil.processor.NewHeaderDateStrategy.NOW;
import static symbolhistoryutil.processor.NewHeaderDateStrategy.FUTURE;
import static unsorted.StaticLogger.info;
import static unsorted.StaticLogger.withDirectoryLogAppender;
import static unsorted.Unsorted.processException;

public class SymbolHistoryGapFillerDirectoryProcessor {
    public void process(Path srcDirPath, Path standaloneTargetDir){
        final File srcDir = new File(srcDirPath.toString());
        final Path targetDirPath = Path.of(srcDir.getAbsolutePath()).resolveSibling("out");
        withDirectoryLogAppender(()->doProcess(srcDir, targetDirPath, standaloneTargetDir));
    }

    private void doProcess(File srcDir, Path targetDirPath, Path standaloneTargetDirPath) {
        info(format("processing files... \n%s\n->\n%s\n%s",
                new ShexLocateUrl(srcDir.getAbsolutePath()), new ShexLocateUrl(targetDirPath), new ShexLocateUrl(standaloneTargetDirPath))
        );
        File[] arrFiles = srcDir.listFiles();
        assert arrFiles != null: "can't access inDirPath: "+ srcDir.getAbsolutePath();
        Arrays.stream(arrFiles)
                .filter(File::isFile)
                .forEach(file -> {
                    try {
                        info(file.getName());
//                        BarFileMappingStat targetDirMappingStat =
//                                processFile(file, targetDirPath.resolve("nowHeader"), NOW);
//                        processFile(file, targetDirPath.resolve("futureHeader"), FUTURE);
                        BarFileMappingStat standaloneFileMappingStat=
                                processFile(file, standaloneTargetDirPath, NOW, "_ctf");

                        //info("targetDirMappingStat "+targetDirMappingStat.toString());
                        info("standalone "+standaloneFileMappingStat.toString());
                    } catch (IOException | ParseException | RuntimeException e) {
                        processException(new Exception(
                                format("exception while processing %s->%s", file, Path.of(srcDir.getParent(), "out")),
                                e
                        ));
                    }
                });
        info(format("processing files: finished"));
    }

    private BarFileMappingStat processFile(File file, Path outDirPath, NewHeaderDateStrategy newHeaderDateStrategy) throws IOException, ParseException {
        return processFile(file, outDirPath, newHeaderDateStrategy, "");
    }

    private BarFileMappingStat processFile(File file, Path outDirPath, NewHeaderDateStrategy newHeaderDateStrategy, String symbolNameSuffix) throws IOException, ParseException {

        final FileNameParser fileNameParser = new FileNameParser(file).withSymbolNameSuffix(symbolNameSuffix);
        return new SymbolHistoryGapFillerFileProcessor(
                new SymbolHistoryCsvFileReader.CsvFileParams(
                        //shex://d:\\projectData\\ASPTO2022\\unsorted\\in\\USDRUB1440.csv
                        file.getAbsolutePath(),
                        false, ','
                ),
                new ProcessingParams(new HstFileWriter.HstFileParams(
                        outDirPath.resolve(fileNameParser.resolveWithExtension(".hst")).toString(),
                        fileNameParser.getSymbol(),
                        fileNameParser.getTimeframe(), 5,
                        //иначе - МТ4 удаляет заполненные гэпы влоть до -(4+) года
                        getNewHeaderDate(newHeaderDateStrategy),
                        getNewHeaderDate(newHeaderDateStrategy)
                        ))
        ).process();
    }

    private long getNewHeaderDate(NewHeaderDateStrategy newHeaderDateStrategy) {
        return switch (newHeaderDateStrategy) {
            case NOW -> new Date().getTime();
            case FUTURE -> Integer.MAX_VALUE* 1000L;
        };
    }
}
