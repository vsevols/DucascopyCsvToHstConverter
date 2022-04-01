package symbolhistoryutil.processor;

import lombok.Value;
import lombok.experimental.Delegate;
import symbolhistoryutil.filemappers.HstFileWriter;

@Value
public class ProcessingParams {
    @Delegate
    HstFileWriter.HstFileParams hstFileParams;
}
