package dev.stable.module.impl.player;

import dev.stable.event.impl.network.PacketSendEvent;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.ParentAttribute;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.server.PacketUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Blink extends Module {
    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private final BooleanSetting pulse = new BooleanSetting("Pulse",  false);
    private final NumberSetting delayPulse = new NumberSetting("Tick Delay",20, 100, 4, 1);

    private EntityOtherPlayerMP blinkEntity;

    List<Vec3> path = new ArrayList<>();

    public Blink() {
        super("Blink","暂停发包", Category.PLAYER, "holds movement packets");
        delayPulse.addParent(pulse, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(pulse, delayPulse);
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (Utils.mc.thePlayer == null || Utils.mc.thePlayer.isDead || Utils.mc.isSingleplayer() || Utils.mc.thePlayer.ticksExisted < 50) {
            packets.clear();
            return;
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            packets.add(event.getPacket());
            event.cancel();
        }

        if (pulse.isEnabled()) {
            if (!packets.isEmpty() && Utils.mc.thePlayer.ticksExisted % delayPulse.getValue().intValue() == 0 && Math.random() > 0.1) {
                packets.forEach(PacketUtils::sendPacketNoEvent);
                packets.clear();
            }
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if(event.isPre()) {
            if (Utils.mc.thePlayer.ticksExisted < 50) return;

            if (Utils.mc.thePlayer.lastTickPosX != Utils.mc.thePlayer.posX || Utils.mc.thePlayer.lastTickPosY != Utils.mc.thePlayer.posY || Utils.mc.thePlayer.lastTickPosZ != Utils.mc.thePlayer.posZ) {
                path.add(new Vec3(Utils.mc.thePlayer.posX, Utils.mc.thePlayer.posY, Utils.mc.thePlayer.posZ));
            }

            if (pulse.isEnabled()) {
                while (path.size() > delayPulse.getValue().intValue()) {
                    path.remove(0);
                }
            }

            if (pulse.isEnabled() && blinkEntity != null) {
                Utils.mc.theWorld.removeEntityFromWorld(blinkEntity.getEntityId());
            }
        }
    }

    @Override
    public void onEnable() {
        path.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        super.onDisable();
    }
}
