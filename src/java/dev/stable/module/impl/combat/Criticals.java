package dev.stable.module.impl.combat;

import dev.stable.event.impl.network.PacketSendEvent;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.time.TimerUtil;
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
