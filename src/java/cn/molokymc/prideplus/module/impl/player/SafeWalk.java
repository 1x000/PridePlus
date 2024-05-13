package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.player.SafeWalkEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.utils.Utils;




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
