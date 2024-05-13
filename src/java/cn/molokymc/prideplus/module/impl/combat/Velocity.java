package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.Setting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;



public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Strict", "Hypixel");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, -100, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, -100, 1);
    private final NumberSetting airhorizontal = new NumberSetting("Air Horizontal", 0, 100, -100, 1);
    private final NumberSetting airvertical = new NumberSetting("Air Vertical", 0, 100, -100, 1);

    public Velocity() {
        super("AntiVelocity","无击退", Category.COMBAT, "Reduces your knockback");
        Setting.addParent(mode, m -> m.is("Strict"), horizontal, vertical, airhorizontal, airvertical);
        this.addSettings(mode, horizontal, vertical, airhorizontal, airvertical);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        Packet<?> packet = e.getPacket();
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = ((S12PacketEntityVelocity) e.getPacket());
            switch (mode.getMode()) {
                case "Strict": {
                    if (Utils.mc.thePlayer.isOnGround()) {
                        if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= horizontal.getValue().intValue() / 100;
                        s12.motionZ *= horizontal.getValue().intValue() / 100;
                        s12.motionX *= vertical.getValue().intValue() / 100;
                    } else {
                        if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionX *= (int) airvertical.getValue().intValue() / 100;
                    }
                }
                break;
                case "Hypixel":
                    if (packet instanceof S12PacketEntityVelocity) {
                        if (s12.getEntityID() == this.mc.thePlayer.getEntityId()) {
                            e.cancel();
                            if (this.mc.thePlayer.onGround || s12.motionY / 8000.0D < 0.2D || s12.motionY / 8000.0D > 0.41995D)
                                this.mc.thePlayer.motionY = s12.motionY / 8000.0D;
                        }
                        break;
            }
            if (e.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion s27 = ((S27PacketExplosion) e.getPacket());
                switch (mode.getMode()) {
                    case "Strict": {
                        if (Utils.mc.thePlayer.onGround) {
                            if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                                e.cancel();
                                return;
                            }
                            s27.motionX *= horizontal.getValue().intValue() / 100;
                            s27.motionZ *= horizontal.getValue().intValue() / 100;
                            s27.motionX *= vertical.getValue().intValue() / 100;
                        } else {
                            if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                                e.cancel();
                                return;
                            }
                            s27.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                            s27.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                            s27.motionX *= (int) airvertical.getValue().intValue() / 100;
                        }
                    }
                    break;
                        }
                }
            }
        }
    }
}
