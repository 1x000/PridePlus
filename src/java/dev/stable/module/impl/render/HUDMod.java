package dev.stable.module.impl.render;

import dev.stable.utils.skidfont.FontDrawer;
import dev.stable.utils.skidfont.FontManager;
import dev.stable.Client;
import dev.stable.event.impl.render.Render2DEvent;
import dev.stable.event.impl.render.ShaderEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.impl.combat.KillAura;
import dev.stable.module.impl.player.Manager;
import dev.stable.module.impl.player.Stealer;
import dev.stable.module.settings.impl.*;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.Utils;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.impl.DecelerateAnimation;
import dev.stable.utils.font.AbstractFontRenderer;
import dev.stable.utils.font.CustomFont;
import dev.stable.utils.objects.GradientColorWheel;
import dev.stable.utils.render.*;
import dev.stable.utils.render.blur.GlowUtils;
import dev.stable.utils.server.PingerUtils;
import dev.stable.utils.tuples.Pair;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

import static dev.stable.utils.skidfont.FontManager.rubik20;

public class HUDMod extends Module {

    private final StringSetting clientName = new StringSetting("Client Name");
    private final ModeSetting watermarkMode = new ModeSetting("Watermark Mode", "Stable", "Stable","StableNew","Exhi", "Novoline","cutecat","Rise","GameSense", "Minecraft", "None");
    @Getter
    public static final ModeSetting capeMode = new ModeSetting("Cape Mode", "Mtf", "Drak","Light", "Mtf");
    public static final ColorSetting color1 = new ColorSetting("Color 1", new Color(0x7878FF));
    public static final ColorSetting color2 = new ColorSetting("Color 2", new Color(0x280A8D));
    public static final ModeSetting theme = Theme.getModeSetting("Theme Selection", "Stable");
    public static final ModeSetting Language = new ModeSetting("Language", "English", "Chinese", "English");
    public static final BooleanSetting customFont = new BooleanSetting("Custom Font", true);
    private static final MultipleBoolSetting infoCustomization = new MultipleBoolSetting
            ("Info Options", new BooleanSetting
            ("Game Info", false), new BooleanSetting
            ("Semi-Bold Info", true), new BooleanSetting
            ("White Info", false), new BooleanSetting
            ("Info Shadow", true), new BooleanSetting
            ("Client Info", true));
    private static final MultipleBoolSetting infoSetting = new MultipleBoolSetting
            ("Info Setting", new BooleanSetting
            ("Show Ping", false), new BooleanSetting
            ("Show Fps", false), new BooleanSetting
            ("Show BPS", true), new BooleanSetting
            ("Show Coordinate", false));
    public static final MultipleBoolSetting hudCustomization = new MultipleBoolSetting
            ("HUD Options", new BooleanSetting
            ("Armor HUD", true), new BooleanSetting
            ("Radial Gradients", true), new BooleanSetting
            ("Render Cape", true), new BooleanSetting
            ("Lowercase", false));



    private static final MultipleBoolSetting disableButtons = new MultipleBoolSetting("Disable Buttons",
            new BooleanSetting("Disable KillAura", true),
            new BooleanSetting("Disable InvManager", true),
            new BooleanSetting("Disable ChestStealer", true));
    FontDrawer font = FontManager.edit16;

    public HUDMod() {
        super("Interface","显示界面", Category.DISPLAY, "customizes the client's appearance");
        color1.addParent(theme, modeSetting -> modeSetting.is("Custom Theme"));
        color2.addParent(theme, modeSetting -> modeSetting.is("Custom Theme") && !color1.isRainbow());
        this.addSettings(clientName, watermarkMode,capeMode,Language, theme, color1, color2, customFont, infoSetting, infoCustomization, hudCustomization, disableButtons);
        if (!enabled) this.toggleSilent();
    }
    public static int offsetValue = 0;
    private final Animation fadeInText = new DecelerateAnimation(500, 1);
    private int ticks = 0;

    private boolean version = true;

    public static float xOffset = 0;
    public static float hue = 0.0F;
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private final GradientColorWheel colorWheel = new GradientColorWheel();

