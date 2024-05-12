package dev.stable.module.impl.render;

import dev.stable.Client;
import dev.stable.event.impl.render.Render2DEvent;
import dev.stable.event.impl.render.ShaderEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.ModuleCollection;
import dev.stable.module.settings.ParentAttribute;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.Direction;
import dev.stable.utils.objects.Dragging;
import dev.stable.utils.render.ColorUtil;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.skidfont.FontDrawer;
import dev.stable.utils.skidfont.FontManager;
import dev.stable.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import static dev.stable.module.impl.render.HUDMod.Language;


public class ArrayListMod extends Module {

    public final BooleanSetting importantModules = new BooleanSetting("Important", false);
    private final ModeSetting textShadow = new ModeSetting("Text Shadow", "Black", "Colored", "Black", "None");
    public static final ModeSetting fontmode = new ModeSetting("Font", "Rubik", "Rubik", "Product");
    private final ModeSetting rectangle = new ModeSetting("Rectangle", "Top", "None", "Top", "Side","Rise");
    private final BooleanSetting partialGlow = new BooleanSetting("Partial Glow", true);
    private final BooleanSetting minecraftFont = new BooleanSetting("Minecraft Font", false);
    public static final BooleanSetting splicedName = new BooleanSetting("Spliced Name", false);

    public final NumberSetting height = new NumberSetting("Height", 11, 20, 9, .5f);
    private final ModeSetting animation = new ModeSetting("Animation", "Scale in", "Move in", "Scale in");
    private final NumberSetting colorIndex = new NumberSetting("Color Seperation", 20, 100, 5, 1);
    private final NumberSetting colorSpeed = new NumberSetting("Color Speed", 15, 30, 2, 1);
    private final BooleanSetting background = new BooleanSetting("Background", true);
    private final BooleanSetting backgroundColor = new BooleanSetting("Background Color", false);
    private final NumberSetting backgroundAlpha = new NumberSetting("Background Alpha", .35, 1, 0, .01);

    //    public AbstractFontRenderer font = csgoFont.size(18);
    public List<Module> modules;
    FontDrawer font = FontManager.edit16;
    public ArrayListMod() {
        super("Arraylist","功能显示", Category.DISPLAY, "Displays your active modules");
        addSettings( fontmode,importantModules, rectangle, partialGlow, textShadow, splicedName, height, animation,
                colorIndex, colorSpeed, background, backgroundColor, backgroundAlpha);
        backgroundAlpha.addParent(background, ParentAttribute.BOOLEAN_CONDITION);
        backgroundColor.addParent(background, ParentAttribute.BOOLEAN_CONDITION);
        partialGlow.addParent(rectangle, modeSetting -> !modeSetting.is("None"));
        if (!enabled) this.toggleSilent();
    }

    public void getModulesAndSort() {
            if(fontmode.is("Rubik")){
                font = FontManager.edit16;
            }
            if(fontmode.is("Product")){
                font = FontManager.product_sans_regular_18;
            }
        if (modules == null || ModuleCollection.reloadModules) {
            List<Class<? extends Module>> hiddenModules = Client.INSTANCE.getModuleCollection().getHiddenModules();
            List<Module> moduleList = Client.INSTANCE.getModuleCollection().getModules();
            moduleList.removeIf(module -> hiddenModules.stream().anyMatch(moduleClass -> moduleClass == module.getClass()));
            modules = moduleList;
        }
        if (Language.is("Chinese")) {
            modules.sort(Comparator.<Module>comparingDouble(m -> {
                String name = HUDMod.get(m.getChineseName() + (m.hasMode() ? " " + m.getSuffix() : ""));
                return font.getStringWidth(applyText(name));
            }).reversed());
        }
        else if(Language.is("English")){
            modules.sort(Comparator.<Module>comparingDouble(m -> {
                String name = HUDMod.get(m.getName() + (m.hasMode() ? " " + m.getSuffix() : ""));
                return font.getStringWidth(applyText(name));
            }).reversed());
        }
    }


    public Dragging arraylistDrag = Client.INSTANCE.createDrag(this, "arraylist", 2, 1);

