package cn.molokymc.prideplus.ui.clickguis.dropdown;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.impl.movement.InventoryMove;
import cn.molokymc.prideplus.ui.sidegui.SideGUI;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.*;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.tuples.Pair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class DropdownClickGUI extends GuiScreen {

    private final Pair<Animation, Animation> openingAnimations = Pair.of(
            new EaseOutSine(100, 1),
            new EaseOutSine(100, .4f));


    private List<CategoryPanel> categoryPanels;

    public boolean binding;


    public static boolean gradient;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.onDrag(mouseX, mouseY);
        }
        Client.INSTANCE.getSideGui().onDrag(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        openingAnimations.use((fade, opening) -> {
            fade.setDirection(Direction.FORWARDS);
            opening.setDirection(Direction.FORWARDS);
        });


        if (categoryPanels == null) {
            categoryPanels = new ArrayList<>();
            for (Category category : Category.values()) {
                categoryPanels.add(new CategoryPanel(category, openingAnimations));
            }
        }

        Client.INSTANCE.getSideGui().initGui();
        Client.INSTANCE.getSearchBar().initGui();


        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.initGui();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && !binding) {
            if (Client.INSTANCE.getSearchBar().isFocused()) {
                Client.INSTANCE.getSearchBar().getSearchField().setText("");
                Client.INSTANCE.getSearchBar().getSearchField().setFocused(false);
                return;
            }

            if (Client.INSTANCE.getSideGui().isFocused()) {
                Client.INSTANCE.getSideGui().setFocused(false);
                return;
            }

            Client.INSTANCE.getSearchBar().getOpenAnimation().setDirection(Direction.BACKWARDS);
            openingAnimations.use((fade, opening) -> {
                fade.setDirection(Direction.BACKWARDS);
                opening.setDirection(Direction.BACKWARDS);
            });
        }
        Client.INSTANCE.getSideGui().keyTyped(typedChar, keyCode);
        Client.INSTANCE.getSearchBar().keyTyped(typedChar, keyCode);
        categoryPanels.forEach(categoryPanel -> categoryPanel.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        binding = categoryPanels.stream().anyMatch(CategoryPanel::isTyping) ||
                (Client.INSTANCE.getSideGui().isFocused() && Client.INSTANCE.getSideGui().typing) || Client.INSTANCE.getSearchBar().isTyping();


        //Gui.drawRect2(0,0, width, height, ColorUtil.applyOpacity(0, Client.INSTANCE.getSearchBar().getFocusAnimation().getOutput().floatValue() * .25f));
        InventoryMove.updateStates();

        // If the closing animation finished then change the gui screen to null
        if (openingAnimations.getSecond().finished(Direction.BACKWARDS)) {
            Utils.mc.displayGuiScreen(null);
            return;
        }


        boolean focusedConfigGui = Client.INSTANCE.getSideGui().isFocused() || Client.INSTANCE.getSearchBar().isTyping();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;
        ScaledResolution sr = new ScaledResolution(Utils.mc);


        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);

        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.drawScreen(fakeMouseX, fakeMouseY);
        }

        RenderUtil.scaleEnd();
        categoryPanels.forEach(categoryPanel -> categoryPanel.drawToolTips(fakeMouseX, fakeMouseY));

        //Draw Side GUI

        SideGUI sideGUI = Client.INSTANCE.getSideGui();
        sideGUI.getOpenAnimation().setDirection(openingAnimations.getFirst().getDirection());
        sideGUI.drawScreen(mouseX, mouseY);

    }

    public void renderEffects() {
        ScaledResolution sr = new ScaledResolution(Utils.mc);
        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.renderEffects();
        }
        RenderUtil.scaleEnd();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean focused = Client.INSTANCE.getSideGui().isFocused();
        Client.INSTANCE.getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
        Client.INSTANCE.getSearchBar().mouseClicked(mouseX, mouseY, mouseButton);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        boolean focused = Client.INSTANCE.getSideGui().isFocused();
        Client.INSTANCE.getSideGui().mouseReleased(mouseX, mouseY, state);
        Client.INSTANCE.getSearchBar().mouseReleased(mouseX, mouseY, state);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseReleased(mouseX, mouseY, state));
        }
    }

    @Override
    public void onGuiClosed() {

    }


}
