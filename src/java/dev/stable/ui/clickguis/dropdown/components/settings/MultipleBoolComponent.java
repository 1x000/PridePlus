package dev.stable.ui.clickguis.dropdown.components.settings;

import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.MultipleBoolSetting;
import dev.stable.ui.clickguis.dropdown.components.SettingComponent;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.Direction;
import dev.stable.utils.animations.impl.DecelerateAnimation;
import dev.stable.utils.misc.HoveringUtil;
import dev.stable.utils.render.ColorUtil;
import dev.stable.utils.render.RoundedUtil;
import dev.stable.utils.tuples.Pair;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleBoolComponent extends SettingComponent<MultipleBoolSetting> {

    public float realHeight;
    public float normalCount;

    private final HashMap<BooleanSetting, Pair<Animation, Animation>> booleanSettingAnimations = new HashMap<>();

    private boolean opened;


    private final List<BooleanSetting> sortedSettings;
    public MultipleBoolComponent(MultipleBoolSetting setting) {
        super(setting);

        sortedSettings = setting.getBoolSettings().stream().sorted(Comparator.comparingDouble(this::getEnabledWidth)).collect(Collectors.toList());

        for (BooleanSetting booleanSetting : sortedSettings) {
            booleanSettingAnimations.put(booleanSetting, Pair.of(new DecelerateAnimation(250, 1), new DecelerateAnimation(250, 1)));
        }

        normalCount = 2;
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private float additionalHeight = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float boxHeight = 15;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 4;
        float boxX = x + 6;
        float boxWidth = width - 10;


        float enabledCount = sortedSettings.stream().filter(BooleanSetting::isEnabled).count();

        Color outlineColor = ColorUtil.interpolateColorC(settingRectColor.brighter(), clientColors.getSecond(),enabledCount / sortedSettings.size());


        Color rectColor = new Color(50,50,50);


        // Gui.drawRect2(x,y,width,height, -1);
        //RoundedUtil.drawRound(x + 5, boxY, width - 10, boxHeight + additionalHeight, 2, outlineColor);


        RoundedUtil.drawRound(x + 6, boxY + 1, width - 12, (boxHeight - 2) + additionalHeight, 2, new Color(0,0,0,50));

        productregular14.drawString(getSetting().name, x + 6, y + 4, new Color(0,0,0));

        float addHeight = 0;
        float xOffset = 2;
        float yOffset = 3;
        float spacing = 3;

        //The available space for the settings
        float avaliableWidth = boxWidth - 2;

        //Sort the boolean settings to have the smallest width ones at the top


        for (BooleanSetting setting : sortedSettings) {
            float enabledWidth = getEnabledWidth(setting);
            float enabledHeight = rubikFont14.getHeight() + 4;

            //If the width exceeds the available space, we need to add a new line
            if (xOffset + enabledWidth > avaliableWidth) {
                xOffset = 2;
                yOffset += enabledHeight + spacing;

                //Calculates the amount of space that the new line will take
                addHeight += (yOffset + enabledHeight + spacing) - ((boxHeight) + addHeight);
            }

            float enabledX = boxX + xOffset;
            float enabledY = boxY + yOffset;

            boolean hovering = HoveringUtil.isHovering(enabledX, enabledY, enabledWidth, enabledHeight, mouseX, mouseY);

            Animation hoverAnimation = booleanSettingAnimations.get(setting).getFirst();
            Animation toggleAnimation = booleanSettingAnimations.get(setting).getSecond();

            hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
            toggleAnimation.setDirection(setting.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

            Color rectColorBool = ColorUtil.interpolateColorC(settingRectColor.brighter().brighter(),
                    clientColors.getSecond(), toggleAnimation.getOutput().floatValue());

            rectColorBool = ColorUtil.interpolateColorC(rectColorBool, rectColorBool.brighter(), hoverAnimation.getOutput().floatValue());



            RoundedUtil.drawRound(enabledX, enabledY, enabledWidth, enabledHeight, 3, rectColorBool);

            rubikFont14.drawString(setting.name, enabledX + 2, enabledY + 2,
                    ColorUtil.applyOpacity(new Color(255,255,255), .5f + (.5f * toggleAnimation.getOutput().floatValue())));


            xOffset += enabledWidth + spacing;
        }

        additionalHeight = addHeight;

        float increment = (((boxY - y) + boxHeight + addHeight + 3) - realHeight) / (realHeight / normalCount);


        countSize = normalCount + increment;
    }

    private float getEnabledWidth(BooleanSetting setting) {
        return (rubikFont14.getStringWidth(setting.name) + 4);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float boxHeight = 15;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 4;
        float boxX = x + 6;
        float boxWidth = width - 10;

        float xOffset = 2;
        float yOffset = 3;
        float spacing = 3;

        //The available space for the settings
        float avaliableWidth = boxWidth - 2;

        //Sort the boolean settings to have the smallest width ones at the top

        for (BooleanSetting setting : sortedSettings) {
            float enabledWidth = getEnabledWidth(setting);
            float enabledHeight = rubikFont14.getHeight() + 4;

            //If the width exceeds the available space, we need to add a new line
            if (xOffset + enabledWidth > avaliableWidth) {
                xOffset = 2;
                yOffset += enabledHeight + spacing;
            }

            float enabledX = boxX + xOffset;
            float enabledY = boxY + yOffset;
            boolean hovered = HoveringUtil.isHovering(enabledX, enabledY, enabledWidth, enabledHeight, mouseX, mouseY);

            if(isClickable(enabledY + enabledHeight) && hovered && button == 0) {
                setting.toggle();
            }

            xOffset += enabledWidth + spacing;
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
