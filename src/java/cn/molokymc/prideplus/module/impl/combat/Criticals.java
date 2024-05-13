package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;




@SuppressWarnings("unused")
public final class Criticals extends Module {

    private boolean stage;
    private double offset;
    private int groundTicks;
    private final ModeSetting mode = new ModeSetting("Mode", "NoGround", "NoGround", "Packet");
    private final NumberSetting delay = new NumberSetting("Delay", 1, 20, 0, 1);
    private final TimerUtil timer = new TimerUtil();

    public Criticals() {
        super("Criticals","刀刀暴击", Category.COMBAT, "Crit attacks");
        delay.addParent(mode, m -> !(m.is("Verus") || (m.is("Watchdog"))));
        this.addSettings(mode, delay);
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "NoGround":
                e.setOnGround(false);
                break;
            case "Packet":
                if (Utils.mc.objectMouseOver.entityHit != null && Utils.mc.thePlayer.onGround)
                    if (Utils.mc.objectMouseOver.entityHit.hurtResistantTime > delay.getValue().intValue())
                        for (double offset : new double[]{0.006253453, 0.002253453, 0.001253453})
                            Utils.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Utils.mc.thePlayer.posX, Utils.mc.thePlayer.posY + offset, Utils.mc.thePlayer.posZ, false));
                    }
                }
        }
