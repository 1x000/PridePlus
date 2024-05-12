package dev.stable.module.impl.render;

import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.ui.clickguis.compact.CompactClickgui;
import dev.stable.ui.clickguis.dropdown.DropdownClickGUI;
import dev.stable.ui.clickguis.modern.ModernClickGui;
import dev.stable.ui.clickguis.test.TestClickgui;
import dev.stable.utils.Utils;
import dev.stable.utils.misc.SoundUtils;
import dev.stable.utils.render.Theme;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ClickGUIMod extends Module {

    public static final ModeSetting clickguiMode = new ModeSetting("ClickGui", "Dropdown", "Dropdown");
    public static final ModeSetting scrollMode = new ModeSetting("Scroll Mode", "Screen Height", "Screen Height", "Value");
    public static final BooleanSetting gradient = new BooleanSetting("Gradient", false);
    public static final BooleanSetting outlineAccent = new BooleanSetting("Outline Accent", true);
    public static final BooleanSetting transparent = new BooleanSetting("Transparent", false);
    public static final BooleanSetting walk = new BooleanSetting("Allow Movement", true);
    public static final NumberSetting clickHeight = new NumberSetting("Tab Height", 250, 500, 100, 1);
    public static final BooleanSetting rescale = new BooleanSetting("Rescale GUI", true);

    public static final DropdownClickGUI dropdownClickGui = new DropdownClickGUI();
    public static final dev.stable.ui.clickguis.test.TestClickgui TestClickgui = new TestClickgui();
    public static final ModernClickGui modernClickGui = new ModernClickGui();
    public static final CompactClickgui compactClickgui = new CompactClickgui();

    private int activeCategory = 0;
    private Category activeCategory2 = Category.COMBAT;

    public static int prevGuiScale;

    public ClickGUIMod() {
        super("ClickGUI","点击界面", Category.DISPLAY, "Displays modules");
        clickHeight.addParent(scrollMode, selection -> selection.is("Value"));

        gradient.addParent(clickguiMode, selection -> selection.is("Dropdown") && !Theme.getCurrentTheme().isGradient());
        transparent.addParent(clickguiMode, selection -> selection.is("Dropdown"));
        outlineAccent.addParent(clickguiMode, selection -> selection.is("Dropdown"));
        scrollMode.addParent(clickguiMode, selection -> selection.is("Dropdown"));

        this.addSettings(clickguiMode, scrollMode, outlineAccent, gradient, transparent, walk, clickHeight, rescale);
        this.setKey(Keyboard.KEY_RSHIFT);
    }

    public void toggle() {
        this.onEnable();
        //SoundUtils.playSound(new ResourceLocation("Stable/Sounds/notification/clickguiopen.wav"), .8f);
    }

    public void onEnable() {
        if (rescale.isEnabled()) {
            prevGuiScale = Utils.mc.gameSettings.guiScale;
            Utils.mc.gameSettings.guiScale = 2;
        }
        switch (clickguiMode.getMode()) {
            case "Dropdown":
                Utils.mc.displayGuiScreen(dropdownClickGui);
                break;
            case "Modern":
                Utils.mc.displayGuiScreen(modernClickGui);
                break;
            case "Compact":
                Utils.mc.displayGuiScreen(compactClickgui);
                break;
            case "Test":
                Utils.mc.displayGuiScreen(TestClickgui);
                break;
        }
    }


    public int getActiveCategoryy() {
        return activeCategory;
    }

    public Category getActiveCategory() {
        return activeCategory2;
    }

    public void setActiveCategory(int activeCategory) {
        this.activeCategory = activeCategory;
    }

    public void setActiveCategory(Category activeCategory) {
        this.activeCategory2 = activeCategory;
    }

}
