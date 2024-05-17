package cn.molokymc.prideplus.event.impl.player;


import cn.molokymc.prideplus.event.Event;
import cn.molokymc.prideplus.utils.math.Vector2f;
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

