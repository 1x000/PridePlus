package dev.stable.utils.client;

public class TimeLog {
    private static int hour;
    private static int minute;
    private static int second;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;

    private static long startTimeMillis;

    public TimeLog() {
        startTimeMillis = System.currentTimeMillis();
    }

    private void updateTime() {
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        int totalSeconds = (int) (elapsedTimeMillis / MILLISECONDS_PER_SECOND);

        second = totalSeconds % SECONDS_PER_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_PER_MINUTE;
        minute = totalMinutes % MINUTES_PER_HOUR;
        hour = totalMinutes / MINUTES_PER_HOUR;
    }

    public static void reset() {
        startTimeMillis = System.currentTimeMillis();
        hour = 0;
        minute = 0;
        second = 0;
    }

    public int getHour() {
        updateTime();
        return hour;
    }

    public int getMinute() {
        updateTime();
        return minute;
    }

    public int getSecond() {
        updateTime();
        return second;
    }
}