    public String longest = "";

    @Override
    public void onShaderEvent(ShaderEvent e) {
        if (modules == null) return;
        float yOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        int count = 0;
        for (Module module : modules) {
            if (importantModules.isEnabled() && module.getCategory() == Category.RENDER || module.getCategory() == Category.DISPLAY) continue;
            final Animation moduleAnimation = module.getAnimation();
            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;


            String displayText = HUDMod.get(module.getChineseName() + (module.hasMode() ? " §7" + module.getSuffix() : ""));

            if(Language.is("Chinese")) displayText = HUDMod.get(module.getChineseName() + (module.hasMode() ? " §7" + module.getSuffix() : ""));
            else if(Language.is("English")) displayText = HUDMod.get(module.getName() + (module.hasMode() ? " §7" + module.getSuffix() : ""));

            displayText = applyText(displayText);
            float textWidth = font.getStringWidth(displayText);

            float xValue = sr.getScaledWidth() - (arraylistDrag.getX());

            boolean flip = xValue <= sr.getScaledWidth() / 2f;
            float x = flip ? xValue : sr.getScaledWidth() - (textWidth + arraylistDrag.getX());

            float y = yOffset + arraylistDrag.getY();

            float heightVal = height.getValue().floatValue() + 1;
            boolean scaleIn = false;
            switch (animation.getMode()) {
                case "Move in":
                    if (flip) {
                        x -= Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (sr.getScaledWidth() - (arraylistDrag.getX() + textWidth)));
                    } else {
                        x += Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (arraylistDrag.getX() + textWidth));
                    }
                    break;
                case "Scale in":
                    if (!moduleAnimation.isDone()) {
                        RenderUtil.scaleStart((float) (x + font.getStringWidth(displayText) / 2f), (float) (y + heightVal / 2 - font.getHeight() / 2f), (float) moduleAnimation.getOutput().floatValue());
                    }
                    scaleIn = true;
                    break;
            }


            int index = (int) (count * colorIndex.getValue());
            Pair<Color, Color> colors = HUDMod.getClientColors();

            Color textcolor = ColorUtil.interpolateColorsBackAndForth(colorSpeed.getValue().intValue(), index, colors.getFirst(), colors.getSecond(), false);

            if (HUDMod.isRainbowTheme()) {
                textcolor = ColorUtil.rainbow(colorSpeed.getValue().intValue(), index, HUDMod.color1.getRainbow().getSaturation(), 1, 1);
            }

            if (background.isEnabled()) {
                float offset = minecraftFont.isEnabled() ? 4 : 5;
                int rectColor = e.getBloomOptions().getSetting("Arraylist").isEnabled() ? textcolor.getRGB() : (rectangle.getMode().equals("Outline") && partialGlow.isEnabled() ? textcolor.getRGB() : Color.BLACK.getRGB());


                Gui.drawRect2(x - 2,y - 2, font.getStringWidth(displayText) + offset, heightVal,
                        scaleIn ? ColorUtil.applyOpacity(rectColor, moduleAnimation.getOutput().floatValue()) : rectColor);

                float offset2 = minecraftFont.isEnabled() ? 1 : 0;

                int rectangleColor = partialGlow.isEnabled() ? textcolor.getRGB() : Color.BLACK.getRGB();

                if (scaleIn) {
                    rectangleColor = ColorUtil.applyOpacity(rectangleColor, moduleAnimation.getOutput().floatValue());
                }

                switch (rectangle.getMode()) {
                    default:
                        break;
                    case "Top":
                        if (count == 0) {
                            Gui.drawRect2(x, y - 1, textWidth - (offset2), 9, rectangleColor);
                        }
                        break;
                    case "Rise":
                        if (flip) {
                            Gui.drawRect2(x - 3.4D, (y + 1.0F), 1.5D, 9.5D, textcolor.getRGB());
                            break;
                        }
                        Gui.drawRect2((x + textWidth) + 3.2D, (y + 1.0F), 1.535D, 9.5D, textcolor.getRGB());
                        break;
                    case "Side":
                        if (flip) {
                            Gui.drawRect2(x, y, 9, heightVal, textcolor.getRGB());
                        } else {
                            Gui.drawRect2(x + textWidth, y, 9, heightVal, rectangleColor);
                        }
                        break;
                    case "Outline":
                        break;
                }
            }


