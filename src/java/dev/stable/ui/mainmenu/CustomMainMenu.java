package dev.stable.ui.mainmenu;

import dev.stable.Client;
import dev.stable.ui.Screen;
import dev.stable.utils.Utils;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.AnimationUtil;
import dev.stable.utils.animations.Direction;
import dev.stable.utils.animations.impl.DecelerateAnimation;
import dev.stable.utils.misc.HoveringUtil;
import dev.stable.utils.misc.NetworkingUtils;
import dev.stable.utils.render.*;
import dev.stable.utils.skidfont.FontManager;
import lombok.Getter;
import net.minecraft.client.gui.*;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomMainMenu extends GuiScreen {
    private float textY;
    private final MenuShader BACKGROUND_SHADER = new MenuShader();
    public static boolean animatedOpen = false;
    public static int background = 1;
    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Singleplayer"));
        add(new MenuButton("Multiplayer"));
        add(new MenuButton("Alt Manager"));
        add(new MenuButton("Settings"));
        add(new MenuButton("BackGround"));
        add(new MenuButton("Exit"));

    }};
    public int textAlpha;
    private final List<TextButton> textButtons = new ArrayList() {{
    }};

    private static boolean firstInit = false;
    @Override
    public void initGui() {
        if (!firstInit) {
            NetworkingUtils.bypassSSL();
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            }
            firstInit = true;
        }

        if (Utils.mc.gameSettings.guiScale != 2) {
            Client.prevGuiScale = Utils.mc.gameSettings.guiScale;
            Client.updateGuiScale = true;
            Utils.mc.gameSettings.guiScale = 2;
            Utils.mc.resize(Utils.mc.displayWidth - 1, Utils.mc.displayHeight);
            Utils.mc.resize(Utils.mc.displayWidth + 1, Utils.mc.displayHeight);
        }
        buttons.forEach(MenuButton::initGui);
    }

    private static final Color TRANSPARENCY = new Color(255, 255, 255, 0);
    private static final Color BACK = new Color(0, 0, 0, 60);
    private static final Color SHADOW = new Color(255, 255, 255, 30);
    private Framebuffer framebuffer = new Framebuffer(1, 1, true);
    private void updateTextAlpha() {
        if (textAlpha != 200) {
            textAlpha = (int) AnimationUtil.moveUD(textAlpha, 200, 0.2f, 0.15f);
        }
    }
    public int textColor() {
        return new Color(233, 233, 233, textAlpha).getRGB();
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Utils.mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
        this.textY = -15.0F;
        RenderUtil.resetColor();

        BACKGROUND_SHADER.render(new ScaledResolution(mc), true);
        RenderUtil.resetColor();

        float titleHeight = Utils.tenacityBoldFont32.getHeight() + 3 + 12;

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        framebuffer.bindFramebuffer(false);

        float rectWodth = 140;
        float rectHeight = 0.04f * 0.04f + titleHeight;
        float rectY = (height / 2.5f - -16 / 2.5f) - 4 - titleHeight;
        //Singleplayer BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY,
                rectWodth, rectHeight, 6, Color.WHITE);
        //Multiplayer BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 40,
                rectWodth, rectHeight, 6, Color.WHITE);
        //Alt Manager BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 80,
                rectWodth, rectHeight, 6, Color.WHITE);
        //Settings BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 120,
                rectWodth, rectHeight, 6, Color.WHITE);
        //Exit BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 160,
                rectWodth, rectHeight, 6, Color.WHITE);

        //Singleplayer BLUR BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY,
                rectWodth, rectHeight, 6, SHADOW);
        //Multiplayer BLUR BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 40,
                rectWodth, rectHeight, 6, SHADOW);
        //Alt Manager BLUR BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 80,
                rectWodth, rectHeight, 6, SHADOW);
        //Settings BLUR BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 119.5f,
                rectWodth, rectHeight, 6, SHADOW);
        //Exit BLUR BG
        RoundedUtil.drawRound(width / 2f - rectWodth / 2f, rectY + 159.5f,
                rectWodth, rectHeight, 6, SHADOW);



        GL11.glEnable(GL11.GL_BLEND);
        StencilUtil.initStencilToWrite();
        RenderUtil.setAlphaLimit(0);
        StencilUtil.readStencilBuffer(1);

        GLUtil.startBlend();
        StencilUtil.uninitStencilBuffer();


        float buttonWidth = 120;
        float buttonHeight = 22;

        int count = 0;
        for (MenuButton button : buttons) {
            button.x = width / 2f - buttonWidth / 2f;
            button.y = ((height / 2.5f - buttonHeight / 2.5f) - 10) + count;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Singleplayer":
                        Utils.mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        Utils.mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "Alt Manager":
                        Utils.mc.displayGuiScreen(Client.INSTANCE.getAltManager());
                        break;
                    case "Settings":
                        Utils.mc.displayGuiScreen(new GuiOptions(this, Utils.mc.gameSettings));
                        break;
                    case "BackGround":
                        background++;
                        if(background==14)background=1;
                        mc.displayGuiScreen(new CustomMainMenu());
                        break;
                    case "Exit":
                        Utils.mc.shutdown();
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += buttonHeight + 10;

            //FontDrawer sb = FontManager.edit18;
            //rubikFont18.drawString("Copyright Mojang AB. Do not distribute!", 1, this.height - 8, new Color(255, 255, 255, 255).getRGB());
            //rubikFont18.drawString("Minecraft 1.8.9(Optifine HD_U_M5)", 1, this.height - 8 * 2, new Color(255, 255, 255, 255).getRGB());
            //rubikFont18.drawString(Client.NAME + " " + Client.INSTANCE.getVersion() + " §a(Latest)", 1, this.height - 8 * 3, new Color(255, 255, 255, 255).getRGB());
            //rubikFont18.drawString("Developed by Stable Team", this.width - rubikFont18.getStringWidth("Developed by Stable Team"), this.height - 8, new Color(255, 255, 255, 255).getRGB());
            //rubikFont18.drawString("Welcome, " + Client.username, this.width - rubikFont18.getStringWidth("Welcome, " + Client.username), this.height - 8 * 2, new Color(255, 255, 255, 255).getRGB());
            //    sb.drawStringWithShadow(Stable.NAME + " §a" + Client.INSTANCE.getVersion(),0,y ,new Color(255,255,255).getRGB());
        }
        drawMainMenu();
        drawChangelog();
    }
    public void drawChangelog() {
        String[] Changelog = {"Changelog" , "[+]AutoPlay音乐盒","[~]改进NameTags","[~]改进GuiChat","[/]改进ClickGUI","[+]Minecraft,Exhi WaterMark","[+]Arraylist字体切换","[~]改进SplashScreen"};
        for (String s : Changelog) {
            FontManager.edit13.drawString(Changelog[0], 1.5, 1, Colors.getColor(255, 255, 255, 40));
            FontManager.edit13.drawString(Changelog[1], 1.5, 9, Colors.getColor(255, 255, 255, 20));
            FontManager.edit13.drawString(Changelog[2], 1.5, 17, Colors.getColor(255, 255, 255, 20));
            FontManager.edit13.drawString(Changelog[3], 1.5, 25, Colors.getColor(255, 255, 255, 20));
            FontManager.edit13.drawString(Changelog[4], 1.5, 33, Colors.getColor(255, 255, 255, 20));
            FontManager.edit13.drawString(Changelog[5], 1.5, 41, Colors.getColor(255, 255, 255, 20));
            //FontManager.edit13.drawString(Changelog[6], 1.5, 49, Colors.getColor(255, 255, 255, 20));
            //FontManager.edit13.drawString(Changelog[7], 1.5, 57, Colors.getColor(255, 255, 255, 20));
        }
    }
    public void drawMainMenu() {
        //Left
        FontManager.product_sans_regular_18.drawString(Client.NAME+" "+Client.INSTANCE.getVersion(), 6, height - 12, textColor());
        //Right
        final String text2 = "Welcome, User";
        FontManager.product_sans_regular_18.drawString(text2, width - FontManager.product_sans_regular_18.getStringWidth(text2) - 6, height - 12, textColor());
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        textButtons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onGuiClosed() {
        if (Client.updateGuiScale) {
            Utils.mc.gameSettings.guiScale = Client.prevGuiScale;
            Client.updateGuiScale = false;
        }
    }

    private static class TextButton implements Screen {

        public float x, y;
        @Getter
        private final float width, height;
        public Runnable clickAction;
        private final String text;

        private final Animation hoverAnimation = new DecelerateAnimation(150, 1);

        public boolean addToEnd;

        public TextButton(String text) {
            this.text = text;
            width = FontManager.edit16.getStringWidth(text);
            height = FontManager.edit16.getHeight();
        }

        @Override
        public void initGui() {

        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {

        }

        @Override
        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            FontManager.edit16.drawString(text, x, (float) (y - (height / 2f * hoverAnimation.getOutput())), Color.WHITE.getRGB());
            if (addToEnd) {
                FontManager.edit16.drawString(" | ", x + width, y, Color.WHITE.getRGB());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            if (hovered && button == 0) {
                clickAction.run();
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {

        }
    }
}
