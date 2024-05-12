package dev.stable.module.api.events.world;

import dev.stable.event.Event;
import lombok.Getter;
import lombok.Setter;

public class EventJump extends Event {
    @Getter
    @Setter
    public float yaw;
    public EventJump(float yaw) {
        this.yaw = yaw;
    }
}
