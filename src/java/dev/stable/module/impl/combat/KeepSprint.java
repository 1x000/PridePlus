package dev.stable.module.impl.combat;

import dev.stable.event.impl.player.KeepSprintEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

public final class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint","保持疾跑", Category.COMBAT, "Stops sprint reset after hitting");
    }

    @Override
    public void onKeepSprintEvent(KeepSprintEvent event) {
        event.cancel();
    }

}
