package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.utils.Utils;

public final class Brightness extends Module {

    @Override
    public void onMotionEvent(MotionEvent event) {
        Utils.mc.gameSettings.gammaSetting = 100;
    }

    @Override
    public void onDisable() {
        Utils.mc.gameSettings.gammaSetting = 0;
        super.onDisable();
    }

    public Brightness() {
        super("Brightness","夜视", Category.RENDER, "changes the game brightness");
    }

}
