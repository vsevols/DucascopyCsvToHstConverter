package sysutils.beanutils;

import java.io.Serializable;

/**
 *Getter method interface definition
 */
public interface SGetter<T> extends Serializable {
    Object apply(T source);
}
