package cn.molokymc.prideplus.ui.sidegui.panels;

import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.ui.Screen;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height, alpha;

    public Color getTextColor() {
        return ColorUtil.applyOpacity(Color.WHITE, alpha);
    }

    public Color getAccentColor() {
        return ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
    }

}
