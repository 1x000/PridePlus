package dev.stable.module.impl.movement;

import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.event.impl.network.PacketReceiveEvent;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.utils.time.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;


import java.util.Arrays;
import java.util.List;


public final class InventoryMove extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Spoof", "Delay");
    private final TimerUtil delayTimer = new TimerUtil();
    private boolean wasInContainer;

    private static final List<KeyBinding> keys = Arrays.asList(
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    );

    public InventoryMove() {
        super("InventoryMove","背包行走", Category.MOVEMENT, "lets you move in your inventory");
        addSettings(mode);
    }

    public static void updateStates() {
        if (mc.currentScreen != null) {
            keys.forEach(k -> KeyBinding.setKeyBindState(k.getKeyCode(), GameSettings.isKeyDown(k)));
        }
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        boolean inContainer = mc.currentScreen instanceof GuiContainer;
        if (wasInContainer && !inContainer) {
            wasInContainer = false;
            updateStates();
        }
        switch (mode.getMode()) {
            case "Spoof":
            case "Vanilla":
                if (inContainer) {
                    wasInContainer = true;
                    updateStates();
                }
                break;
            case "Delay":
                if (e.isPre() && inContainer) {
                    if (delayTimer.hasTimeElapsed(100)) {
                        wasInContainer = true;
                        updateStates();
                        delayTimer.reset();
                    }
                }
                break;
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (mode.is("Spoof") && (e.getPacket() instanceof S2DPacketOpenWindow || e.getPacket() instanceof S2EPacketCloseWindow)) {
            e.cancel();
        }
    }

}
