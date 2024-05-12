package dev.stable.event.impl.player;


import dev.stable.event.Event;
import dev.stable.utils.math.Vector2f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventLook
        extends Event {
    private Vector2f rotation;
    public EventLook(Vector2f rotation) {
        this.rotation = rotation;
    }

}

