package symbolhistoryutil.converters;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static java.nio.file.Path.*;

import static unsorted.StaticLogger.info;

class DucascopyCsvToHstDirectoryConverterTest {
    @Test
    void scratch(){
        new DucascopyCsvToHstDirectoryConverter(
                newDirectoryConverterParams(DucascopyCsvToHstDirectoryConverter.class)
        ).process();
    }


    @Test
    void scratchOffsetDateTimeToString(){
        info(OffsetDateTime.of(LocalDateTime.of(2021, Month.OCTOBER, 1, 15, 25), ZoneOffset.UTC).toString());
    }

    private DirectoryConverterParams newDirectoryConverterParams(Class<?> converterClass) {
        //TODO: parametrize by converterClass.getName()
        return new DirectoryConverterParams(
                of("d:\\projectData\\ASPTO2022\\consoleUtils\\DucascopyCsvToHstDirectoryConverter\\src"),
                of("c:\\Users\\Vsev\\AppData\\Roaming\\MetaQuotes\\Terminal\\EFA2FA992221F78E012406EB76D6B6BC\\history\\RoboForex-Demo\\")
        );
    }

}