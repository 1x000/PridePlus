package cn.molokymc.prideplus.ui.clickguis.dropdown.components.settings;

import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.ui.clickguis.dropdown.components.SettingComponent;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.ContinualAnimation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.font.FontUtil;
import cn.molokymc.prideplus.utils.misc.HoveringUtil;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.render.Theme;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.utils.tuples.mutable.MutablePair;

import java.awt.*;

public class ModeComponent extends SettingComponent<ModeSetting> {

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);
    private final Animation openAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);
    private final Animation selectionBox = new DecelerateAnimation(250, 1, Direction.BACKWARDS);

    private boolean opened;

    public float realHeight;
    public float normalCount;

    private final ContinualAnimation selectionBoxY = new ContinualAnimation();
    private String hoveringMode = "";

    public ModeComponent(ModeSetting modeSetting) {
        super(modeSetting);
        normalCount = 2;
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        ModeSetting modeSetting = getSetting();

        float boxHeight = 18;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 4;
        float boxX = x + 5;
        float boxWidth = width - 10;

        boolean themeSetting = modeSetting.name.equals("Theme Selection");

        boolean hoveringBox = HoveringUtil.isHovering(boxX, boxY, boxWidth, boxHeight, mouseX, mouseY);

        hoverAnimation.setDirection(hoveringBox ? Direction.FORWARDS : Direction.BACKWARDS);
        openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        Color outlineColor = ColorUtil.interpolateColorC(settingRectColor.brighter().brighter(), clientColors.getSecond(), .3f * hoverAnimation.getOutput().floatValue());

        outlineColor = ColorUtil.interpolateColorC(outlineColor, clientColors.getSecond(), openAnimation.getOutput().floatValue());


        Color rectColor = ColorUtil.interpolateColorC(settingRectColor, settingRectColor.brighter(),
                (.5f * hoverAnimation.getOutput().floatValue()) + (openAnimation.getOutput().floatValue()));


        // Gui.drawRect2(x,y,width,height, -1);
        //RoundedUtil.drawRound(boxX, boxY, boxWidth, boxHeight, 4, new Color(0,0,0,50));


        RoundedUtil.drawRound(boxX + 1, boxY + 1, boxWidth - 2, boxHeight - 2, 3, new Color(0,0,0,40));

        productregular14.drawString(modeSetting.name, boxX + 1, y + 4, new Color(0,0,0));

        productregular14.drawString(modeSetting.getMode(), boxX + 5, boxY + productregular14.getMiddleOfBox(boxHeight), new Color(0,0,0));

        if(themeSetting){
            MutablePair<Color, Color> themeColors = Theme.getThemeColors(modeSetting.getMode()).apply(MutablePair::of);

            themeColors.computeFirst(color -> ColorUtil.applyOpacity(color, alpha));
            themeColors.computeSecond(color -> ColorUtil.applyOpacity(color, alpha));

            float height = 8;
            float width = 8;
            float middleOfRect = boxHeight /2f - height /2f;
            float spacing = 3;
            RoundedUtil.drawRound(boxX + 7.5f + productregular12.getStringWidth(modeSetting.getMode()), boxY + middleOfRect,
                    width, height, 1f, themeColors.getFirst());
            RoundedUtil.drawRound(boxX + 7.5f + productregular12.getStringWidth(modeSetting.getMode()) + (spacing + width), boxY + middleOfRect,
                    width, height, 1f, themeColors.getSecond());
        }



        RenderUtil.resetColor();

        RenderUtil.resetColor();
        float arrowX = boxX + boxWidth - 11;
        float arrowY = boxY + iconFont20.getMiddleOfBox(boxHeight) + 1;
        float openAnim = openAnimation.getOutput().floatValue();

        RenderUtil.rotateStart(arrowX, arrowY, iconFont20.getStringWidth(FontUtil.DROPDOWN_ARROW), iconFont20.getHeight(), 180 * openAnim);
        iconFont20.drawString(FontUtil.DROPDOWN_ARROW, boxX + boxWidth - 11, boxY + iconFont20.getMiddleOfBox(boxHeight) + 1, new Color(0,0,0));
        RenderUtil.rotateEnd();

        if (opened || !openAnimation.isDone()) {
            float rectHeight = 15;
            float rectCount = 0;


            float modeHeight = (modeSetting.modes.size() - 1) * rectHeight;
            float modeY = boxY + boxHeight + 5;
            float modeX = boxX - .25f;
            RoundedUtil.drawRound(modeX, modeY, boxWidth, Math.max(4, modeHeight * openAnim), 4,
                    ColorUtil.applyOpacity(settingRectColor.brighter(), openAnim));


            boolean mouseOutsideRect = (mouseY < modeY || mouseY > modeY + modeHeight) || (mouseX < modeX || mouseX > modeX + boxWidth);

            selectionBox.setDirection(mouseOutsideRect ? Direction.BACKWARDS : Direction.FORWARDS);

            RoundedUtil.drawRound(modeX + 1.5f, (float) (modeY + 1.5f + selectionBoxY.getOutput()), boxWidth - 3, rectHeight - 3, 2.5f,
                    ColorUtil.applyOpacity(settingRectColor.brighter().brighter(), openAnim * selectionBox.getOutput().floatValue()));

            for (String mode : modeSetting.modes) {
                if(mode.equals(modeSetting.getMode())) continue;
                boolean hoveringMode = HoveringUtil.isHovering(modeX, modeY + rectCount * rectHeight, boxWidth, rectHeight, mouseX, mouseY);
                if(hoveringMode) {
                    this.hoveringMode = mode;
                }


                if(mode.equals(this.hoveringMode)) {
                    selectionBoxY.animate(rectCount * rectHeight, 17);
                }


                RenderUtil.resetColor();
                productregular14.drawString(mode, modeX + 5,
                        modeY + ((productregular14.getMiddleOfBox(rectHeight) + (rectHeight * rectCount)) * openAnimation.getOutput().floatValue()),
                        ColorUtil.applyOpacity(new Color(255,255,255), openAnim));

                if(themeSetting){
                    MutablePair<Color, Color> themeColors = Theme.getThemeColors(mode).apply(MutablePair::of);

                    themeColors.computeFirst(color -> ColorUtil.applyOpacity(color, openAnim));
                    themeColors.computeSecond(color -> ColorUtil.applyOpacity(color, openAnim));

                    float height = 8f;
                    float width = 8f;
                    float spacing = 3f;
                    float middleOfRect = rectHeight /2f - height /2f;
                    float v = modeY +
                            ((middleOfRect + (rectHeight * rectCount)) * openAnim);

                    RoundedUtil.drawRound(modeX + boxWidth - (width + 4 + (width + spacing)), v,
                            width, height, 2.25f, themeColors.getFirst());

                    RoundedUtil.drawRound(modeX + boxWidth - (width + 4), v,
                            width, height, 2.25f, themeColors.getSecond());


                }


                rectCount++;
            }

            //2 plus the increment of the mode rect height divided by the normal setting rect height
            countSize = 2 + ((.25f + (rectCount * (rectHeight / (realHeight / normalCount)))) * openAnimation.getOutput().floatValue());
        }else {
            countSize = 2;
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        ModeSetting modeSetting = getSetting();
        float boxHeight = 18;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 3;
        float boxX = x + 6;

        float boxWidth = width - 10;

        if (isClickable(boxY + boxHeight) && HoveringUtil.isHovering(boxX, boxY, boxWidth, boxHeight, mouseX, mouseY) && button == 1) {
            opened = !opened;
        }


        if(opened) {
            float rectHeight = 15;
            float rectCount = 0;
            float modeY = boxY + boxHeight + 4;
            float modeX = boxX - 1;

            for (String mode : modeSetting.modes) {
                if(mode.equals(modeSetting.getMode())) continue;
                boolean hoveringMode = HoveringUtil.isHovering(modeX, modeY + rectCount * rectHeight, boxWidth, rectHeight, mouseX, mouseY);
                if(isClickable((modeY + rectCount * rectHeight) + rectHeight) && hoveringMode && button == 0) {
                    modeSetting.setCurrentMode(mode);
                    opened = false;
                    return;
                }
                rectCount++;
            }
        }


    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
