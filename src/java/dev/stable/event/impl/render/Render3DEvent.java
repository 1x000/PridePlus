package dev.stable.event.impl.render;

import dev.stable.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class Render3DEvent extends Event {

    private float ticks;

    public Render3DEvent(float ticks) {
        this.ticks = ticks;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public float getTicks() {
        return ticks;
    }

    public void setTicks(float ticks) {
        this.ticks = ticks;
    }

}
