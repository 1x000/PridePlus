package dev.stable.module.api.events.world;

import dev.stable.event.Event;
import net.minecraft.client.audio.ISound;

public class EventSound extends Event {
    public ISound sound;
    public EventSound(ISound iSound){
        sound = iSound;
    }
}
