package dev.stable.module.impl.player;

import dev.stable.event.impl.player.SafeWalkEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.utils.Utils;




public final class SafeWalk extends Module {
    @Override
    public void onSafeWalkEvent(SafeWalkEvent e) {
        if(Utils.mc.thePlayer == null) return;
        e.setSafe(true);
    }
    public SafeWalk() {
        super("SafeWalk","方块边缘行走", Category.PLAYER, "prevents walking off blocks");
    }

}
