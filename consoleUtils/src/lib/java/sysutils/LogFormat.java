package sysutils;

import sysutils.beanutils.OGetter;

import static java.lang.String.format;
import static sysutils.beanutils.BeanUtils.convertToFieldName;

public class LogFormat {
    public static String formatValues(String comment, OGetter... getters) {
        String result=comment+"\n";
        for (OGetter getter : getters) {
            result+=format(" %s: %s", convertToFieldName(getter), getter.call());
        }
        return result;
    }
}
