package cn.molokymc.prideplus.utils.movementfix;

import cn.molokymc.prideplus.event.ListenerAdapter;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.event.impl.player.SyncCurrentItemEvent;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import static cn.molokymc.prideplus.utils.Utils.mc;

public final class SlotComponent extends ListenerAdapter {

    private static boolean render;



    public static void setSlot(final int slot, final boolean render) {
        if (slot < 0 || slot > 8) {
            return;
        }

        mc.thePlayer.inventory.alternativeCurrentItem = slot;
        mc.thePlayer.inventory.alternativeSlot = true;
        SlotComponent.render = render;
    }

    public static void setSlot(final int slot) {
        setSlot(slot, true);
    }



    @Override
    public void onSyncCurrentItem (SyncCurrentItemEvent event) {
        final InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;

        event.setSlot(inventoryPlayer.alternativeSlot ? inventoryPlayer.alternativeCurrentItem : inventoryPlayer.currentItem);
    };

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (e.isPre()) {
            mc.thePlayer.inventory.alternativeSlot = false;
        }
    };

    public static ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(getItemIndex() + 36).getStack());
    }

    public static int getItemIndex() {
        final InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
        return inventoryPlayer.alternativeSlot || false ? inventoryPlayer.alternativeCurrentItem : inventoryPlayer.currentItem;
    }
}