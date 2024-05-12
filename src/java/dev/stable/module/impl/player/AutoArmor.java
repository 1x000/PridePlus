package dev.stable.module.impl.player;

import dev.stable.event.impl.network.PacketSendEvent;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.player.ChatUtil;
import dev.stable.utils.player.InventoryUtils;
import dev.stable.utils.player.MovementUtils;
import dev.stable.utils.server.PacketUtils;
import dev.stable.utils.time.TimerUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;



public class AutoArmor extends Module {

    private final NumberSetting delay = new NumberSetting("Delay",  80.0, 1000.0, 0, 10.0);

    private final BooleanSetting invOnly = new BooleanSetting("Inventory only", false);
    private final TimerUtil timer = new TimerUtil();

    public AutoArmor() {
        super("AutoArmor","自动穿衣物", Category.PLAYER, "Automatically equips armor");
        this.addSettings(delay, invOnly);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(String.valueOf(delay.getValue().intValue()));
        if (event.isPre()) {
            if ((this.invOnly.getValue() && !(this.mc.currentScreen instanceof GuiInventory))) {
                return;
            }
            if (this.timer.hasTimePassed(((Double)this.delay.getValue()).longValue())) {
                for(int type = 1; type < 5; ++type) {
                    if (this.mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                        ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                        if (this.isBestArmor(is, type)) {
                            continue;
                        }

                        this.drop(4 + type);
                    }

                    for(int i = 9; i < 45; ++i) {
                        if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (this.isBestArmor(is, type) && getProtection(is) > 0.0F) {
                                this.shiftClick(i);
                                this.timer.reset();
                                if (((Double)this.delay.getValue()).longValue() > 0L) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }

        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        } else if (((ItemArmor)stack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.CHAIN) {
            return false;
        } else {
            for(int i = 5; i < 45; ++i) {
                if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
    public void shiftClick(int slot) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, this.mc.thePlayer);
    }

    public void drop(int slot) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, this.mc.thePlayer);
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0.0F;
        Item var3 = stack.getItem();
        if (var3 instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) var3;
            prot += (float) armor.damageReduceAmount + (float) ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075F;
            prot += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0F;
            prot += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0F;
            prot += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0F;
            prot += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0F;
            prot += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) / 100.0F;
        }

        return prot;
    }
}