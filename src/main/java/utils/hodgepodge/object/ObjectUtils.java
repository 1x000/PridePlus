package utils.hodgepodge.object;

import utils.hodgepodge.function.CatchHandler;
import utils.hodgepodge.function.ThrowsVoidFunction;
import utils.hodgepodge.function.VoidFunction;

public final class ObjectUtils {
    public static int hash(Object o) {
        return o.hashCode();
    }

    public static boolean checkNull(Object o) {
        return o == null;
    }

    public static boolean checkNull(Object... o) {
        if (o == null || o.length == 0) return true;

        for (Object o1 : o) {
            if (checkNull(o1)) {
                return true;
            }
        }

        return false;
    }

    public static boolean notNull(Object o) {
        return o != null;
    }

    public static boolean notNull(Object... o) {
        if (o == null || o.length == 0) return false;
        for (Object o1 : o) {
            if (checkNull(o1)) {
                return false;
            }
        }

        return true;
    }

    public static <T> T makeSureNotNull(T o) {
        if (o == null) {
            throw new NullPointerException();
        }

        return o;
    }

    public static <T> T makeSureNotNull(T o,String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }

        return o;
    }

    public static <T> T makeSureNotNull(T o,VoidFunction function) {
        if (o == null) {
            function.handle();
        }

        return o;
    }

    public static Object[] makeSureNotNull(Object... objArray) {
        for (Object obj : objArray) {
            makeSureNotNull(obj);
        }
        return objArray;
    }

    public static Object[] makeSureNotNull(String message,Object... objArray) {
        for (Object obj : objArray) {
            makeSureNotNull(obj,message);
        }
        return objArray;
    }

    public static Object[] makeSureNotNull(VoidFunction function,Object... objArray) {
        for (Object obj : objArray) {
            makeSureNotNull(obj,function);
        }
        return objArray;
    }

    public static void trySomeThing(ThrowsVoidFunction function,CatchHandler handler) {
        try {
            function.handle();
        } catch (Throwable throwable) {
            handler.onCatchException(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    public static <O,T> O forcedConversion(T obj) {
        return (O) obj;
    }

    public static boolean reverseBoolean(boolean b) {
        return !b;
    }
}
