package cn.molokymc.prideplus.event.impl.player;

import cn.molokymc.prideplus.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SyncCurrentItemEvent extends Event {
    private int slot;
}