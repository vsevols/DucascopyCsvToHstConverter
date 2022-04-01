package sysutils.beanutils;

import java.io.Serializable;

/**
 * Setter method interface definition
 */
@FunctionalInterface
public interface SSetter<T, U> extends Serializable {
    void accept(T t, U u);
}
