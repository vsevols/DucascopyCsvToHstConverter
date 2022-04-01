package unsorted;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Unmodifiable (incl. self-unmodifiable) list
 */
@Value
public class StreamSupplier<T> implements Supplier<Stream<T>> {
    @Getter(AccessLevel.PRIVATE)
    List<T> list;
    public static <T> StreamSupplier<T> of(T... values) {
        return of(Stream.of(values));
    }

    public static <T> StreamSupplier<T> of(Stream<T> stream) {
        return new StreamSupplier<>(stream.collect(java.util.stream.Collectors.toList()));
    }

    @Override
    public Stream<T> get() {
        return list.stream();
    }
}
