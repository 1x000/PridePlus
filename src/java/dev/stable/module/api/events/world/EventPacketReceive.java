package dev.stable.module.api.events.world;

import dev.stable.event.Event;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class EventPacketReceive extends Event
	{
		private Packet<?> packet;
		private final EnumPacketDirection direction;
		private final INetHandler netHandler;

    public EventPacketReceive(final Packet<?> packet, final EnumPacketDirection direction, final INetHandler netHandler) {
		this.packet = packet;
		this.direction = direction;
		this.netHandler = netHandler;
	}

		public Packet<?> getPacket() {
		return this.packet;
	}

		public EnumPacketDirection getDirection() {
		return this.direction;
	}

		public INetHandler getNetHandler() {
		return this.netHandler;
	}

		public void setPacket(final Packet<?> packet) {
		this.packet = packet;
	}
	}

