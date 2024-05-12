package dev.stable.ui;

import dev.stable.Client;
import dev.stable.module.impl.render.targethud.utils.AnimationUtils;
import dev.stable.utils.Utils;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.AnimationUtil;
import dev.stable.utils.animations.Direction;
import dev.stable.utils.animations.impl.DecelerateAnimation;
import dev.stable.utils.font.CustomFont;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.skidfont.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.awt.*;

import static net.minecraft.client.gui.Gui.drawRect;

@NativeObfuscation
public final class SplashScreen
{
    private static final int DEFAULT_MAX = 14;
    private static int PROGRESS;
    public static String CURRENT;
    private static TextureManager ctm;
    private static float animated;
    static float hue2;
    public static int count;


    public static void continueCount() {
        continueCount(true);
        update();
    }

    public static void continueCount(boolean continueCount) {
        //drawSplash();
        update();
        if(continueCount){
            count++;
        }
    }
    public static void update() {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getLanguageManager() == null) {
            return;
        }
        drawSplash(Minecraft.getMinecraft().getTextureManager());
    }

    public static void setProgress(final int givenProgress, final String givenSplash) {
        SplashScreen.PROGRESS = givenProgress;
        SplashScreen.CURRENT = givenSplash;
        update();
    }

    public static void drawSplash(final TextureManager tm) {
        if (SplashScreen.ctm == null) {
            SplashScreen.ctm = tm;
        }
        final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        final int scaleFactor = scaledresolution.getScaleFactor();
        final Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * scaleFactor, scaledresolution.getScaledHeight() * scaleFactor, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, (double)scaledresolution.getScaledWidth(), (double)scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        Gui.drawRect(0.0, 0.0, (double)scaledresolution.getScaledWidth(), (double)scaledresolution.getScaledHeight(), new Color(0, 0, 0).getRGB());
        final float h_ = SplashScreen.hue2;
        final float h2 = SplashScreen.hue2 + 85.0f;
        SplashScreen.hue2 += 0.5;
        final Color c = Color.getHSBColor(h_ / 255.0f, 0.9f, 1.0f);
        final Color c2 = Color.getHSBColor(h2 / 255.0f, 0.9f, 1.0f);
        final int color1 = c.getRGB();
        //Gui.drawRect(0.0, 0.0, scaledresolution.getScaledWidth(), 1.0, color1);
        drawProgress();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * scaleFactor, scaledresolution.getScaledHeight() * scaleFactor);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        Minecraft.getMinecraft().updateDisplay();
    }

    private static void drawProgress() {
        if (Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null) {
            return;
        }
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        //final float startX = sr.getScaledWidth() / 2.0f - 255.0f;
        //final float endX = sr.getScaledWidth() / 2.0f + 255.0f;
        final double nProgress = PROGRESS;
        //final double calc = nProgress / 14.0 * 510.0;
        //SplashScreen.animated = (float)new AnimationUtils().animateNoFast(startX + calc, SplashScreen.animated, 0.10000000149011612);
        //RenderUtil.drawImage(new ResourceLocation("Stable/splash.png"), 0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        //Gui.drawRect((double)startX, (double)(sr.getScaledHeight() - 35.0f), (double)endX, (double)(sr.getScaledHeight() - 34.0f), new Color(255, 255, 255, 100).getRGB());

        //Gui.drawRect((double)startX, (double)(sr.getScaledHeight() - 35.0f), (double)SplashScreen.animated, (double)(sr.getScaledHeight() - 34.0f), new Color(255, 255, 255, 255).getRGB());
        Display.setTitle("Initializing...");
        FontManager.edit20.drawCenteredString("正在初始化"+CURRENT+"("+PROGRESS*10+"%)", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2+50 , new Color(255, 255, 255, 255).getRGB());
        //RenderUtil.drawCircle(sr.getScaledWidth() / 2,sr.getScaledHeight() / 2,8,0,PROGRESS*28+30,Color.WHITE.darker().getRGB());
        RenderUtil.drawCircle(sr.getScaledWidth() / 2,sr.getScaledHeight() / 2,8,0,PROGRESS*36,Color.WHITE.getRGB());
        //RenderUtil.drawLoadingCircle(sr.getScaledWidth() / 2, (float)(sr.getScaledHeight() / 2));
        //FontManager.edit12.drawString("Loading " + Client.NAME.toUpperCase() + Client.VERSION, 10.0f, sr.getScaledHeight() - 10.0f, new Color(255, 255, 255, 200).getRGB());
    }

    static {
        SplashScreen.CURRENT = "";
        SplashScreen.animated = 0.0f;
    }
}
