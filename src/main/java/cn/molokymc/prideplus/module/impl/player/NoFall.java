package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class NoFall extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Packet", "Edit");
    private TimerUtil timer = new TimerUtil();
    boolean sendC07 = false;
    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();

    public NoFall() {
        super("NoFall","摔不死", Category.PLAYER, "prevents fall damage");
        this.addSettings(mode);
    }
    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(this.mode.getMode());
        switch (this.mode.getMode()) {
            case "Vanilla":
                event.setOnGround(true);
            break;
            case "Packet":
                PacketUtils.sendPacket(new C03PacketPlayer(true));
                break;
        }
    }
    public static boolean aabb(double d) {
        AxisAlignedBB axisAlignedBB = mc.thePlayer.getEntityBoundingBox();
        if (axisAlignedBB == null) {
            return false;
        } else {
            double d2;
            for(d2 = 0.0; d2 < d; d2 += (double)mc.thePlayer.height) {
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, axisAlignedBB.offset(0.0, -d2, 0.0)).isEmpty()) {
                    return false;
                }
            }

            for(d2 = 0.0; d2 < axisAlignedBB.minY; d2 += (double)mc.thePlayer.height) {
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, axisAlignedBB.offset(0.0, -d2, 0.0)).isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean jumpPotion() {
        PotionEffect potionEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        float f = potionEffect != null ? (float)(potionEffect.getAmplifier() + 1) : 0.0F;
        return MathHelper.ceiling_float_int(mc.thePlayer.fallDistance - 3.0F - f) > 0;
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (this.mode.is("Edit")) {
          if (event.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer)event.getPacket()).onGround || !this.timer.hasTimeElapsed(100L) || !aabb(0.0) || !jumpPotion()) {
                return;
            }
        }

            this.timer.reset();
            PacketUtils.sendPacket(new C03PacketPlayer(true));
            mc.thePlayer.fallDistance = 0.0F;
        }
    }
