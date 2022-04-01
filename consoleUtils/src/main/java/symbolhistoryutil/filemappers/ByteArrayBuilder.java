package symbolhistoryutil.filemappers;

import lombok.Value;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static java.lang.String.format;
import static unsorted.StaticLogger.warn;

@Value
public class ByteArrayBuilder {
    ByteBuffer buffer;

    public ByteArrayBuilder(int targetSize) {
        buffer = ByteBuffer.allocate(targetSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public ByteArrayBuilder putInt(int i) {
        buffer.putInt(i);
        return this;
    }

    public void flushToStream(DataOutputStream out) throws IOException {
        out.write(buffer.array());
    }

    public ByteArrayBuilder putSized(int size, byte[] bytes) {
        if(bytes.length>size) {
            warn("bytes.length>size: " + String.valueOf(bytes));
        }
        buffer.put(Arrays.copyOf(bytes, size));
        return this;
    }

    public ByteArrayBuilder assertTargetSizeFilled() {
        assert buffer.position()== buffer.capacity() :
                format("buffer.position()==buffer.capacity() %d %d",
                        buffer.position(), buffer.capacity());
        return this;
    }

    public ByteArrayBuilder putDouble(double value) {
        buffer.putDouble(value);
        return this;
    }
}
