package cn.molokymc.prideplus.ui.altmanager;

import cn.molokymc.prideplus.ui.Screen;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height;


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(27));
    }


}
