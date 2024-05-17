package cn.molokymc.prideplus.ui.sidegui;

import cn.molokymc.prideplus.ui.Screen;
import cn.molokymc.prideplus.ui.sidegui.panels.Panel;
import cn.molokymc.prideplus.ui.sidegui.panels.infopanel.InfoPanel;
import cn.molokymc.prideplus.ui.sidegui.panels.searchpanel.SearchPanel;
import cn.molokymc.prideplus.ui.sidegui.utils.TooltipObject;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.misc.HoveringUtil;
import cn.molokymc.prideplus.utils.misc.MathUtils;
import cn.molokymc.prideplus.utils.objects.Drag;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class SideGUI implements Screen {

    @Setter
    private boolean focused;
    private float rectWidth, rectHeight;
    private final Animation openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation clickAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private SideGUIHotbar hotbar = new SideGUIHotbar();
    private HashMap<String, Panel> panels;
    private final List<TooltipObject> tooltips = new ArrayList<>();
    public boolean typing = false;
    private final Color greenEnabledColor = new Color(70, 220, 130);
    private final Color redBadColor = new Color(209, 56, 56);
    private final Animation formFadeAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    private Drag drag;
    private TimerUtil timerUtil;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        if (drag != null) {
            drag.onDraw(mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
        if (panels == null) {
            panels = new HashMap<>();
            panels.put("Search", new SearchPanel());
            panels.put("Info", new InfoPanel());
        }

        hotbar.initGui();
        panels.values().forEach(Panel::initGui);
        focused = false;
        timerUtil = new TimerUtil();
        rectWidth = 550;
        rectHeight = 350;
        ScaledResolution sr = new ScaledResolution(Utils.mc);
        drag = new Drag(sr.getScaledWidth() - 40, sr.getScaledHeight() / 2f - rectHeight / 2f);
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (focused) {
            hotbar.keyTyped(typedChar, keyCode);
            if (keyCode == Keyboard.KEY_ESCAPE) {
                if (hotbar.searchField.isFocused() || !hotbar.searchField.getText().equals("")) {
                    hotbar.setCurrentPanel(hotbar.getCarouselButtons().getCurrentButton());
                }
                hotbar.searchField.setText("");
                hotbar.searchField.setFocused(false);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

    }

    private float animateX = 0;
    private boolean canSnap;


    public void drawForEffects(boolean bloom) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = SideGUI.isHovering(drag.getX(), drag.getY(), rectWidth, rectHeight, mouseX, mouseY);
        if (!focused && hovering) {
            focused = true;
        } else if (focused) {
            // Only want to drag when clicking the top
            boolean hoveringTop = SideGUI.isHovering(drag.getX(), drag.getY(), rectWidth, 40, mouseX, mouseY);
            if (!hotbar.searchField.isFocused() && !hotbar.getCarouselButtons().isHovering()) {
                drag.onClick(mouseX, mouseY, button, hoveringTop);
            }
            hotbar.mouseClicked(mouseX, mouseY, button);


            if (panels.containsKey(hotbar.getCurrentPanel())) {
                panels.get(hotbar.getCurrentPanel()).mouseClicked(mouseX, mouseY, button);
            }

        }


    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        drag.onRelease(button);
        //If the transparent animation is shown and the user released the mouse then snap it back to the right
        if (canSnap) {
            focused = false;
        }
    }


    public void addTooltip(TooltipObject tooltip) {
        if (tooltips.contains(tooltip)) return;
        tooltips.add(tooltip);
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
    }

}
