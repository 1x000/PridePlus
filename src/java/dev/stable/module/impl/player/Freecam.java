package dev.stable.module.impl.player;

import dev.stable.event.impl.network.PacketSendEvent;
import dev.stable.event.impl.player.BoundingBoxEvent;
import dev.stable.event.impl.player.PushOutOfBlockEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.utils.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;



public final class Freecam extends Module {

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        if (Utils.mc.thePlayer != null) {
            event.cancel();
        }
    }

    @Override
    public void onPushOutOfBlockEvent(PushOutOfBlockEvent event) {
        if (Utils.mc.thePlayer != null) {
            event.cancel();
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.cancel();
        }
    }

    @Override
    public void onEnable() {
        if (Utils.mc.thePlayer != null) {
            Utils.mc.thePlayer.capabilities.allowFlying = true;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (Utils.mc.thePlayer != null) {
            Utils.mc.thePlayer.capabilities.allowFlying = false;
            Utils.mc.thePlayer.capabilities.isFlying = false;
        }
        super.onDisable();
    }

    public Freecam() {
        super("Freecam","射像机", Category.PLAYER, "allows you to look around freely");
    }

}
