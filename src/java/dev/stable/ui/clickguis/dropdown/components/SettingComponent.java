package dev.stable.ui.clickguis.dropdown.components;

import dev.stable.module.Module;
import dev.stable.module.settings.Setting;
import dev.stable.ui.Screen;
import dev.stable.utils.misc.HoveringUtil;
import dev.stable.utils.tuples.Pair;
import lombok.Getter;

import java.awt.*;

public abstract class SettingComponent<T extends Setting> implements Screen {
    @Getter
    private final T setting;

    public float x, y, width, height, alpha;
    public boolean typing;
    public float panelLimitY;
    public Pair<Color, Color> clientColors;
    public Color settingRectColor, textColor;
    public float countSize = 1;


    public SettingComponent(T setting) {
        this.setting = setting;
    }

    public boolean isHoveringBox(int mouseX, int mouseY) {
        return HoveringUtil.isHovering(x,y,width,height, mouseX, mouseY);
    }

    //Idk why 40 is perfect for it
    public boolean isClickable(float bottomY) {
        return bottomY > panelLimitY && bottomY < panelLimitY + 17 + Module.allowedClickGuiHeight;
    }

}
