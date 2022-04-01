package sysutils.beanutils;

import java.io.Serializable;

public interface OSetter<V> extends Serializable {
    void accept(V value);
}
