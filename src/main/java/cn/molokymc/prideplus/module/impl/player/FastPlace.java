package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;


public final class FastPlace extends Module {

    private final NumberSetting ticks = new NumberSetting("Ticks", 0, 4, 0, 1);
    private final BooleanSetting blocks = new BooleanSetting("Blocks", true);
    private final BooleanSetting projectiles = new BooleanSetting("Projectiles", true);

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (canFastPlace()) {
            Utils.mc.rightClickDelayTimer = Math.min(Utils.mc.rightClickDelayTimer, ticks.getValue().intValue());
        }
    }

    @Override
    public void onDisable() {
        Utils.mc.rightClickDelayTimer = 4;
        super.onDisable();
    }

    private boolean canFastPlace() {
        if (Utils.mc.thePlayer == null || Utils.mc.thePlayer.getCurrentEquippedItem() == null || Utils.mc.thePlayer.getCurrentEquippedItem().getItem() == null)
            return false;
        Item heldItem = Utils.mc.thePlayer.getCurrentEquippedItem().getItem();
        return (blocks.isEnabled() && heldItem instanceof ItemBlock) || (projectiles.isEnabled() && (heldItem instanceof ItemSnowball || heldItem instanceof ItemEgg));
    }

    public FastPlace() {
        super("FastPlace","快速放置", Category.PLAYER, "place blocks fast");
        this.addSettings(ticks, blocks, projectiles);
    }

}
