package cn.molokymc.prideplus.module.api.events.world;

import cn.molokymc.prideplus.event.Event;
import net.minecraft.client.audio.ISound;

public class EventSound extends Event {
    public ISound sound;
    public EventSound(ISound iSound){
        sound = iSound;
    }
}