    @Override
    public void onShaderEvent(ShaderEvent e) {
        ScaledResolution sr = new ScaledResolution(mc);
        Pair<Color, Color> clientColors = HUDMod.getClientColors();
        String name = Client.NAME;
        if (!clientName.getString().equals("")) {
            name = clientName.getString().replace("%time%", getCurrentTimeStamp());
        }
        String finalName = get(name);
            switch (watermarkMode.getMode()) {
                case "StableNew":
                    String sb = name + " | "+Client.INSTANCE.getVersion()+" | " + Minecraft.getDebugFPS() + "FPS";
                    float sb1 = FontManager.edit17.getStringWidth(sb);
                    RoundedUtil.drawRound(8.0f,8.0f,sb1+9f,20f,2,Color.BLACK);
                    //RoundedUtil.drawGradientHorizontal(8.0f,8.0f,sb1+2f,20f,2,new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
                    break;
                case "Stable":
                    String text1 = finalName+" "+ Client.INSTANCE.getVersion() + " | " + Minecraft.getDebugFPS() + "FPS";
                    //String text1 = "     "+finalName+" | "+ Client.INSTANCE.getVersion() + " | " + Minecraft.getDebugFPS() + " fps";
                    float textS = rubikFont18.getStringWidth(text1);
                    RoundedUtil.drawRound(6.0f, 7.0f, textS + 4.0f, 16.0f, 2f,Color.BLACK);
                    break;
                case "Novoline":
                    if (!clientName.getString().equals("")) {
                        name = clientName.getString().replace("%time%", getCurrentTimeStamp());
                    }
                    LocalTime currentTime = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    String formattedTime = currentTime.format(formatter);
                    FontDrawer font20 = FontManager.normal_bold_20;
                    FontDrawer font18 = FontManager.rubik15;
                    String client = name.toUpperCase();
                    String string = " §7| §f " + Client.INSTANCE.getVersion() + " §7| §f" + HUDMod.mc.thePlayer.getName() + " §7| §f" + PingerUtils.getPing() + " Ping §7| §f" + formattedTime;
                    float clientw = font20.getStringWidth(client);
                    float stringw = font18.getStringWidth(string);
                    float aw = clientw + stringw;
                    RoundedUtil.drawRound(6.0f, 6.0f, aw + 11.0F, 16.0f, 2.0f, Color.BLACK);
                    //RenderUtil.drawGradientSideways(6.0f, 5.9f, aw + 11.0f, 6.0f, HUDMod.getClientColors().getFirst().getRGB(), HUDMod.getClientColors().getSecond().getRGB());
                    RenderUtil.drawGradientSideways(6.0f, 5.9f, aw + 11.0f, 6.0f, getColor(0).getRGB(),  getColor(500).getRGB());
                    RenderUtil.resetColor();
                    break;
                case "Rise":
                    FontManager.product_sans_medium_36.drawString(finalName,6,6,getColor(0));
                    break;
            }

            if (e.isBloom()) {
                boolean glow = e.getBloomOptions().getSetting("Watermark").isEnabled();
                if (!glow) {
                    clientColors = Pair.of(Color.BLACK);
                }

                if (!clientName.getString().equals("")) {
                    name = clientName.getString().replace("%time%", getCurrentTimeStamp());
                }


                switch (watermarkMode.getMode()) {
                    case "Moon":
                        String text = "Moon | " + Client.INSTANCE.getVersion() + " | " + HUDMod.mc.thePlayer.getName() + " | " + (mc.isSingleplayer() ? "singleplayer" : HUDMod.mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + " fps";
                        float textW = tenacityBoldFont16.getStringWidth(text);
                        tenacityBoldFont16.drawString(text, 12.0f, 14.0f, -1);
                        RoundedUtil.drawRound(8.0f, 9.0f, textW + 7.0f, 16.0f, 4.0f, clientColors.getFirst());
                        break;
                    case "cutecat":
                        RoundedUtil.drawRound(8f, 8.5f, 68f, 68f, 0f, new Color(0, 0, 0, 240));
                }

                RenderUtil.resetColor();
                this.drawBottomRight();
                RenderUtil.resetColor();
                this.drawInfo(clientColors);
            }
        }
    @Override
    public void onRender2DEvent(Render2DEvent e) {
        ScaledResolution sr = new ScaledResolution(Utils.mc);
        Pair<Color, Color> clientColors = getClientColors();
        String name = Client.NAME;
        float h = hue;
        PostProcessing postProcessing = Client.INSTANCE.getModuleCollection().getModule(PostProcessing.class);
        if (!postProcessing.isEnabled()) {
            version = false;
        }

        if (!clientName.getString().equals("")) {
            name = clientName.getString().replace("%time%", getCurrentTimeStamp());
        }

        version = name.equalsIgnoreCase(Client.NAME);

        String finalName = get(name);

        switch (watermarkMode.getMode()) {
            case "StableNew":
                String sb = name + " | "+Client.INSTANCE.getVersion()+" | " + Minecraft.getDebugFPS() + "FPS";
                float sb1 = FontManager.edit17.getStringWidth(sb);
                RoundedUtil.drawRound(8.0f,8.0f,sb1+9f,20f,2,new Color(0,0,0,130));
                RoundedUtil.drawGradientHorizontal(8.0f,8.0f,sb1+9f,20f,2,new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
                RoundedUtil.drawGradientHorizontal(8.0f+2f, 8.0f+4.0f, 2, 12f, 1, new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
                RoundedUtil.drawGradientHorizontal(8.0f+2f, 8.0f+7.0f, 2, 6f, 1, new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
                RoundedUtil.drawGradientHorizontal(8.0f+2f, 8.0f+7.0f, 2, 6f, 1, new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
                FontManager.edit17.drawString(sb,15.0f,13.8f,-1);
                break;
            case "Stable":
                String text1 = finalName+" "+ Client.INSTANCE.getVersion() + " | " + Minecraft.getDebugFPS() + "FPS";
                float textS = rubikFont18.getStringWidth(text1);
                RoundedUtil.drawRound(6.0f, 7.0f, textS + 4.0f, 16.0f, 2f, new Color(0, 0, 0, 105));
                rubikFont18.drawStringWithShadow(text1, 8.0f, 14.0f, -1);
                RoundedUtil.drawRound(6.0f, 7.0f, textS + 4.0f, 0.8f, 1f,  HUDMod.getColor(0));
                break;
            case "Moon":
                String text = "Moon | "+ Client.INSTANCE.getVersion() + " | " + HUDMod.mc.thePlayer.getName() + " | " + (mc.isSingleplayer() ? "singleplayer" : HUDMod.mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + " fps";
                float textW = tenacityBoldFont16.getStringWidth(text);
                RoundedUtil.drawRound(8.0f, 9.0f, textW + 7.0f, 16.0f, 4.0f, new Color(0, 0, 0, 120));
                tenacityBoldFont16.drawString(text, 12.0f, 14.0f, -1);
                String finalName2 = name;
                GradientUtil.applyGradientHorizontal(10.0f, 11.0f, fr.getStringWidth(finalName2.toUpperCase()), 18.0f, 1.0f, new Color(getColor(0).getRGB()), new Color(getColor(200).getRGB()), () -> {
                    RenderUtil.setAlphaLimit(0.0f);
                    rubik20.drawString(finalName2.toUpperCase(), 10.0f, 13.0f, -1);
                });
                break;
            case "Rise":
                FontManager.product_sans_medium_36.drawString(finalName,6,6,getColor(0));
                break;
            case "GameSense":
                float h1 = hue;
                float h2 = hue + 85.0f;
                float h3 = hue + 170.0f;
                Color color33 = Color.getHSBColor(h / 255.0f, 0.9f, 1.0f);
                Color color332 = Color.getHSBColor(h2 / 255.0f, 0.9f, 1.0f);
                Color color333 = Color.getHSBColor(h3 / 255.0f, 0.9f, 1.0f);
                int color1 = color33.getRGB();
                int color2 = color332.getRGB();
                int color3 = color333.getRGB();
                int ping = mc.getNetHandler().getPlayerInfo(Minecraft.thePlayer.getUniqueID()).getResponseTime();
                String pings = ping + " ms";
                String testtext = "Stable§2sense§f | " + this.df.format(new Date()) + " | " + pings + " | Edited 0.1 | " + Minecraft.thePlayer.getName();
                RenderUtil.drawNewRect(4.0, 4.0, Utils.fr.getStringWidth(testtext) + 11, 20.0, 1.0, color33.getRGB(), color332.getRGB(), color333.getRGB());
                fr.drawCenteredStringWithShadow(testtext, (double)fr.getStringWidth(testtext) / 2.0 + 8.0, 14.25 - (double)fr.getStringHeight(testtext) / 2.0 - 0.75, new Color(255, 255, 255).getRGB());
                break;
            case "Exhi":
                StringBuilder stringBuilder = new StringBuilder(name.replace("Stable", "Stable")).insert(1, "§7");
                stringBuilder.append(" [§fFPS: ").append(Minecraft.getDebugFPS()).append("§7]"+" [§fMinecraft 1.8.x"+"§7]");
                RenderUtil.resetColor();
                mc.fontRendererObj.drawOutlinedString(stringBuilder.toString(), 3, 3, HUDMod.getColor(0).getRGB(),true);
                break;
            case "Novoline":
                if (!clientName.getString().equals("")) {
                    name = clientName.getString().replace("%time%", getCurrentTimeStamp());
                }
                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String formattedTime = currentTime.format(formatter);
                FontDrawer font20 = FontManager.normal_bold_20;
                FontDrawer font18 = FontManager.rubik15;
                String client = name.toUpperCase();
                String string = " §7| §f " + Client.INSTANCE.getVersion() + " §7| §f" + HUDMod.mc.thePlayer.getName() + " §7| §f" + PingerUtils.getPing() + " Ping §7| §f" + formattedTime;
                float clientw = font20.getStringWidth(client);
                float stringw = font18.getStringWidth(string);
                float aw = clientw + stringw;
                RenderUtil.resetColor();
                RoundedUtil.drawRound(6.0f, 6.0f, aw + 11.0F, 16.0f, 2.0f, new Color(0, 0, 0, 120));
                //RenderUtil.drawGradientSideways(6.0f,5.9f,aw+11.0f,6.0f, HUDMod.getClientColors().getFirst().getRGB(), HUDMod.getClientColors().getSecond().getRGB());
                RenderUtil.resetColor();
                //GlowUtils.drawGlow(9.0f, 9, fr.getStringWidth(client) + 4, 8, 15, new Color(clientColors.getFirst().getRed(), clientColors.getFirst().getGreen(), clientColors.getFirst().getBlue(), 180));
                font20.drawString(client, 10.0f, 8.3f, HUDMod.getColor(0));
                font18.drawString(string, clientw + 12.0f, 9.6f, -1);

                break;

            case "Glow":
                String client1 = "Stable";
                CustomFont font40 = tenacityBoldFont40;
                GlowUtils.drawGlow(6.5f, 8.5f, fr.getStringWidth(client1)+29, 19, 21, new Color(clientColors.getFirst().getRed(), clientColors.getFirst().getGreen(), clientColors.getFirst().getBlue(), 200));
                GradientUtil.applyGradientHorizontal(13.0f-10f, 12.0f, fr.getStringWidth(client1)+22f, 10.0f, 1.0f, clientColors.getFirst(), clientColors.getFirst(), () -> {
                    RenderUtil.setAlphaLimit(0.0f);
                    font40.drawString(client1, 12.5f, 12.5f, -1);
                });
                break;
            case "cutecat":
                RoundedUtil.drawRound(8f,8f,68f,68f,0f,new Color(0,0,0,240));
                //GlowUtils.drawGlow(7f,7f,70f,70f,10,new Color(0,0,0,140));
                RenderUtil.drawImage(new ResourceLocation("Pride/cat.jpg"),7f,7f,70f,70f);
                break;
            case "Normal":
                String client2 = name.toUpperCase();
                Utils.mc.fontRendererObj.drawString(client2,1f,1f,-1);
                break;
            case "Minecraft":
                AbstractFontRenderer fr = mc.fontRendererObj;
                AbstractFontRenderer finalFr = fr;
                GradientUtil.applyGradientHorizontal(3, 3, finalFr.getStringWidth(finalName), finalFr.getHeight(), 1,
                        HUDMod.getColor(0),HUDMod.getColor(500), () -> {
                            RenderUtil.setAlphaLimit(0);
                            finalFr.drawStringWithShadow(finalName, 3.5f, 3.5f, new Color(0, 0, 0, 0).getRGB());
                        });
                break;


        }
        RenderUtil.resetColor();
        this.drawBottomRight();
        RenderUtil.resetColor();
        this.drawInfo(clientColors);
        drawArmor(sr);
    }

    private void drawBottomRight() {
        boolean whiteInfo = infoCustomization.isEnabled("White Info");
        AbstractFontRenderer fr = customFont.isEnabled() ? rubikFont16 : HUDMod.mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = (float)(12.5 * (double)GuiChat.openingAnimation.getOutput().floatValue());
        boolean shadowInfo = infoCustomization.isEnabled("Info Shadow");
        if (infoCustomization.isEnabled("Client Info")) {
            String text;
            if(Client.username.equals("development")){
                text = Client.INSTANCE.getVersion() + " | " + (customFont.isEnabled() ? "" : "§l") + "ok" + "§r" + " | " + Client.username;
            }else{
                text = Client.INSTANCE.getVersion() + " | "  + "Release" + " | " + Client.username;
            }
            text = HUDMod.get(text);
            float x = (float)sr.getScaledWidth() - (fr.getStringWidth(text) + 3.0f);
            float y = (float)(sr.getScaledHeight() - (fr.getHeight() + 3)) - yOffset;
            Pair<Color, Color> clientColors = HUDMod.getClientColors();
            String finalText = text;
            float f = customFont.isEnabled() ? 0.5f : 1.0f;
            fr.drawString(finalText, x + f, y + f, -16777216);
            if (whiteInfo) {
                fr.drawString(finalText, x, y, -1);
            } else {
                GradientUtil.applyGradientHorizontal(x, y, fr.getStringWidth(text), 20.0f, 1.0f, HUDMod.getColor(0), HUDMod.getColor(500), () -> {
                    RenderUtil.setAlphaLimit(0.0f);
                    fr.drawString(finalText, x, y, -1);
                });
            }
        }
    }
    private String intToRomanByGreedy(int num) {
        int[] values = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; ++i) {
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }
        }
        return stringBuilder.toString();
    }

    private final Map<String, String> bottomLeftText = new LinkedHashMap<>();

    private void drawInfo(Pair<Color, Color> clientColors) {
        float yMovement;
        boolean shadowInfo = infoCustomization.isEnabled("Info Shadow");
        boolean semiBold = infoCustomization.isEnabled("Semi-Bold Info");
        boolean whiteInfo = infoCustomization.isEnabled("White Info");
        String titleBold = semiBold ? "\u00a7l" : "";
        ScaledResolution sr = new ScaledResolution(mc);
        int size = this.bottomLeftText.size();
        GuiNewChat.chatPos = 30;
        if (size != 1) {
            if (size == 2) {
                GuiNewChat.chatPos = 26;
            } else if (size == 3) {
                GuiNewChat.chatPos = 15;
            } else if (size == 4) {
                GuiNewChat.chatPos = 5;
            }
        }
        if (infoSetting.isEnabled("Show Ping")) {
            this.bottomLeftText.put("Ping", PingerUtils.getPing());
        } else {
            this.bottomLeftText.remove("Ping");
        }
        if (infoSetting.isEnabled("Show Fps")) {
            this.bottomLeftText.put("FPS", String.valueOf(Minecraft.getDebugFPS()));
        } else {
            this.bottomLeftText.remove("FPS");
        }
        if (infoSetting.isEnabled("Show BPS")) {
            this.bottomLeftText.put("BPS", String.valueOf(this.calculateBPS()));
        } else {
            this.bottomLeftText.remove("BPS");
        }
        if (infoSetting.isEnabled("Show Coordinate")) {
            this.bottomLeftText.put("Pos", Math.round(HUDMod.mc.thePlayer.posX) + " " + Math.round(HUDMod.mc.thePlayer.posY) + " " + Math.round(HUDMod.mc.thePlayer.posZ));
        } else {
            this.bottomLeftText.remove("Pos");
        }
        AbstractFontRenderer nameInfoFr = rubikFont16;
        if (!customFont.isEnabled()) {
            nameInfoFr = HUDMod.mc.fontRendererObj;
        }
        xOffset = semiBold ? nameInfoFr.getStringWidth("§Pos: " + this.bottomLeftText.get("Pos")) : nameInfoFr.getStringWidth("Pos: " + this.bottomLeftText.get("Pos"));
        float yOffset = (float)(12.5 * (double)GuiChat.openingAnimation.getOutput().floatValue());
        float f2 = customFont.isEnabled() ? 0.5f : 1.0f;
        float f3 = customFont.isEnabled() ? 1.0f : 0.5f;
        float f = yMovement = !customFont.isEnabled() ? -1.0f : 0.0f;
        if (infoCustomization.isEnabled("Game Info")) {
            if (whiteInfo) {
                float boldFontMovement = (float)(nameInfoFr.getHeight() + 2) + yOffset + yMovement;
                for (Map.Entry<String, String> line : this.bottomLeftText.entrySet()) {
                    nameInfoFr.drawString(HUDMod.get(titleBold + line.getKey() + "§r: " + line.getValue()), 2.0f, (float)sr.getScaledHeight() - boldFontMovement, -1, shadowInfo);
                    boldFontMovement += (float)nameInfoFr.getHeight() + f3;
                }
            } else {
                float f4 = (float)(nameInfoFr.getHeight() + 2) + yOffset + yMovement;
                for (Map.Entry<String, String> line : this.bottomLeftText.entrySet()) {
                    if (shadowInfo) {
                        nameInfoFr.drawString(HUDMod.get(line.getValue()), 2.0f + f2 + nameInfoFr.getStringWidth(titleBold + line.getKey() + ":\u00a7r "), (float)sr.getScaledHeight() - f4 + f2, -16777216);
                    }
                    nameInfoFr.drawString(HUDMod.get(line.getValue()), 2.0f + nameInfoFr.getStringWidth(titleBold + line.getKey() + ":\u00a7r "), (float)sr.getScaledHeight() - f4, -1);
                    f4 += (float)nameInfoFr.getHeight() + f3;
                }
                float height = (nameInfoFr.getHeight() + 2) * this.bottomLeftText.size();
                float width = nameInfoFr.getStringWidth(titleBold + "Speed:");
                AbstractFontRenderer finalFr = nameInfoFr;
                if (shadowInfo) {
                    float boldFontMovement1 = (float)(finalFr.getHeight() + 2) + yOffset + yMovement;
                    for (Map.Entry<String, String> line : this.bottomLeftText.entrySet()) {
                        finalFr.drawString(HUDMod.get(titleBold + line.getKey() + ": "), 2.0f + f2, (float)sr.getScaledHeight() - boldFontMovement1 + f2, -16777216);
                        boldFontMovement1 += (float)finalFr.getHeight() + f3;
                    }
                }
                GradientUtil.applyGradientVertical(2.0f, (float)sr.getScaledHeight() - (height + yOffset + yMovement), width, height, 1.0f, HUDMod.getColor(0), HUDMod.getColor(500), () -> {
                    float boldFontMovement = (float)(finalFr.getHeight() + 2) + yOffset + yMovement;
                    for (Map.Entry<String, String> line : this.bottomLeftText.entrySet()) {
                        finalFr.drawString(HUDMod.get(titleBold + line.getKey() + ": "), 2.0f, (float)sr.getScaledHeight() - boldFontMovement, -1);
                        boldFontMovement += (float)finalFr.getHeight() + f3;
                    }
                });
            }
        }
    }

    public static double calculateBPS() {
        double bps = Math.hypot(HUDMod.mc.thePlayer.posX - HUDMod.mc.thePlayer.prevPosX, HUDMod.mc.thePlayer.posZ - HUDMod.mc.thePlayer.prevPosZ) * (double)HUDMod.mc.timer.timerSpeed * 20.0;
        return (double)Math.round(bps * 100.0) / 100.0;
    }
    private void drawArmor(ScaledResolution sr) {
        if (hudCustomization.getSetting("Armor HUD").isEnabled()) {
            List<ItemStack> equipment = new ArrayList<>();
            boolean inWater = Utils.mc.thePlayer.isEntityAlive() && Utils.mc.thePlayer.isInsideOfMaterial(Material.water);
            int x = -60;

            ItemStack armorPiece;
            for (int i = 3; i >= 0; i--) {
                if ((armorPiece = Utils.mc.thePlayer.inventory.armorInventory[i]) != null) {
                    equipment.add(armorPiece);
                }
            }
            Collections.reverse(equipment);

            for (ItemStack itemStack : equipment) {
                armorPiece = itemStack;
                RenderHelper.enableGUIStandardItemLighting();
                x += 18;
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.clear(256);
                Utils.mc.getRenderItem().zLevel = -12.0F;
                int s = Utils.mc.thePlayer.capabilities.isCreativeMode ? 2 : 0;
                Utils.mc.getRenderItem().renderItemAndEffectIntoGUI(armorPiece, - x - 155 + sr.getScaledWidth() / 2 - 2,
                        (int) (sr.getScaledHeight() - (inWater ? 65 : 18) + s - (16 * GuiChat.openingAnimation.getOutput().floatValue())));
                Utils.mc.getRenderItem().zLevel = 0.0F;
                GlStateManager.disableBlend();
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();
                armorPiece.getEnchantmentTagList();
            }
        }
    }
    public static Pair<Color, Color> getClientColors() {
        return Theme.getThemeColors(theme.getMode());
    }
    public static Color getColor(int offset) {
        return ColorUtil.getColor(HUDMod.getClientColors().getFirst(),HUDMod.getClientColors().getSecond(), 2500, offset);
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }

    public static String get(String text) {
        return hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
    }

    public static boolean isRainbowTheme() {return theme.is("Custom Theme") && color1.isRainbow();}
    public static boolean is1Rainbow() {return theme.is("Custom Theme") && color1.isRainbow();}
    public static boolean is2Rainbow() {
        return theme.is("Custom Theme") && color2.isRainbow();
    }
    public static boolean drawRadialGradients() {return hudCustomization.getSetting("Radial Gradients").isEnabled();}

    public static void addButtons(List<GuiButton> buttonList) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (!mb.getSetting().isEnabled()) continue;
            buttonList.add(mb.getButton());
        }
    }

    public static void updateButtonStatus() {
        for (ModuleButton mb : ModuleButton.values()) {
            mb.getButton().enabled = Client.INSTANCE.getModuleCollection().getModule(mb.getModule()).isEnabled();
        }
    }

    public static void handleActionPerformed(GuiButton button) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getButton() != button) continue;
            Module m = Client.INSTANCE.getModuleCollection().getModule(mb.getModule());
            if (!m.isEnabled()) break;
            m.toggle();
            break;
        }
    }

    static /* synthetic */ MultipleBoolSetting access$000() {
        return disableButtons;
    }

    public static enum ModuleButton {
        AURA(KillAura.class, HUDMod.access$000().getSetting("Disable KillAura"), new GuiButton(2461, 3, 4, 120, 20, "Disable KillAura")),
        INVMANAGER(Manager.class, HUDMod.access$000().getSetting("Disable InvManager"), new GuiButton(2462, 3, 26, 120, 20, "Disable InvManager")),
        CHESTSTEALER(Stealer.class, HUDMod.access$000().getSetting("Disable ChestStealer"), new GuiButton(2463, 3, 48, 120, 20, "Disable ChestStealer"));

        private final Class<? extends Module> module;
        private final BooleanSetting setting;
        private final GuiButton button;

        public Class<? extends Module> getModule() {
            return this.module;
        }

        public BooleanSetting getSetting() {
            return this.setting;
        }

        public GuiButton getButton() {
            return this.button;
        }

        private ModuleButton(Class<? extends Module> module, BooleanSetting setting, GuiButton button) {
            this.module = module;
            this.setting = setting;
            this.button = button;
        }
    }
}
