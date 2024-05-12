package dev.stable.module.impl.misc;

import dev.stable.event.impl.network.PacketReceiveEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.utils.player.ChatUtil;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;



public final class LightningTracker extends Module {

    public LightningTracker() {
        super("LightningTracker","闪电追踪器", Category.MISC, "detects lightning");
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundPacket = ((S29PacketSoundEffect) event.getPacket());
            if (soundPacket.getSoundName().equals("ambient.weather.thunder")) {
                ChatUtil.print(String.format("Lightning detected at (%s, %s, %s)", (int) soundPacket.getX(), (int) soundPacket.getY(), (int) soundPacket.getZ()));
            }
        }
    }

}
