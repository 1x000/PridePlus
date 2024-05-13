package cn.molokymc.prideplus.event.impl.game;

import cn.molokymc.prideplus.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;
public class TickEvent extends Event.StateEvent {

    private final int ticks;

    public TickEvent(int ticks) {
        this.ticks = ticks;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getTicks() {
        return ticks;
    }

}
