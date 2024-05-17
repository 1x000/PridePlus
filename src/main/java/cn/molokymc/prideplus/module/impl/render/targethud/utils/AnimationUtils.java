package cn.molokymc.prideplus.module.impl.render.targethud.utils;

import cn.molokymc.prideplus.utils.animations.AnimationUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class AnimationUtils {
    public static double delta;
    public static float getAnimationStateFlux(float animation, final float finalState, final float speed) {
        final float add = (float) (delta * (speed / 1000f));
        if (animation < finalState) {
            if (animation + add < finalState) {
                animation += add;
            } else {
                animation = finalState;
            }
        } else if (animation - add > finalState) {
            animation -= add;
        } else {
            animation = finalState;
        }
        return animation;
    }
    public double animateNoFast(final double value, final double target, double speed) {
        if (AnimationUtils.delta <= 1L) {
            AnimationUtils.delta = 500L;
        }
        speed = speed * (double)4.0 / 4.0;
        final double increment = speed * RenderUtil.deltaTime / 500.0;
        final double returnValue = value + (target - value) * increment / 200.0;
        if (Math.abs(target - returnValue) < 0.05 * (4.0 / (double)4.0)) {
            return target;
        }
        return returnValue;
    }
    public static float animate(float target, float current, float speed) {
        boolean larger;
        boolean bl = larger = target > current;
        if (speed < 0.0) {
            speed = 0.0F;
        } else if (speed > 1.0) {
            speed = 1.0f;
        }
        float dif = Math.max(target, current) - Math.min(target, current);
        float factor = dif * speed;
        if (factor < 0.1) {
            factor = 0.1f;
        }
        current = larger ? (current += factor) : (current -= factor);
        return current;
    }

    public static float animation(float animation, float target, float speedTarget) {
        float dif = (target - animation) / Math.max((float) Minecraft.getDebugFPS(), 5) * 15;

        if (dif > 0) {
            dif = Math.max(speedTarget, dif);
            dif = Math.min(target - animation, dif);
        } else if (dif < 0) {
            dif = Math.min(-speedTarget, dif);
            dif = Math.max(target - animation, dif);
        }
        return animation + dif;
    }
    public static double animation(double animation, double target, double speedTarget) {
        double dif = (target - animation) / Math.max(Minecraft.getDebugFPS(), 5) * speedTarget;
        if (dif > 0.0D) {
            dif = Math.max(speedTarget, dif);
            dif = Math.min(target - animation, dif);
        } else if (dif < 0.0D) {
            dif = Math.min(-speedTarget, dif);
            dif = Math.max(target - animation, dif);
        }
        return animation + dif;
    }

    public static float moveUD(final float current, final float end, final float smoothSpeed, final float minSpeed) {
        float movement = (end - current) * smoothSpeed;
        if (movement > 0.0f) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
        } else if (movement < 0.0f) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
        }
        return current + movement;
    }

    public static float moveUD(float now, float desired, double speed) {
        double dif = (double) Math.abs(desired - now);
        float a = (float) Math.abs((double) (desired - (desired - Math.abs(desired - now))) / (100.0 - speed * 10.0));
        float x = now;
        if (dif != 0.0 && dif < (double) a) {
            a = (float) dif;
        }

        if (dif > 0.0) {
            if (now < desired) {
                x = (float) (now + a * delta);
            } else if (now > desired) {
                x = (float) (now - a * delta);
            }
        } else {
            x = desired;
        }

        if ((double) Math.abs(desired - x) < 0.05 && x != desired) {
            x = desired;
        }

        return x;
    }

    public static float getAnimationState(float animation, final float finalState, final float speed) {
        final float add = (float) (delta * (speed / 1000f));
        if (animation < finalState) {
            if (animation + add < finalState) {
                animation += add;
            } else {
                animation = finalState;
            }
        } else if (animation - add > finalState) {
            animation -= add;
        } else {
            animation = finalState;
        }
        return animation;
    }

    public static float calculateCompensation(final float target, float current, long delta, final int speed) {
        final float diff = current - target;
        if (delta < 1L) {
            delta = 1L;
        }
        if (diff > speed) {
            final double xD = (speed * delta / 16L < 0.25) ? 0.5 : ((double) (speed * delta / 16L));
            current -= (float) xD;
            if (current < target) {
                current = target;
            }
        } else if (diff < -speed) {
            final double xD = (speed * delta / 16L < 0.25) ? 0.5 : ((double) (speed * delta / 16L));
            current += (float) xD;
            if (current > target) {
                current = target;
            }
        } else {
            current = target;
        }
        return current;
    }

    public static float calculateCompensation(final float target, float current, final double speed, long delta) {
        final float diff = current - target;
        double add =  (delta * (speed / 50));
        if (diff > speed){
        	if(current - add > target) {
                current -= add;
        	}else {
                current = target;
        	}
        }
        else if (diff < -speed) {
        	if(current + add < target) {
                current += add;
        	}else {
                current = target;
        	}
        }
        else{
            current = target;
        }
        return current;
    }
}
