package utils.hodgepodge.object.math;

import java.math.BigDecimal;

public final class NumberUtils {
    public static int toInteger(double n) {
        return (int) n;
    }

    public static int toInteger(float n) {
        return (int) n;
    }

    public static int toInteger(long n) {
        return (int) n;
    }

    public static int toInteger(boolean b) {
        return b ? 1 : 0;
    }

    public static long toLong(double n) {
        return (long) n;
    }

    public static long toLong(float n) {
        return (long) n;
    }

    public static short toShort(double n) {
        return (short) n;
    }

    public static short toShort(float n) {
        return (short) n;
    }

    public static short toShort(int n) {
        return (short) n;
    }

    public static short toShort(long n) {
        return (short) n;
    }

    public static boolean isNaN(double n) {
        return n != n;
    }

    public static boolean isNaN(float n) {
        return n != n;
    }

    public static String originalNumber(double n) {
        return new BigDecimal(n).toString();
    }

    public static String originalNumber(float n) {
        return new BigDecimal(n).toString();
    }

    public static String originalNumber(long n) {
        return new BigDecimal(n).toString();
    }

    public static String originalNumber(int n) {
        return new BigDecimal(n).toString();
    }
}
