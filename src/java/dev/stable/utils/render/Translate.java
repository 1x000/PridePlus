package dev.stable.utils.render;

import dev.stable.utils.animations.AnimationUtilFDP;

public final class Translate {
    private float x;
    private float y;
    private boolean first = false;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void interpolate(float targetX, float targetY, double smoothing) {
        if (this.first) {
            this.x = AnimationUtilFDP.animate(targetX, this.x, smoothing);
            this.y = AnimationUtilFDP.animate(targetY, this.y, smoothing);
        } else {
            this.x = targetX;
            this.y = targetY;
            this.first = true;
        }
    }

    public float getY() {
        return this.y;
    }
}

