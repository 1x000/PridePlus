/*
 * Decompiled with CFR 0_132.
 */
package dev.stable.module.api.events.world;

import dev.stable.event.Event;
import net.minecraft.network.Packet;

public class EventPacketSend extends Event {
	public Packet packet;

	public EventPacketSend(Packet packet) {
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}
}
