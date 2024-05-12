package dev.stable.module.impl.render;

import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.utils.Utils;

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
