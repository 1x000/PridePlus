package dev.stable.module.impl.render;

import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;

public final class Camera extends Module
{

    public final BooleanSetting betterBobbingValue = new BooleanSetting("BetterBobbing", true);

    public Camera() {
        super("Camera","镜头", Category.RENDER,"sb");
        addSettings(betterBobbingValue);
    }
}
