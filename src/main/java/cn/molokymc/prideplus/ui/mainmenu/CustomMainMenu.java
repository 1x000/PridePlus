package cn.molokymc.prideplus.ui.mainmenu;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.ui.Screen;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.AnimationUtil;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.misc.HoveringUtil;
import cn.molokymc.prideplus.utils.misc.NetworkingUtils;
import cn.molokymc.prideplus.utils.render.*;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import lombok.Getter;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import org.lwjgl.compatibility.util.glu.Project;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomMainMenu extends GuiScreen {

    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("Pride/background/panorama_0.png"), new ResourceLocation("Pride/background/panorama_1.png"), new ResourceLocation("Pride/background/panorama_2.png"), new ResourceLocation("Pride/background/panorama_3.png"), new ResourceLocation("Pride/background/panorama_4.png"), new ResourceLocation("Pride/background/panorama_5.png")};

    private ResourceLocation backgroundTexture;
    private int panoramaTimer;
    private DynamicTexture viewportTexture;

    private float textY;
    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Singleplayer"));
        add(new MenuButton("Multiplayer"));
        add(new MenuButton("Alt Manager"));
        add(new MenuButton("Settings"));
        add(new MenuButton("Exit"));

    }};
    public int textAlpha;
    private final List<TextButton> textButtons = new ArrayList() {{
    }};

    private static boolean firstInit = false;
    @Override
    public void initGui() {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);

        if (!firstInit) {
            NetworkingUtils.bypassSSL();
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            }
            firstInit = true;
        }

        if (Utils.mc.gameSettings.guiScale != 2) {
            Pride.prevGuiScale = Utils.mc.gameSettings.guiScale;
            Pride.updateGuiScale = true;
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

        //BACKGROUND_SHADER.render(new ScaledResolution(mc), true);
        renderSkybox(mouseX, mouseY, partialTicks);
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
                        Utils.mc.displayGuiScreen(Pride.INSTANCE.getAltManager());
                        break;
                    case "Settings":
                        Utils.mc.displayGuiScreen(new GuiOptions(this, Utils.mc.gameSettings));
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
        FontManager.edit13.drawString("Changelog", 1.5, 1, Colors.getColor(255, 255, 255, 200));
        String[] Changelog = {
                "[+]升级到Java21",
                "[+]升级到LWJGL3",
                "[~]替换了AltManager",
                "[~]修复了Scaffold",
                "[-]移除了原Stable的无用Event",
        };
        int i = 0;
        for (String s : Changelog) {
            FontManager.edit13.drawString(s, 1.5, 9 + i, Colors.getColor(255, 255, 255, 120));
            i += 8;
        }
    }
    public void drawMainMenu() {
        //Left
        FontManager.product_sans_regular_18.drawString(Pride.NAME+" "+ Pride.INSTANCE.getVersion(), 6, height - 12, textColor());
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
        if (Pride.updateGuiScale) {
            Utils.mc.gameSettings.guiScale = Pride.prevGuiScale;
            Pride.updateGuiScale = false;
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


    private void rotateAndBlurSkybox(float p_73968_1_)
    {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int i = 3;
        int j = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null)
        {
            j = custompanoramaproperties.getBlur2();
        }

        for (int k = 0; k < j; ++k)
        {
            float f = 1.0F / (float)(k + 1);
            int l = this.width;
            int i1 = this.height;
            float f1 = (float)(k - i / 2) / 256.0F;
            worldrenderer.pos((double)l, (double)i1, (double)this.zLevel).tex((double)(0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos((double)l, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, (double)i1, (double)this.zLevel).tex((double)(0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_)
    {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        int i = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null)
        {
            i = custompanoramaproperties.getBlur3();
        }

        for (int j = 0; j < i; ++j)
        {
            this.rotateAndBlurSkybox(p_73971_3_);
            this.rotateAndBlurSkybox(p_73971_3_);
        }

        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f2 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
        float f = (float)this.height * f2 / 256.0F;
        float f1 = (float)this.width * f2 / 256.0F;
        int k = this.width;
        int l = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)l, (double)this.zLevel).tex((double)(0.5F - f), (double)(0.5F + f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos((double)k, (double)l, (double)this.zLevel).tex((double)(0.5F - f), (double)(0.5F - f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos((double)k, 0.0D, (double)this.zLevel).tex((double)(0.5F + f), (double)(0.5F - f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(0.5F + f), (double)(0.5F + f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = 8;
        int j = 64;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null)
        {
            j = custompanoramaproperties.getBlur1();
        }

        for (int k = 0; k < j; ++k)
        {
            GlStateManager.pushMatrix();
            float f = ((float)(k % i) / (float)i - 0.5F) / 64.0F;
            float f1 = ((float)(k / i) / (float)i - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float)this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float)this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int l = 0; l < 6; ++l)
            {
                GlStateManager.pushMatrix();

                if (l == 1)
                {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 3)
                {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 4)
                {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (l == 5)
                {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                ResourceLocation[] aresourcelocation = titlePanoramaPaths;

                if (custompanoramaproperties != null)
                {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }

                this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int i1 = 255 / (k + 1);
                float f3 = 0.0F;
                worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }
    public void updateScreen()
    {
        ++this.panoramaTimer;
    }
}
