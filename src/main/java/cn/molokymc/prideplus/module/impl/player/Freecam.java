package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.BoundingBoxEvent;
import cn.molokymc.prideplus.event.impl.player.PushOutOfBlockEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.utils.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;




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
