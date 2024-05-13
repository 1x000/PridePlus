package cn.molokymc.prideplus.module.api.events.world;

import cn.molokymc.prideplus.event.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    Type type;
    public Packet<?> packet;

    public EventPacket(Packet<?> packet,Type type){
        this.type = type;
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public boolean isServerSide(){
        return type == Type.RECEIVE;
    }

    public Type getTypes() {
        return type;
    }

    public enum Type {
        RECEIVE,
        SEND
    }
}
