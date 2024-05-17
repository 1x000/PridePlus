package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;

public class ScoreboardMod extends Module {

    public static final NumberSetting yOffset = new NumberSetting("Y Offset", 0, 250, 1, 5);
    public static final BooleanSetting textShadow = new BooleanSetting("Text Shadow", true);
    public static final BooleanSetting redNumbers = new BooleanSetting("Red Numbers", false);

    public ScoreboardMod() {
        super("Scoreboard","计分板", Category.DISPLAY, "Scoreboard preferences");
        this.addSettings(yOffset, textShadow, redNumbers);
        this.setToggled(true);
    }

}
