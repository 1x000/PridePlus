package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.event.impl.game.TickEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;

public final class Animations extends Module {

    public static final ModeSetting mode = new ModeSetting("Mode", "Swing",
            "Swing", "Swong", "Swonk", "Swang","Swagg", "Swank", "Swack", "Lucky","SlideDown", "1.7","Loser");


    public static final NumberSetting scale = new NumberSetting("Item Scale", 1, 1, 0, 0.1);

    public static final BooleanSetting every = new BooleanSetting("EverythingBlock", true);
    public static final BooleanSetting swing = new BooleanSetting("Flux Swing", false);

    public static final BooleanSetting damage = new BooleanSetting("Old damage", true);
    public static final NumberSetting slowdown = new NumberSetting("Swing Slowdown", 1, 15, 1, 0.1);

    public Animations() {
        super("Animations","挥手动画", Category.RENDER, "changes animations");
        this.addSettings(mode,every,swing,scale,damage,slowdown);
    }
    private void swingItem(EntityPlayerSP entityplayersp) {
        int swingAnimationEnd = entityplayersp.isPotionActive(Potion.digSpeed)
                ? (6 - (1 + entityplayersp.getActivePotionEffect(Potion.digSpeed).getAmplifier()))
                : (entityplayersp.isPotionActive(Potion.digSlowdown)
                ? (6 + (1 + entityplayersp.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2)
                : 6);
        if (!entityplayersp.isSwingInProgress || entityplayersp.swingProgressInt >= swingAnimationEnd / 2
                || entityplayersp.swingProgressInt < 0) {
            entityplayersp.swingProgressInt = -1;
            entityplayersp.isSwingInProgress = true;
        }
    }
    @Override
    public void onTickEvent(TickEvent event) {
            boolean mouseDown = mc.gameSettings.keyBindUseItem.isKeyDown() && mc.gameSettings.keyBindAttack.isKeyDown();
            if (mouseDown && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                swingItem(mc.thePlayer);
            }

    }
    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());
    }

}
