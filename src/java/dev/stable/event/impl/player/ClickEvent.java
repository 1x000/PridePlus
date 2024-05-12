package dev.stable.event.impl.player;

import dev.stable.event.Event;

public class ClickEvent extends Event
{
    boolean fake;

    public ClickEvent(boolean fake) { this.fake = fake; }

    public boolean isFake() { return fake; }
}
