package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.movement.Speed;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;


import java.util.ArrayList;
import java.util.List;


public class AntiVoid extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog");
    private final NumberSetting fallDist = new NumberSetting("Fall Distance", 3, 20, 1, 0.5);
    private final TimerUtil timer = new TimerUtil();
    private boolean reset;
    private double lastGroundY;

    private final List<Packet> packets = new ArrayList<>();

    public AntiVoid() {
        super("AntiVoid","虚空拉回", Category.PLAYER, "saves you from the void");
        this.addSettings(mode, fallDist);
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        setSuffix(fallDist.getValue().toString());
        if(mode.is("Watchdog") && !Client.INSTANCE.getModuleCollection().getModule(Speed.class).isEnabled()) {
            if(event.getPacket() instanceof C03PacketPlayer) {
                if(!isBlockUnder()) {
                    if(Utils.mc.thePlayer.fallDistance < fallDist.getValue()) {
                        event.cancel();
                        packets.add(event.getPacket());
                    } else {
                        if(!packets.isEmpty()) {
                            for(Packet packet : packets) {
                                final C03PacketPlayer c03 = (C03PacketPlayer) packet;
                                c03.setY(lastGroundY);
                                PacketUtils.sendPacketNoEvent(packet);
                            }
                            packets.clear();
                        }
                    }
                } else {
                    lastGroundY = Utils.mc.thePlayer.posY;
                    if(!packets.isEmpty()) {
                        packets.forEach(PacketUtils::sendPacketNoEvent);
                        packets.clear();
                    }
                }
            }
        }
    }

    private boolean isBlockUnder() {
        if (Utils.mc.thePlayer.posY < 0) return false;
        for (int offset = 0; offset < (int) Utils.mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = Utils.mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!Utils.mc.theWorld.getCollidingBoundingBoxes(Utils.mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
