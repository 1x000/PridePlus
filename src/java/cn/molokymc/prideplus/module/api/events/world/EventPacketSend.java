/*
 * Decompiled with CFR 0_132.
 */
package cn.molokymc.prideplus.module.api.events.world;

import cn.molokymc.prideplus.event.Event;
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
