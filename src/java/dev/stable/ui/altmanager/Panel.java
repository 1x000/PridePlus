package dev.stable.ui.altmanager;

import dev.stable.ui.Screen;
import dev.stable.utils.render.ColorUtil;
import dev.stable.utils.render.RoundedUtil;
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
