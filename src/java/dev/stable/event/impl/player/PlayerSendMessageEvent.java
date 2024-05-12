package dev.stable.event.impl.player;

import dev.stable.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class PlayerSendMessageEvent extends Event {
    private final String message;

    public PlayerSendMessageEvent(String message) {
        this.message = message;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public String getMessage() {
        return message;
    }

}
