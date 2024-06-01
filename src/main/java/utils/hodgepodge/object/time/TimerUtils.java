package utils.hodgepodge.object.time;

public final class TimerUtils {
    private final boolean autoReset;

    private long lastTime;

    public TimerUtils() {
        autoReset = false;
    }

    public TimerUtils(boolean autoReset) {
        this.autoReset = autoReset;
    }

    public boolean hasReached(double milliseconds) {
        boolean b = (double)(getCurrentMS() - lastTime) >= milliseconds;
        if (autoReset) {
            if (b) {
                reset();
                return true;
            }
        }

        return b;
    }

    public final long getElapsedTime() {
        return getCurrentMS() - lastTime;
    }

    public void reset() {
        lastTime = getCurrentMS();
    }

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
}
