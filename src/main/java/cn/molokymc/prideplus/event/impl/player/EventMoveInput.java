/*
 * Decompiled with CFR 0_132.
 */
package cn.molokymc.prideplus.event.impl.player;

import cn.molokymc.prideplus.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMoveInput
        extends Event {

    private float forward, strafe;
    private boolean jump, sneak;
    public EventMoveInput(float forward, float strafe, boolean jump, boolean sneak) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
    }
}

