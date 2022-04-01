package sysutils.beanutils;

import java.io.Serializable;

public interface OGetter<R> extends Serializable {
    R call();
}
