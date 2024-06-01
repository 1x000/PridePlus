package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.game.LegitTickEvent;
import cn.molokymc.prideplus.event.impl.game.TickEvent;
import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.player.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public final class Timer extends Module {
    private final ModeSetting mods = new ModeSetting("Balance", "Balance",  "Balance", "Normal");
    private final NumberSetting timerSpeed = new NumberSetting("TimerSpeed", 1, 10, 0.1, 0.1);

    public LinkedBlockingQueue<S32PacketConfirmTransaction> transactions = new LinkedBlockingQueue<>();

    @Override
    public void onMotionEvent(MotionEvent event) {
        Utils.mc.timer.timerSpeed = timerSpeed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        Utils.mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    public Timer() {
        super("Timer","时间管理大师", Category.PLAYER, "changes game speed");
        this.addSettings(mods, timerSpeed);
    }

    private long lastTransaction = -1;

    @Override
    public void onEnable() {
        lastTransaction = System.currentTimeMillis();
        transactions.clear();
        super.onEnable();
    }

    @Override
    public void onTickEvent(TickEvent e) {
        this.setSuffix(timerSpeed.getValue().toString());

        if (mods.is("Normal")) {
            mc.timer.timerSpeed = timerSpeed.getValue().intValue();
        } else if (mods.is("Balance")) {
            if (mc.thePlayer.hurtTime > 0){
                this.setState(false);
                return;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                mc.timer.timerSpeed = 2f;
                if (transactions.isEmpty()) this.setState(false);
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        final Packet<?> packet = event.getPacket();
        if (mods.is("Balance")) {
            if (packet instanceof S08PacketPlayerPosLook){
                event.cancel();
                this.setState(false);
                return;
            }
            if (packet instanceof S32PacketConfirmTransaction) {
                event.cancel();
                try {
                    transactions.put((S32PacketConfirmTransaction) packet);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        final Packet<?> packet = event.getPacket();
        if (mods.is("Balance")) {
            if (packet instanceof C03PacketPlayer && !transactions.isEmpty()) {
                if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C05PacketPlayerLook || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook){
                    return;
                }
                event.cancel();
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (mods.is("Balance")) {
            if (mc.thePlayer.hurtTime > 0){
                transactions.clear();
                this.setState(false);
            }
            int timer;
            // ChatUtil.print(String.valueOf(transactions.size()));
            mc.fontRendererObj.drawCenteredStringWithShadow(String.valueOf(transactions.size()), (float) event.sr.getScaledWidth() / 2, (float) event.sr.getScaledHeight() / 2 + 20, Color.WHITE.getRGB());
            if (!Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                timer = 500;
            } else {
                timer = 7;
            }
            if (System.currentTimeMillis() - lastTransaction >= timer) {
                if (!transactions.isEmpty()) {
                    transactions.poll().processPacket(mc.thePlayer.sendQueue);
                }
                lastTransaction = System.currentTimeMillis();
            }
        }
    }

}
