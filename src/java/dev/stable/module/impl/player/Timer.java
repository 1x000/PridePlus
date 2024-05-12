package dev.stable.module.impl.player;

import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;



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
