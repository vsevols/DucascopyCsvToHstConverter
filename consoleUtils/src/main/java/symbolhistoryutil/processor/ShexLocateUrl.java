package symbolhistoryutil.processor;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.net.URL;
import java.nio.file.Path;

import static java.lang.String.format;

@Value
@RequiredArgsConstructor
public class ShexLocateUrl {
    Path path;
    public ShexLocateUrl(String path) {
        this.path=Path.of(path);
    }

    /**
     * TODO: см. варианты в symbolhistoryutil.processor.ShexLocateUrlTest#testToString()
     */
    @Override
    public String toString() {
        return format("shex://%s?", path.toString());
    }
}
