package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;




@SuppressWarnings("unused")
public final class Timer extends Module {

    private final NumberSetting amount = new NumberSetting("Amount", 1, 10, 0.1, 0.1);

    @Override
    public void onMotionEvent(MotionEvent event) {
        Utils.mc.timer.timerSpeed = amount.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        Utils.mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    public Timer() {
        super("Timer","时间管理大师", Category.PLAYER, "changes game speed");
        this.addSettings(amount);
    }

}
