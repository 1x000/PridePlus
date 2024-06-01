package cn.molokymc.prideplus.ui;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.compatibility.display.Display;


import java.awt.*;


public final class SplashScreen
{
    private static int PROGRESS;
    public static String CURRENT;
    private static TextureManager ctm;
    static float hue2;
    public static int count;


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
        GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        Gui.drawRect(0.0, 0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), new Color(0, 0, 0).getRGB());
        SplashScreen.hue2 += 0.5;
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

        RenderUtil.drawImage(new ResourceLocation(Pride.NAME + "/splash.png"), 0, 0, sr.getScaledWidth(), sr.getScaledHeight());

        Display.setTitle("Initializing...");
        FontManager.edit20.drawCenteredString("正在初始化"+CURRENT+"("+PROGRESS*10+"%)", (double) sr.getScaledWidth() / 2, (double) sr.getScaledHeight() / 2 + 50 , new Color(255, 255, 255, 255).getRGB());
        RenderUtil.drawCircle((float) sr.getScaledWidth() / 2, (float) sr.getScaledHeight() / 2,8,0,PROGRESS*36,Color.WHITE.getRGB());
    }

    static {
        SplashScreen.CURRENT = "";
    }
}
