package dev.stable.event.impl.player;

import dev.stable.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SyncCurrentItemEvent extends Event {
    private int slot;
}