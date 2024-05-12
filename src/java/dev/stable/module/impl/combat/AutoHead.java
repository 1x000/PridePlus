package dev.stable.module.impl.combat;

import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.player.InventoryUtils;
import dev.stable.utils.server.PacketUtils;
import dev.stable.utils.time.TimerUtil;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;



public class AutoHead extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 750, 3000, 0, 50);
    private final NumberSetting healPercent = new NumberSetting("Health %", 50, 99, 1, 1);
    private final TimerUtil timer = new TimerUtil();

    public AutoHead() {
        super("AutoHead","自动吃金头", Category.COMBAT, "auto consume heads");
        this.addSettings(delay, healPercent);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (Utils.mc.thePlayer != null && Utils.mc.theWorld != null && e.isPre()
                && !(Utils.mc.thePlayer.isPotionActive(Potion.moveSpeed) && Utils.mc.thePlayer.isPotionActive(Potion.regeneration))
                && (Utils.mc.thePlayer.getHealth() / Utils.mc.thePlayer.getMaxHealth()) * 100 <= healPercent.getValue()
                && timer.hasTimeElapsed(delay.getValue().longValue())) {
            for (int i = 0; i < 45; i++) {
                ItemStack is = Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is != null && is.getItem() instanceof ItemSkull && is.getDisplayName().contains("Head")) {
                    int prevSlot = Utils.mc.thePlayer.inventory.currentItem;
                    if (i - 36 < 0) {
                        InventoryUtils.swap(i, 8);
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(8));
                    } else {
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(i - 36));
                    }

                    PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
                    Utils.mc.thePlayer.inventory.currentItem = prevSlot;
                    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));

                    timer.reset();
                }
            }
        }
    }

}
