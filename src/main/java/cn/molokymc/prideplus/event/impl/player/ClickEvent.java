package cn.molokymc.prideplus.event.impl.player;

import cn.molokymc.prideplus.event.Event;

public class ClickEvent extends Event
{
    boolean fake;

    public ClickEvent(boolean fake) { this.fake = fake; }

    public boolean isFake() { return fake; }
}
