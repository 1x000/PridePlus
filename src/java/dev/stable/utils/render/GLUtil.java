package dev.stable.utils.render;

import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;

public class GLUtil {

    public static void enableDepth() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void disableDepth() {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static int[] enabledCaps = new int[32];

    public static void enableCaps(int... caps) {
        for (int cap : caps) glEnable(cap);
        enabledCaps = caps;
    }

    public static void disableCaps() {
        for (int cap : enabledCaps) glDisable(cap);
    }

    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
    }

    public static void setup2DRendering(boolean blend) {
        if (blend) {
            startBlend();
        }
        GlStateManager.disableTexture2D();
    }

    public static void setup2DRendering() {
        setup2DRendering(true);
    }

    public static void end2DRendering() {
        GlStateManager.enableTexture2D();
        endBlend();
    }

    public static void startRotate(float x, float y, float rotate) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(rotate, 0, 0, -1);
        GlStateManager.translate(-x, -y, 0);
    }

    public static void endRotate(){
        GlStateManager.popMatrix();
    }
    public static void scale(double x, double y, double z) {
        glScaled(x, y, z);
    }

    public static void pushMatrix() {
        glPushMatrix();
    }

    public static void popMatrix() {
        glPopMatrix();
    }

    public static void enable(int cap) {
        glEnable(cap);
    }

    public static void disable(int cap) {
        glDisable(cap);
    }

    public static void blendFunc(int sFactor,int dFactor) {
        glBlendFunc(sFactor,dFactor);
    }

    public static void translated(double x,double y,double z) {
        glTranslated(x,y,z);
    }

    public static void rotated(double angle,double x,double y,double z) {
        glRotated(angle,x,y,z);
    }

    public static void depthMask(boolean flag) {
        glDepthMask(flag);
    }

    public static void color(int r,int g,int b) {
        color(r,g,b,255);
    }

    public static void color(int r,int g,int b,int a) {
        GlStateManager.color(r / 255f,g / 255f,b / 255f,a / 255f);
    }

    public static void color(int hex) {
        GlStateManager.color(
                (hex >> 16 & 0xFF) / 255.0f,
                (hex >> 8 & 0xFF) / 255.0f,
                (hex & 0xFF) / 255.0f,
                (hex >> 24 & 0xFF) / 255.0f);
    }

    public static void resetColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }


}
