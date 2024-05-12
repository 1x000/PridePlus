package dev.stable.module.impl.render;

import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.module.Category;
import dev.stable.module.Module;

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
