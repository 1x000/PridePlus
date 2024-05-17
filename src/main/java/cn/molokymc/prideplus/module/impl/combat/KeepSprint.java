package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.event.impl.player.KeepSprintEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;


public final class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint","保持疾跑", Category.COMBAT, "Stops sprint reset after hitting");
    }

    @Override
    public void onKeepSprintEvent(KeepSprintEvent event) {
        event.cancel();
    }

}
