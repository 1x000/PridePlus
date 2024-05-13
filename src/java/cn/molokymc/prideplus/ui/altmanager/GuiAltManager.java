package cn.molokymc.prideplus.ui.altmanager;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.module.impl.render.NotificationsMod;
import cn.molokymc.prideplus.ui.Screen;
import cn.molokymc.prideplus.ui.altmanager.helpers.Alt;
import cn.molokymc.prideplus.ui.altmanager.helpers.AltManagerUtils;
import cn.molokymc.prideplus.ui.altmanager.panels.AltPanel;
import cn.molokymc.prideplus.ui.altmanager.panels.InfoPanel;
import cn.molokymc.prideplus.ui.altmanager.panels.LoginPanel;
import cn.molokymc.prideplus.ui.altmanager.panels.MicrosoftInfoPanel;
import cn.molokymc.prideplus.ui.mainmenu.CustomMainMenu;
import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import cn.molokymc.prideplus.ui.sidegui.utils.ToggleButton;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.objects.TextField;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.GLUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiAltManager extends GuiScreen {
    private final AltManagerUtils utils = new AltManagerUtils();
    public Alt currentSessionAlt;
    private List<Panel> panels;
    public final TextField searchField = new TextField(Utils.rubikFont20);
    public final ToggleButton filterBanned = new ToggleButton("Filter banned accounts");
    private final AltPanel.AltRect altRect = new AltPanel.AltRect(null);

    public GuiAltManager() {
        if (panels == null) {
            panels = new ArrayList<>();
            panels.add(new InfoPanel());
            panels.add(new LoginPanel());
            panels.add(new MicrosoftInfoPanel());
            panels.add(new AltPanel());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Utils.mc.displayGuiScreen(new CustomMainMenu());
            searchField.setFocused(false);
        }
        searchField.keyTyped(typedChar, keyCode);
        panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }

    @Override
    public void initGui() {
        if (Utils.mc.gameSettings.guiScale != 2) {
            Client.prevGuiScale = Utils.mc.gameSettings.guiScale;
            Client.updateGuiScale = true;
            Utils.mc.gameSettings.guiScale = 2;
            Utils.mc.resize(Utils.mc.displayWidth - 1, Utils.mc.displayHeight);
            Utils.mc.resize(Utils.mc.displayWidth + 1, Utils.mc.displayHeight);
        }

        panels.forEach(Screen::initGui);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        utils.writeAltsToFile();
        GLUtil.startBlend();

        ScaledResolution sr = new ScaledResolution(Utils.mc);

        Gui.drawRect2(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtil.tripleColor(35).getRGB());


        AltPanel altPanel = (AltPanel) panels.get(3);
        int count = 0;
        int seperation = 0;
        for (Panel panel : panels) {
            boolean notAltPanel = !(panel instanceof AltPanel);
            if (notAltPanel) {
                panel.setX(16);
                panel.setY(20 + seperation);
                panel.setWidth(325);
            } else {
                panel.setX(36 + 325);
            }
            panel.drawScreen(mouseX, mouseY);
            if (notAltPanel) {
                seperation += panel.getHeight() + (count >= 1 ? 10 : 25);
            }
            count++;
        }


        if (currentSessionAlt != null) {
            altRect.setAlt(currentSessionAlt);
            altRect.setHeight(40);
            altRect.setX(altPanel.getX() + 10);
            altRect.setY(altPanel.getY() - (altRect.getHeight() + 10));
            altRect.setWidth(Math.min(160, searchField.getXPosition() - 10 - altPanel.getX()));
            altRect.setClickable(true);
            altRect.setSelected(false);
            altRect.setBackgroundColor(ColorUtil.tripleColor(27));
            altRect.setRemoveShit(true);
            altRect.drawScreen(mouseX, mouseY);
            Utils.tenacityBoldFont22.drawCenteredString("Current Account", altRect.getX() + altRect.getWidth() / 2f,
                    altRect.getY() - (Utils.tenacityBoldFont22.getHeight() + 5), -1);
        }



        /* Search bar */
        searchField.setRadius(5);
        searchField.setFill(ColorUtil.tripleColor(17, 1));
        searchField.setOutline(ColorUtil.applyOpacity(Color.WHITE, 0));

        searchField.setHeight(20.5F);
        searchField.setWidth(145.5F);

        searchField.setXPosition(width - searchField.getRealWidth() - 20);
        searchField.setYPosition(45);
        searchField.setBackgroundText("Search");
        searchField.drawTextBox();
        /* End search bar */

        /* Filter banned button */
        filterBanned.setX(searchField.getXPosition() + 85);
        filterBanned.setBypass(true);
        filterBanned.setAlpha(1);
        filterBanned.setY(searchField.getYPosition() - (filterBanned.getWH() + 10));
        filterBanned.drawScreen(mouseX, mouseY);
        /* End filter banned button */

        Client.INSTANCE.getModuleCollection().getModule(NotificationsMod.class).render();
        if (Alt.stage != 0) {
            AltPanel.loadingAltRect = null;
        }
        switch (Alt.stage) {
            case 1:
                NotificationManager.post(NotificationType.INFO, "Alt Manager", "Invalid credentials!", 3);
                Alt.stage = 0;
                break;
            case 2:
                NotificationManager.post(NotificationType.SUCCESS, "Alt Manager", "Logged in successfully!", 3);
                Alt.stage = 0;
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        filterBanned.mouseClicked(mouseX, mouseY, mouseButton);
        panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
        if (currentSessionAlt != null) {
            altRect.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public void onGuiClosed() {
        if (Client.updateGuiScale) {
            Utils.mc.gameSettings.guiScale = Client.prevGuiScale;
            Client.updateGuiScale = false;
        }
    }

    public AltManagerUtils getUtils() {
        return utils;
    }

    public AltPanel getAltPanel() {
        return (AltPanel) panels.get(3);
    }

    public boolean isTyping() {
        return searchField.isFocused() || ((LoginPanel) panels.get(1)).textFields.stream().anyMatch(TextField::isFocused);
    }

}
