package utils.hodgepodge.object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectUtils {
    public static <T> Field getField(T obj,String fieldName) throws NoSuchFieldException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        return setFieldAccessible(field,true);
    }

    public static Field setFieldAccessible(Field field,boolean accessible) {
        field.setAccessible(accessible);
        return field;
    }

    public static <T,V> void setField(T obj,String fieldName,V value) throws NoSuchFieldException,IllegalAccessException {
        getField(obj,fieldName).set(obj,value);
    }

    public static <T> Method getMethod(T obj,String methodName,Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = obj.getClass().getDeclaredMethod(methodName,parameterTypes);
        return setMethodAccessible(method,true);
    }

    public static <T> Method getMethodFromClass(Class<T> cla,String methodName,Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = cla.getDeclaredMethod(methodName,parameterTypes);
        return setMethodAccessible(method,true);
    }

    @SuppressWarnings("rawtypes")
    public static Class[] getParameterTypes(Object[] objects) {
        Class[] classes = new Class[objects.length];

        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }

        return classes;
    }

    public static Method setMethodAccessible(Method method,boolean accessible) {
        method.setAccessible(accessible);
        return method;
    }

    public static <T> Object invokeMethodFromName(T obj,Class<T> objClass,String methodName,Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return invokeMethod(obj,getMethodFromClass(objClass,methodName,getParameterTypes(args)),args);
    }

    public static <T> Object invokeMethod(T obj,Method method,Object[] args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj,args);
    }
}
