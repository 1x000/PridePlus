package cn.molokymc.prideplus.module.api.events.world;

import cn.molokymc.prideplus.event.Event;
import net.minecraft.entity.Entity;

public class EventEntityMovement extends Event {
    Entity entity;
    public EventEntityMovement(Entity entityIn) {
        entity = entityIn;
    }

    public Entity getMovedEntity() {
        return entity;
    }
}
