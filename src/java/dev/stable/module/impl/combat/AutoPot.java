package dev.stable.module.impl.combat;

import dev.stable.Client;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.impl.movement.Scaffold;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.player.MovementUtils;
import dev.stable.utils.server.PacketUtils;
import dev.stable.utils.time.TimerUtil;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.concurrent.ThreadLocalRandom;


public class AutoPot extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 750, 2000, 0, 50);
    private final NumberSetting minHealHP = new NumberSetting("Heal HP", 12, 20, 1, 0.5);
    private final BooleanSetting splashFrogPots = new BooleanSetting("Frog potions", false);
    private final BooleanSetting Inventoryonly = new BooleanSetting("Inventory only ", false);
    private final TimerUtil timerUtil = new TimerUtil();
    public static boolean isPotting;
    private float prevPitch;

    public AutoPot() {
        super("AutoPot","自动喷药水", Category.COMBAT, "auto splashes potions");
        this.addSettings(delay, minHealHP, splashFrogPots, Inventoryonly );
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        //this.setSuffix("Stop Moving");
        if (Client.INSTANCE.isEnabled(Scaffold.class) || Utils.mc.currentScreen instanceof GuiChest)
            return;

        int prevSlot = Utils.mc.thePlayer.inventory.currentItem;
        if (e.isPre()) {
            if (MovementUtils.isOnGround(1.0E-5)
                    && !(Utils.mc.theWorld.getBlockState(new BlockPos(e.getX(), e.getY() - 1, e.getZ())).getBlock() instanceof BlockGlass)
                    && (!Utils.mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    || Utils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getDuration() < 30)
                    && timerUtil.hasTimeElapsed(delay.getValue().longValue()) && !Utils.mc.thePlayer.isUsingItem() && !MovementUtils.isMoving()){
                if (isSpeedPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isSpeedPot(Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = Utils.mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            e.setPitch(-90 + ThreadLocalRandom.current().nextFloat());
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveSpeedPots();
                }
            }

            if (!Utils.mc.thePlayer.isPotionActive(Potion.regeneration) && Utils.mc.thePlayer.getHealth() <= minHealHP.getValue()
                    && timerUtil.hasTimeElapsed(delay.getValue().longValue())) {
                if (isRegenPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isRegenPot(Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = Utils.mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            e.setPitch(-90 + ThreadLocalRandom.current().nextFloat());
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveRegenPots();
                }
            }

            if (Utils.mc.thePlayer.getHealth() <= minHealHP.getValue() && timerUtil.hasTimeElapsed(delay.getValue().longValue())) {
                if (isHealthPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isHealthPot(Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = Utils.mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            e.setPitch(-90 + ThreadLocalRandom.current().nextFloat());
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveHealthPots();
                }
            }
        } else if (e.isPost()) {
            isPotting = false;
        }
    }

    private void throwPot(int prevSlot, int index) {
        double x = Utils.mc.thePlayer.posX, y = Utils.mc.thePlayer.posY, z = Utils.mc.thePlayer.posZ;
        float yaw = Utils.mc.thePlayer.rotationYaw;
        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                x, y, z, yaw, 88.8F + ThreadLocalRandom.current().nextFloat(), Utils.mc.thePlayer.onGround));
        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(index - 36));
        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                BlockPos.NEGATIVE, 255, Utils.mc.thePlayer.getHeldItem(), 0, 0, 0));
        PacketUtils.sendPacket(new C09PacketHeldItemChange(prevSlot));
        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                x, y, z, yaw, prevPitch, Utils.mc.thePlayer.onGround));
    }

    private boolean isSpeedPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isSpeedPot(Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private boolean isHealthPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isHealthPot(Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private boolean isRegenPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isRegenPot(Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private int getPotionCount() {
        int count = 0;
        for (int index = 0; index < 45; index++) {
            ItemStack stack = Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isHealthPot(stack) || isHealthPot(stack) || isRegenPot(stack))
                count++;
        }
        return count;
    }

    private void moveSpeedPots() {
        if (Utils.mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            final ItemStack stack = Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null) continue;
            if (!splashFrogPots.isEnabled() && stack.getDisplayName().contains("Frog")) continue;
            if (isSpeedPot(stack)) {
                Utils.mc.playerController.windowClick(0, index, 6, 2, Utils.mc.thePlayer);
                break;
            }
        }
    }

    private void moveHealthPots() {
        if (Utils.mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            ItemStack stack = Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isHealthPot(stack)) {
                Utils.mc.playerController.windowClick(0, index, 6, 2, Utils.mc.thePlayer);
                break;
            }
        }
    }

    private void moveRegenPots() {
        if (Utils.mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            ItemStack stack = Utils.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isRegenPot(stack)) {
                Utils.mc.playerController.windowClick(0, index, 6, 2, Utils.mc.thePlayer);
                break;
            }
        }
    }
    private void Inventoryonly() {

    }
    private boolean isSpeedPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (!splashFrogPots.isEnabled() && stack.getDisplayName().contains("Frog")) return false;
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.moveSpeed.id && e.getPotionID() != Potion.jump.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isHealthPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.heal.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRegenPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.regeneration.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
