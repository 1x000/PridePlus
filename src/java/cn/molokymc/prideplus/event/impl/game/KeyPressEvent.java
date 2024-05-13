package cn.molokymc.prideplus.event.impl.game;

import cn.molokymc.prideplus.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class KeyPressEvent extends Event {

    private final int key;

    public KeyPressEvent(int key) {
        this.key = key;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getKey() {
        return key;
    }

}
