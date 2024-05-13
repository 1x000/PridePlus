package cn.molokymc.prideplus.module.api.events.world;

import cn.molokymc.prideplus.event.Event;
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
