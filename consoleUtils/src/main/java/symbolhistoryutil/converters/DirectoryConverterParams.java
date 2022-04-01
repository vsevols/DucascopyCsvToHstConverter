package symbolhistoryutil.converters;

import lombok.Value;

import java.nio.file.Path;

@Value
public class DirectoryConverterParams {
    Path src;
    Path target;
}
