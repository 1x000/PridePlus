package dev.stable.event.impl.game;

import dev.stable.event.Event;
import net.minecraft.entity.EntityLivingBase;

public class EntityKilledEvent
extends Event {
    private EntityLivingBase targetEntity;

    public EntityKilledEvent(EntityLivingBase targetEntity) {
        this.targetEntity = targetEntity;
    }

    public EntityLivingBase getTargetEntity() {
        return this.targetEntity;
    }
}

