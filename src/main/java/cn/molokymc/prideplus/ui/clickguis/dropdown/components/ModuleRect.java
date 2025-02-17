package cn.molokymc.prideplus.ui.clickguis.dropdown.components;

import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.Setting;
import cn.molokymc.prideplus.module.settings.impl.*;
import cn.molokymc.prideplus.ui.Screen;
import cn.molokymc.prideplus.ui.clickguis.dropdown.components.settings.*;
import cn.molokymc.prideplus.ui.sidegui.utils.TooltipObject;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.animations.impl.EaseInOutQuad;
import cn.molokymc.prideplus.utils.animations.impl.EaseOutSine;
import cn.molokymc.prideplus.utils.misc.HoveringUtil;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.Theme;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import cn.molokymc.prideplus.utils.tuples.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleRect implements Screen {

    public final Module module;
    @Getter
    @Setter
    private int searchScore;
    private final Animation toggleAnimation = new EaseInOutQuad(300, 1);
    private final Animation hoverAnimation = new EaseOutSine(400, 1, Direction.BACKWARDS);
    private final Animation hoverKeybindAnimation = new DecelerateAnimation(200, 1, Direction.BACKWARDS);
    private final Animation settingAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    public final TooltipObject tooltipObject = new TooltipObject();
    private final TimerUtil timerUtil = new TimerUtil();

    @Getter
    private boolean typing;
    public float x, y, width, height, panelLimitY, alpha;

    @Getter
    private double settingSize = 1;
    private final List<SettingComponent> settingComponents;

    public ModuleRect(Module module) {
        this.module = module;
        settingComponents = new ArrayList<>();
        for (Setting setting : module.getSettingsList()) {
            if (setting instanceof KeybindSetting) {
                settingComponents.add(new KeybindComponent((KeybindSetting) setting));
            }
            if (setting instanceof BooleanSetting) {
                settingComponents.add(new BooleanComponent((BooleanSetting) setting));
            }
            if (setting instanceof ModeSetting) {
                settingComponents.add(new ModeComponent((ModeSetting) setting));
            }
            if (setting instanceof NumberSetting) {
                settingComponents.add(new NumberComponent((NumberSetting) setting));
            }
            if (setting instanceof MultipleBoolSetting) {
                settingComponents.add(new MultipleBoolComponent((MultipleBoolSetting) setting));
            }
            if (setting instanceof StringSetting) {
                settingComponents.add(new StringComponent((StringSetting) setting));
            }
            if (setting instanceof ColorSetting) {
                settingComponents.add(new ColorComponent((ColorSetting) setting));
            }
        }
    }

    @Override
    public void initGui() {
        settingAnimation.setDirection(Direction.BACKWARDS);
        toggleAnimation.setDirection(Direction.BACKWARDS);

        if (settingComponents != null) {
            settingComponents.forEach(SettingComponent::initGui);
        }

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (module.isExpanded()) {
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;
                settingComponent.keyTyped(typedChar, keyCode);
            }
        }
    }

    private double actualSettingCount;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        toggleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        settingAnimation.setDirection(module.isExpanded() ? Direction.FORWARDS : Direction.BACKWARDS);


        boolean hoveringModule = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);


        hoverAnimation.setDirection(hoveringModule ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hoveringModule ? 250 : 400);

        boolean hoveringText = HoveringUtil.isHovering(x + 5, y + rubikFont12.getMiddleOfBox(height), rubikFont12.getStringWidth(module.getName()), rubikFont12.getHeight(), mouseX, mouseY);

        tooltipObject.setTip(module.getDescription());
        tooltipObject.setHovering(hoveringText);


        Theme theme = Theme.getCurrentTheme();

        Pair<Color, Color> colors = theme.getColors().apply((color1, color2) -> {
            return Pair.of(ColorUtil.applyOpacity(color1, alpha), ColorUtil.applyOpacity(color2, 0.5f));
        });


        Color rectColor = new Color(35, 37, 43, (int) (alpha * 1.1));
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);

        float textAlpha = .4f;
        Color moduleTextColor = ColorUtil.applyOpacity(textColor, textAlpha + (.4f * toggleAnimation.getOutput().floatValue()));


        if (module.isEnabled() || !toggleAnimation.isDone()) {
            Color toggleColor = colors.getSecond();


            toggleColor = ColorUtil.interpolateColorC(ColorUtil.applyOpacity(Color.BLACK, 0f), ColorUtil.applyOpacity(Color.WHITE, 0f), hoverAnimation.getOutput().floatValue());


            rectColor = ColorUtil.interpolateColorC(rectColor, toggleColor, toggleAnimation.getOutput().floatValue());
        }


        RenderUtil.resetColor();
        Gui.drawRect2(x, y, width, height, ColorUtil.interpolateColor(rectColor, ColorUtil.brighter(rectColor, .8f), hoverAnimation.getOutput().floatValue()));

        RenderUtil.resetColor();

        productregular16.drawString((module.isEnabled() ? "§l" : "") + module.getName(), x + width / 2.12f - (tenacityBoldFont12.getStringWidth((module.isEnabled() ? "§l" : "") + module.getName())) / 2, y + tenacityBoldFont12.getMiddleOfBox(height), new Color(0,0,0));


        Color settingRectColor = ColorUtil.tripleColor(52, 25);

        double settingHeight = (actualSettingCount) * settingAnimation.getOutput();
        actualSettingCount = 0;
        actualSettingCount = 0;
        if (module.isExpanded() || !settingAnimation.isDone()) {
            float settingRectHeight = 16;
            Gui.drawRect2(x, y + height, width, (float) (settingHeight * settingRectHeight), new Color(200, 200, 255, 0).getRGB());

            if (!settingAnimation.isDone()) {
                RenderUtil.scissorStart(x, y + height, width, settingHeight * settingRectHeight);
            }


            typing = false;
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;

                settingComponent.panelLimitY = panelLimitY;
                settingComponent.settingRectColor = settingRectColor;
                settingComponent.textColor = textColor;
                settingComponent.clientColors = colors;
                settingComponent.alpha = alpha;
                settingComponent.x = x;
                settingComponent.y = (float) (y + height + ((actualSettingCount * settingRectHeight)));
                settingComponent.width = width;
                settingComponent.typing = typing;

                if (settingComponent instanceof ModeComponent) {
                    ModeComponent modeComponent = (ModeComponent) settingComponent;
                    modeComponent.realHeight = settingRectHeight * modeComponent.normalCount;
                }
                if (settingComponent instanceof MultipleBoolComponent) {
                    MultipleBoolComponent multipleBoolComponent = (MultipleBoolComponent) settingComponent;
                    multipleBoolComponent.realHeight = settingRectHeight * multipleBoolComponent.normalCount;
                }

                if (settingComponent instanceof ColorComponent) {
                    ColorComponent colorComponent = (ColorComponent) settingComponent;
                    colorComponent.realHeight = settingRectHeight;
                }

                settingComponent.height = settingRectHeight * settingComponent.countSize;

                settingComponent.drawScreen(mouseX, mouseY);

                if (settingComponent.typing) typing = true;

                actualSettingCount += settingComponent.countSize;
            }

            if (!settingAnimation.isDone() || GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)) {
                RenderUtil.scissorEnd();
            }

        }
        settingSize = settingHeight;

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hoveringModule = isClickable(y, panelLimitY) && HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if (module.isExpanded() && settingAnimation.finished(Direction.FORWARDS)) {
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;
                settingComponent.mouseClicked(mouseX, mouseY, button);
            }
        }

        if (hoveringModule) {
            switch (button) {
                case 0:
                    toggleAnimation.setDirection(!module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
                    module.toggleSilent();
                    break;
                case 1:
                    module.setExpanded(!module.isExpanded());
                    break;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (module.isExpanded()) {
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;
                settingComponent.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    public boolean isClickable(float y, float panelLimitY) {
        return y > panelLimitY && y < panelLimitY + Module.allowedClickGuiHeight;
    }


}
