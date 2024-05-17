package cn.molokymc.prideplus.event.impl.network;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@Getter
@Setter
public class PacketReceiveEvent extends PacketEvent {
    private final EnumPacketDirection direction;
    private final INetHandler netHandler;
    public PacketReceiveEvent(Packet<?> packet, EnumPacketDirection direction, INetHandler netHandler) {
        super(packet);
        this.direction = direction;
        this.netHandler = netHandler;
    }
}
