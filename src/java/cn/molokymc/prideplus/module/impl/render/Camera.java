package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;

public final class Camera extends Module
{

    public final BooleanSetting betterBobbingValue = new BooleanSetting("BetterBobbing", true);

    public Camera() {
        super("Camera","镜头", Category.RENDER,"sb");
        addSettings(betterBobbingValue);
    }
}
