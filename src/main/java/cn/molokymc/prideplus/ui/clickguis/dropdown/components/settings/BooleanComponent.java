package cn.molokymc.prideplus.ui.clickguis.dropdown.components.settings;

import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.ui.clickguis.dropdown.components.SettingComponent;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.misc.HoveringUtil;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.skidfont.FontManager;

import java.awt.*;

public class BooleanComponent extends SettingComponent<BooleanSetting> {

    public BooleanComponent(BooleanSetting booleanSetting) {
        super(booleanSetting);
    }


    private final Animation toggleAnimation = new DecelerateAnimation(1, 1, Direction.BACKWARDS);

    private final Animation hoverAnimation = new DecelerateAnimation(1, 1, Direction.BACKWARDS);



    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        toggleAnimation.setDirection(getSetting().isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        RenderUtil.resetColor();

        productregular14.drawString(getSetting().name, x + 5, y + productregular14.getMiddleOfBox(height)+1,new Color(0,0,0));

        float switchWidth = 17;
        float switchHeight = 7;
        float booleanX = x + width - (switchWidth + 5.5f);
        float booleanY = y + height /2f - switchHeight /2f;

        boolean hovering = HoveringUtil.isHovering(booleanX - 2, booleanY - 2, switchWidth + 4, switchHeight + 4, mouseX, mouseY);

        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        Color accentCircle = ColorUtil.applyOpacity(clientColors.getSecond(), alpha);
        Color rectColor =  ColorUtil.interpolateColorC(settingRectColor.brighter().brighter(), accentCircle, toggleAnimation.getOutput().floatValue());
        rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, .8f), hoverAnimation.getOutput().floatValue());

        RenderUtil.resetColor();
        RoundedUtil.drawRound(booleanX, booleanY, switchWidth, switchHeight, 3, rectColor);

        RenderUtil.resetColor();

        RoundedUtil.drawRound(x + width - (switchWidth + 4) + ((switchWidth - 8) * toggleAnimation.getOutput().floatValue()),
                y + productregular12.getMiddleOfBox(height)-(float)0.5, 5, 5, 2, new Color(255,255,255,255));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float switchWidth = 17;
        float switchHeight = 7;
        float booleanX = x + width - (switchWidth + 5.5f);
        float booleanY = y + height /2f - switchHeight /2f;

        boolean hovering = HoveringUtil.isHovering(booleanX - 2, booleanY - 2, switchWidth + 4, switchHeight + 4, mouseX, mouseY);

        if(isClickable(booleanY + switchHeight) && hovering && button == 0) {
            getSetting().toggle();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
