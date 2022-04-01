package symbolhistoryutil.converters;

import lombok.Value;
import symbolhistoryutil.converters.ducascopy.DucascopyFileNameParser;
import symbolhistoryutil.filemappers.HstFileWriter;
import symbolhistoryutil.filemappers.KeyName;
import symbolhistoryutil.filemappers.SymbolHistoryCsvFileReader;
import symbolhistoryutil.processor.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static java.lang.String.format;
import static unsorted.StaticLogger.info;
import static unsorted.StaticLogger.withDirectoryLogAppender;
import static unsorted.SystemService.illegalState;
import static unsorted.Unsorted.processException;

@Value
public class DucascopyCsvToHstDirectoryConverter {
    private static final String TARGET_SYMBOL_NAME_SUFFIX = "_duc";
    DirectoryConverterParams params;
    public void process() {
        final File srcDir = new File(getParams().getSrc().toString());
        final Path targetDirPath = Path.of(getParams().getTarget().toString());
        //TODO: withFileLogAppender(targetHstFile+".log", ...)
        withDirectoryLogAppender(()->doProcess(srcDir, targetDirPath));
    }

    private void doProcess(File srcDir, Path targetDirPath) {
        info(format("processing files... \n%s\n->\n%s",
                new ShexLocateUrl(srcDir.getAbsolutePath()), new ShexLocateUrl(targetDirPath)
        ));

        File[] arrFiles = srcDir.listFiles();
        assert arrFiles != null: "can't access srcDir: "+ srcDir.getAbsolutePath();
        Arrays.stream(arrFiles)
                .filter(File::isFile)
                .forEach(file -> {
                    try {
                        info(file.getName());
                        BarFileMappingStat barFileMappingStat=
                                processFile(file, targetDirPath);

                        info(barFileMappingStat.toString());
                    } catch (IOException | ParseException | RuntimeException e) {
                        processException(new Exception(
                                format("while processing file %s->%s", file, targetDirPath),
                                e
                        ));
                    }
                });
        info(format("processing files: finished"));
    }

    private BarFileMappingStat processFile(File file, Path outDirPath) throws IOException, ParseException {

        final DucascopyFileNameParser fileNameParser = new DucascopyFileNameParser(file, DucascopyCsvToHstDirectoryConverter.TARGET_SYMBOL_NAME_SUFFIX);
        return new SymbolHistoryGapFillerFileProcessor(
                new SymbolHistoryCsvFileReader.CsvFileParams(
                        file.getAbsolutePath(),
                        true, ',',
                        new SymbolHistoryCsvFileReader.KeyMapper() {
                            @Override
                            public String get(KeyName name) {
                                return switch (name){
                                    case DATE -> "Gmt time";
                                    case TIME -> null;
                                    case OPEN -> "Open";
                                    case HIGH -> "High";
                                    case LOW -> "Low";
                                    case CLOSE -> "Close";
                                    case VOLUME -> "Volume";
                                };
                            }

                            @Override
                            public String getDatePattern() {
                                return "dd.MM.yyyy HH:mm:ss.SSS";
                            }

                            @Override
                            public String getTimePattern() {
                                return "";
                            }
                        }
                ),
                new ProcessingParams(new HstFileWriter.HstFileParams(
                        outDirPath.resolve(fileNameParser.resolveTargetHstFileName()).toString(),
                        fileNameParser.resolveTargetSymbolName(),
                        fileNameParser.getTimeframe(),
                        5, //TODO: persp: обновлять после записи всех баров
                        new Date().getTime(),
                        new Date().getTime() //TODO: persp: обновлять при дописывании
                ))
        ).process();
    }
}