            if (animation.is("Scale in") && !moduleAnimation.isDone()) {
                RenderUtil.scaleEnd();
            }

            yOffset += moduleAnimation.getOutput().floatValue() * heightVal;
            count++;
        }
    }

    Module lastModule;
    int lastCount;

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        getModulesAndSort();

        String longestModule = "";
        float longestWidth = 0;
        double yOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        int count = 0;
        for (Module module : modules) {
            if (importantModules.isEnabled() && module.getCategory() == Category.RENDER || module.getCategory() == Category.DISPLAY) continue;
            final Animation moduleAnimation = module.getAnimation();

            moduleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;


            String displayText = HUDMod.get(module.getChineseName() + (module.hasMode() ? (module.getCategory().equals(Category.EXPLOIT) ? " §c" : " §7") + module.getSuffix() : ""));
            if(Language.is("Chinese")) displayText = HUDMod.get(module.getChineseName() + (module.hasMode() ? (module.getCategory().equals(Category.EXPLOIT) ? " §c" : " §7") + module.getSuffix() : ""));
            else if(Language.is("English")) displayText = HUDMod.get(module.getName() + (module.hasMode() ? (module.getCategory().equals(Category.EXPLOIT) ? " §c" : " §7") + module.getSuffix() : ""));
            displayText = applyText(displayText);
            float textWidth = font.getStringWidth(displayText);

            if (textWidth > longestWidth) {
                longestModule = displayText;
                longestWidth = textWidth;
            }

            double xValue = sr.getScaledWidth() - (arraylistDrag.getX());


            boolean flip = xValue <= sr.getScaledWidth() / 2f;
            float x = (float) (flip ? xValue : sr.getScaledWidth() - (textWidth + arraylistDrag.getX()));


            float alphaAnimation = 1;

            float y = (float) (yOffset + arraylistDrag.getY());

            float heightVal = (float) (height.getValue() + 1);

            switch (animation.getMode()) {
                case "Move in":
                    if (flip) {
                        x -= Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (sr.getScaledWidth() - (arraylistDrag.getX() - textWidth)));
                    } else {
                        x += Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (arraylistDrag.getX() + textWidth));
                    }
                    break;
                case "Scale in":
                    if (!moduleAnimation.isDone()) {
                        RenderUtil.scaleStart(x + font.getStringWidth(displayText) / 2f, y + heightVal / 2 - font.getHeight() / 2f, (float) moduleAnimation.getOutput().floatValue());
                    }
                    alphaAnimation = (float) moduleAnimation.getOutput().floatValue();
                    break;
            }


            int index = (int) (count * colorIndex.getValue());
            Pair<Color, Color> colors = HUDMod.getClientColors();

            Color textcolor = ColorUtil.interpolateColorsBackAndForth(colorSpeed.getValue().intValue(), index, colors.getFirst(), colors.getSecond(), false);

            if (HUDMod.isRainbowTheme()) {
                textcolor = ColorUtil.rainbow(colorSpeed.getValue().intValue(), index, HUDMod.color1.getRainbow().getSaturation(), 1, 1);
            }

            if (background.isEnabled()) {
                float offset = minecraftFont.isEnabled() ? 4 : 5;
                Color color = backgroundColor.isEnabled() ? textcolor : new Color(10, 10, 10);
                Gui.drawRect2(x - 2, y - 2, font.getStringWidth(displayText) + offset, heightVal,
                        ColorUtil.applyOpacity(color, backgroundAlpha.getValue().floatValue() * alphaAnimation).getRGB());
            }

            float offset = minecraftFont.isEnabled() ? 1 : 0;
            switch (rectangle.getMode()) {
                default:
                    break;
                case "Top":
                    if (count == 0) {
                        Gui.drawRect2(x - 2, y - 1, textWidth + 5 - offset, 1, textcolor.getRGB());
                    }
                    break;
                case "Rise":
                    if (flip) {
                        Gui.drawRect2(x - 3.4D, (y + 1.0F), 1.5D, 9.5D, textcolor.getRGB());
                        break;
                    }
                    Gui.drawRect2((x + textWidth) + 3.2D, (y + 1.0F), 1.535D, 9.5D, textcolor.getRGB());
                    break;
                case "Side":
                    if (flip) {
                        Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                    } else {
                        Gui.drawRect2(x + textWidth + 2, y, 1, heightVal, textcolor.getRGB());
                    }
                    break;
                case "Outline":
                    if (count != 0) {
                        String modText = applyText(HUDMod.get(lastModule.getChineseName() + (lastModule.hasMode() ? " " + lastModule.getSuffix() : "")));
                        if(Language.is("Chinese")) modText = applyText(HUDMod.get(lastModule.getChineseName() + (lastModule.hasMode() ? " " + lastModule.getSuffix() : "")));
                        else if(Language.is("English")) modText = applyText(HUDMod.get(lastModule.getName() + (lastModule.hasMode() ? " " + lastModule.getSuffix() : "")));
                        float texWidth = font.getStringWidth(modText) - textWidth;
                        //Draws the difference of width rect and also the rect on the side of the text
                        if (flip) {
                            Gui.drawRect2(x + textWidth + 3, y, 1, heightVal, textcolor.getRGB());
                            Gui.drawRect2(x + textWidth + 3, y, texWidth + 1, 1, textcolor.getRGB());
                        } else {
                            Gui.drawRect2(x - (3 + texWidth), y, texWidth + 1, 1, textcolor.getRGB());
                            Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                        }
                        if (count == (lastCount - 1)) {
                            Gui.drawRect2(x - 3, y + heightVal, textWidth + 6, 1, textcolor.getRGB());
                        }
                    } else {
                        //Draws the rects for the first module in the count
                        if (flip) {
                            Gui.drawRect2(x + textWidth + 3, y, 1, heightVal, textcolor.getRGB());
                        } else {
                            Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                        }

                        //Top Bar rect
                        Gui.drawRect2(x - 3, y - 1, textWidth + 6, 1, textcolor.getRGB());
                    }
                    //sidebar
                    if (flip) {
                        Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                    } else {
                        Gui.drawRect2(x + textWidth + 2, y, 1, heightVal, textcolor.getRGB());
                    }


                    break;
            }


            float textYOffset = minecraftFont.isEnabled() ? .5f : 0;
            y += textYOffset;
            Color color = ColorUtil.applyOpacity(textcolor, alphaAnimation);
            switch (textShadow.getMode()) {
                case "None":
                    font.drawString(displayText, x, y + font.getMiddleOfBox(heightVal) + 2f, color.getRGB());
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    font.drawString(StringUtils.stripColorCodes(displayText), x + 0.5, y + font.getMiddleOfBox(heightVal)+1.5f + 0.5, ColorUtil.darker(color, .5f).getRGB());
                    RenderUtil.resetColor();
                    font.drawString(displayText, x, y + font.getMiddleOfBox(heightVal) + 1.5f, color.getRGB());
                    break;
                case "Black":
                    RenderUtil.resetColor();
                    font.drawString(StringUtils.stripColorCodes(displayText), x + 0.5, y + font.getMiddleOfBox(heightVal)+1.5f + 0.5, ColorUtil.applyOpacity(Color.BLACK, alphaAnimation).getRGB());
                    RenderUtil.resetColor();
                    font.drawString(displayText, x, y + font.getMiddleOfBox(heightVal) + 1.5f, color.getRGB());
                    break;
            }


            //  font.drawString(displayText, x, (y - 3) + font.getMiddleOfBox(heightVal), color.getRGB());

            if (animation.is("Scale in") && !moduleAnimation.isDone()) {
                RenderUtil.scaleEnd();
            }

            lastModule = module;

            yOffset += moduleAnimation.getOutput().floatValue() * heightVal;
            count++;
        }
        lastCount = count;
        longest = longestModule;
    }

    private String applyText(String text) {
        return text;
    }
}
