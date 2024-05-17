package cn.molokymc.prideplus.utils.animations;

public class AnimationUtilFDP {
    public static float calculateCompensation(float target, float current, long delta, int speed) {
        float diff = current - target;
        if (delta < 1L) {
            delta = 1L;
        }
        if (diff > (float)speed) {
            double xD = (double)((long)speed * delta / 16L) < 0.25 ? 0.5 : (double)((long)speed * delta / 16L);
            if ((current = (float)((double)current - xD)) < target) {
                current = target;
            }
        } else if (diff < (float)(-speed)) {
            double xD = (double)((long)speed * delta / 16L) < 0.25 ? 0.5 : (double)((long)speed * delta / 16L);
            if ((current = (float)((double)current + xD)) > target) {
                current = target;
            }
        } else {
            current = target;
        }
        return current;
    }

    public static float animate(float target, float current, double speed) {
        boolean larger;
        boolean bl = larger = target > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        float dif = Math.max(target, current) - Math.min(target, current);
        float factor = (float)((double)dif * speed);
        current = larger ? current + factor : current - factor;
        return current;
    }
}

