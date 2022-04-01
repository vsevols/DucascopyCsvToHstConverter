package sysutils.beanutils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://developpaper.com/using-lambda-to-get-the-property-name-through-getter-setter-method-reference/
 */
public class BeanUtils {
    /**
     *The mapping of cache class lambda
     */
    private static Map<Class, SerializedLambda> CLASS_LAMDBA_CACHE = new ConcurrentHashMap<>();

    /***
     *Convert method reference to property name
     * @param fn
     * @return
     */
    public static <T> String convertToFieldName(SGetter<T> fn) {
        return convertGetter(fn);
    }

    public static <R> String convertToFieldName(OGetter<R> fn) {
        return convertGetter(fn);
    }

    /***
     *Convert setter method reference to property name
     * @param fn
     * @return
     */
    public static <T,R> String convertToFieldName(SSetter<T,R> fn) {
        return convertSetter(fn);
    }

    public static <V> String convertToFieldName(OSetter<V> fn) {
        return convertSetter(fn);
    }

    private static <T> String convertGetter(Serializable fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if(methodName.startsWith("get")){
            prefix = "get";
        }
        else if(methodName.startsWith("is")){
            prefix = "is";
        }
        if(prefix == null){
            Log.warn ("invalid getter method:" + methodName);
        }
        //Intercept the string after get / is and convert the initial to lowercase (s is the string tool class of diboot project, which can be implemented by yourself)
        //return S.uncapFirst(S.substringAfter(methodName, prefix));
        return makeReturnFieldString(methodName, prefix);
    }

    private static String makeReturnFieldString(String methodName, String prefix) {
        return methodName;
    }

    private static <T, R> String convertSetter(Serializable fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        if(!methodName.startsWith("set")){
            Log.warn ("invalid setter method:" + methodName);
        }
        //Intercept the string after set and convert the initial to lowercase (s is the string tool class of the diboot project, which can be implemented by yourself)
        //return S.uncapFirst(S.substringAfter(methodName, "set"));
        return makeReturnFieldString(methodName, "set");
    }

    /***
     *Get the lambda corresponding to the class
     * @param fn
     * @return
     */
    private static SerializedLambda getSerializedLambda(Serializable fn){
        //Check whether the cache already exists
        SerializedLambda lambda = CLASS_LAMDBA_CACHE.get(fn.getClass());
        if(lambda == null){
            try {// extract serializedlambda and cache
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMDBA_CACHE.put(fn.getClass(), lambda);
            }
            catch (Exception e){
                Log.Error("get serializedlambda exception, class =" + fn.getClass().getSimpleName(), e);
            }
        }
        return lambda;
    }

}

