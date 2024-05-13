
package cn.molokymc.prideplus.module.api.events.world;


import cn.molokymc.prideplus.event.Event;
import lombok.Getter;
import lombok.Setter;

public final class EventStrafe
        extends Event {
    @Getter
    @Setter
    private final float strafe;
    @Getter
    @Setter
    public float yaw;
    private final float forward;
    private final float friction;

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public EventStrafe(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }
}
