package cn.molokymc.prideplus.ui.altmanager;

import java.util.concurrent.ThreadLocalRandom;

public final class StringUtils {
    public static final String ALPHA_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";

    public static String replace(String s2, Object ... o2) {
        for (int i = 0; i < o2.length; ++i) {
            s2 = s2.replace(StringUtils.build("{", i, "}"), o2[i].toString());
        }
        return s2;
    }

    public static String breakString(String s2) {
        StringBuilder sb = new StringBuilder();
        String[] sArray = s2.split("");
        int index = 0;
        for (String s1 : sArray) {
            if (s1.equals("")) continue;
            if (s1.equals(s1.toUpperCase()) && Character.isLetter(s1.toCharArray()[0]) && index != 0) {
                sb.append(" ");
            }
            sb.append(s1);
            ++index;
        }
        return sb.toString();
    }

    public static String build(Object ... objects) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o2 : objects) {
            stringBuilder.append(o2);
        }
        return stringBuilder.toString();
    }

    public static String randomString(String pool, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(pool.charAt(ThreadLocalRandom.current().nextInt(0, pool.length())));
        }
        return builder.toString();
    }

    public static boolean isNullOrEmpty(String s2) {
        return s2 == null || s2.length() == 0;
    }

    public static boolean isNotNullOrEmpty(String s2) {
        return s2 != null && StringUtils.isNotEmpty(s2);
    }

    public static boolean isNotEmpty(String s2) {
        return s2.length() != 0;
    }
}

