package cn.molokymc.prideplus.utils.render;

import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.misc.MathUtils;
import cn.molokymc.prideplus.utils.time.TimeUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil implements Utils {
    public static int deltaTime;
    static int animationX;
    private static final TimeUtil animationTimer = new TimeUtil();

    public static void drawTexture(int texture, float x, float y, float width, float height, float u, float v, int textureWidth, int textureHeight) {
        float xTexel = 1.0F / textureWidth;
        float yTexel = 1.0F / textureHeight;

        GlStateManager.bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(u * xTexel, (v + height) * yTexel).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex((u + width) * xTexel, (v + height) * yTexel).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex((u + width) * xTexel, v * yTexel).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(u * xTexel, v * yTexel).endVertex();
        tessellator.draw();
    }
    public static int reAlpha(final int color, final float alpha) {
        try {
            final Color c = new Color(color);
            final float r = ((float) 1 / 255) * c.getRed();
            final float g = ((float) 1 / 255) * c.getGreen();
            final float b = ((float) 1 / 255) * c.getBlue();
            return new Color(r, g, b, alpha).getRGB();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return color;
    }
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }
    public static void glColor(final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }
    public static void drawLoadingCircle(final float x, final float y) {
        for (int i = 0; i < 2; ++i) {
            final int rot = (int)(System.nanoTime() / 2000000L * i % 360L);
            drawCircle(x, y, (float)(i * 8), rot - 160, rot,Color.WHITE.getRGB());
        }
    }
    public static void drawCircle(final float x, final float y, final float radius, final int start, final int end,int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glColor(color);
        GL11.glEnable(2848);
        GL11.glLineWidth(3.0f);
        GL11.glBegin(3);
        for (float i = (float)end; i >= start; i -= 4.0f) {
            GL11.glVertex2f((float)(x + Math.cos(i * 3.141592653589793 / 180.0) * (radius * 1.001f)), (float)(y + Math.sin(i * 3.141592653589793 / 180.0) * (radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void rectangle(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float) (color >> 24 & 255) / 255.0f;
        float var6 = (float) (color >> 16 & 255) / 255.0f;
        float var7 = (float) (color >> 8 & 255) / 255.0f;
        float var8 = (float) (color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor,
                                         int borderColor) {
        RenderUtil.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    public static void otherDrawBoundingBox(final Entity entity, final float x, final float y, final float z, double width, final double height) {
        width *= 1.5;
        final float yaw1 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 45.0f;
        float newYaw1;
        if (yaw1 < 0.0f) {
            newYaw1 = 0.0f;
            newYaw1 += 360.0f - Math.abs(yaw1);
        }
        else {
            newYaw1 = yaw1;
        }
        newYaw1 *= -1.0f;
        newYaw1 *= 0.017453292f;
        final float yaw2 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 135.0f;
        float newYaw2;
        if (yaw2 < 0.0f) {
            newYaw2 = 0.0f;
            newYaw2 += 360.0f - Math.abs(yaw2);
        }
        else {
            newYaw2 = yaw2;
        }
        newYaw2 *= -1.0f;
        newYaw2 *= 0.017453292f;
        final float yaw3 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 225.0f;
        float newYaw3;
        if (yaw3 < 0.0f) {
            newYaw3 = 0.0f;
            newYaw3 += 360.0f - Math.abs(yaw3);
        }
        else {
            newYaw3 = yaw3;
        }
        newYaw3 *= -1.0f;
        newYaw3 *= 0.017453292f;
        final float yaw4 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 315.0f;
        float newYaw4;
        if (yaw4 < 0.0f) {
            newYaw4 = 0.0f;
            newYaw4 += 360.0f - Math.abs(yaw4);
        }
        else {
            newYaw4 = yaw4;
        }
        newYaw4 *= -1.0f;
        newYaw4 *= 0.017453292f;
        final float x2 = (float)(Math.sin(newYaw1) * width + x);
        final float z2 = (float)(Math.cos(newYaw1) * width + z);
        final float x3 = (float)(Math.sin(newYaw2) * width + x);
        final float z3 = (float)(Math.cos(newYaw2) * width + z);
        final float x4 = (float)(Math.sin(newYaw3) * width + x);
        final float z4 = (float)(Math.cos(newYaw3) * width + z);
        final float x5 = (float)(Math.sin(newYaw4) * width + x);
        final float z5 = (float)(Math.cos(newYaw4) * width + z);
        final float y2 = (float)(y + height);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x5, (double)y2, (double)z5).endVertex();
        worldrenderer.pos((double)x5, (double)y, (double)z5).endVertex();
        worldrenderer.pos((double)x5, (double)y, (double)z5).endVertex();
        worldrenderer.pos((double)x5, (double)y2, (double)z5).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x5, (double)y, (double)z5).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x5, (double)y2, (double)z5).endVertex();
        worldrenderer.endVertex();
        tessellator.draw();
    }
    public static void otherDrawOutlinedBoundingBox(final Entity entity, final float x, final float y, final float z, double width, final double height) {
        width *= 1.5;
        final float yaw1 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 45.0f;
        float newYaw1;
        if (yaw1 < 0.0f) {
            newYaw1 = 0.0f;
            newYaw1 += 360.0f - Math.abs(yaw1);
        }
        else {
            newYaw1 = yaw1;
        }
        newYaw1 *= -1.0f;
        newYaw1 *= 0.017453292f;
        final float yaw2 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 135.0f;
        float newYaw2;
        if (yaw2 < 0.0f) {
            newYaw2 = 0.0f;
            newYaw2 += 360.0f - Math.abs(yaw2);
        }
        else {
            newYaw2 = yaw2;
        }
        newYaw2 *= -1.0f;
        newYaw2 *= 0.017453292f;
        final float yaw3 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 225.0f;
        float newYaw3;
        if (yaw3 < 0.0f) {
            newYaw3 = 0.0f;
            newYaw3 += 360.0f - Math.abs(yaw3);
        }
        else {
            newYaw3 = yaw3;
        }
        newYaw3 *= -1.0f;
        newYaw3 *= 0.017453292f;
        final float yaw4 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 315.0f;
        float newYaw4;
        if (yaw4 < 0.0f) {
            newYaw4 = 0.0f;
            newYaw4 += 360.0f - Math.abs(yaw4);
        }
        else {
            newYaw4 = yaw4;
        }
        newYaw4 *= -1.0f;
        newYaw4 *= 0.017453292f;
        final float x2 = (float)(Math.sin(newYaw1) * width + x);
        final float z2 = (float)(Math.cos(newYaw1) * width + z);
        final float x3 = (float)(Math.sin(newYaw2) * width + x);
        final float z3 = (float)(Math.cos(newYaw2) * width + z);
        final float x4 = (float)(Math.sin(newYaw3) * width + x);
        final float z4 = (float)(Math.cos(newYaw3) * width + z);
        final float x5 = (float)(Math.sin(newYaw4) * width + x);
        final float z5 = (float)(Math.cos(newYaw4) * width + z);
        final float y2 = (float)(y + height);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x5, (double)y, (double)z5).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x5, (double)y2, (double)z5).endVertex();
        worldrenderer.pos((double)x5, (double)y, (double)z5).endVertex();
        worldrenderer.pos((double)x5, (double)y2, (double)z5).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x5, (double)y, (double)z5).endVertex();
        worldrenderer.pos((double)x5, (double)y2, (double)z5).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.endVertex();
        tessellator.draw();
    }
    public static void drawEntityServerESP(final Entity entity, final float red, final float green, final float blue, final float alpha, final float lineAlpha, final float lineWidth) {
        double d0 = entity.serverPosX / 32.0;
        double d2 = entity.serverPosY / 32.0;
        double d3 = entity.serverPosZ / 32.0;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase livingBase = (EntityLivingBase)entity;
            d0 = livingBase.realPosX / 32.0;
            d2 = livingBase.realPosY / 32.0;
            d3 = livingBase.realPosZ / 32.0;
        }
        final float x = (float)(d0 - RenderUtil.mc.getRenderPosX());
        final float y = (float)(d2 - RenderUtil.mc.getRenderPosY());
        final float z = (float)(d3 - RenderUtil.mc.getRenderPosZ());
        GL11.glColor4f(red, green, blue, alpha);
        otherDrawBoundingBox(entity, x, y, z, entity.width - 0.2f, entity.height + 0.1f);
        if (lineWidth > 0.0f) {
            GL11.glLineWidth(lineWidth);
            GL11.glColor4f(red, green, blue, lineAlpha);
            otherDrawOutlinedBoundingBox(entity, x, y, z, entity.width - 0.2f, entity.height + 0.1f);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    public static void drawRect(float x, float y, float x1, float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float) (color >> 24 & 255) / 255.0f;
        float var6 = (float) (color >> 16 & 255) / 255.0f;
        float var7 = (float) (color >> 8 & 255) / 255.0f;
        float var8 = (float) (color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        resetColor();
    }

    public static void drawNewRect(double x, double y, double x1, double y1, double size , float color1, float color2 , float color3) {
        cn.molokymc.prideplus.utils.render.RenderUtil.rectangleBordered(x, y, x1 + size, y1 + size, 0.5d, ColorUtils.getColor(90), ColorUtils.getColor(0));
        cn.molokymc.prideplus.utils.render.RenderUtil.rectangleBordered(x + 1.0f, y + 1.0f, (x1 + size - 1.0f), y1 + size - 1.0f, 1.0, ColorUtils.getColor(90),
                ColorUtils.getColor(61));
        cn.molokymc.prideplus.utils.render.RenderUtil.rectangleBordered(x + 2.5, y + 2.5, (x1 + size) - 2.5, (y1 + size) - 2.5, 0.5, ColorUtils.getColor(61),
                ColorUtils.getColor(0));
        double width = x1 - x;
        cn.molokymc.prideplus.utils.render.RenderUtil.drawGradientSideways(x + 3, y + 3, x + (width / 3), y + 4, new Color(81, 149, 219, 255).getRGB(), new Color(180, 49, 218, 255).getRGB());
        cn.molokymc.prideplus.utils.render.RenderUtil.drawGradientSideways(x + (width / 3), y + 3, x + ((width / 3) * 2), y + 4, new Color(180, 49, 218, 255).getRGB(), new Color(236, 93, 128, 255).getRGB());
        cn.molokymc.prideplus.utils.render.RenderUtil.drawGradientSideways(x + ((width / 3) * 2), y + 3, x + ((width / 3) * 3) - 1, y + 4, new Color(236, 93, 128, 255).getRGB(), new Color(167, 171, 90, 255).getRGB());
    }
    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(255, 255, 255, 255);
    }
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void drawTracerLine(Entity entity, float width, Color color, float alpha) {
        float ticks = mc.timer.renderPartialTicks;
        glPushMatrix();

        glLoadIdentity();

        mc.entityRenderer.orientCamera(ticks);
        double[] pos = ESPUtil.getInterpolatedPos(entity);

        glDisable(GL_DEPTH_TEST);
        GLUtil.setup2DRendering();

        double yPos = pos[1] + entity.height / 2f;
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);

        glBegin(GL_LINE_STRIP);
        color(color.getRGB(), alpha);
        glVertex3d(pos[0], yPos, pos[2]);
        glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);

        GLUtil.end2DRendering();

        glPopMatrix();
    }

    public static void drawMicrosoftLogo(float x, float y, float size, float spacing, float alpha) {
        float rectSize = size /2f - spacing;
        int alphaVal = (int) (255 * alpha);
        Gui.drawRect2(x, y, rectSize, rectSize, new Color(244, 83, 38, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y, rectSize, rectSize, new Color(130, 188, 6, alphaVal).getRGB());
        Gui.drawRect2(x, y + spacing + rectSize, rectSize, rectSize, new Color(5, 166, 241, alphaVal).getRGB());
        Gui.drawRect2(x + rectSize + spacing, y + spacing + rectSize, rectSize, rectSize, new Color(254, 186, 7, alphaVal).getRGB());
    }

    public static void drawMicrosoftLogo(float x, float y, float size, float spacing) {
        drawMicrosoftLogo(x, y, size, spacing, 1f);
    }


    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x,y,0,0, imgWidth, imgHeight, imgWidth, imgHeight);
        GLUtil.endBlend();
    }


    public static void fixBlendIssues() {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawUnfilledCircle(double x, double y, float radius, float lineWidth, int color) {
        GLUtil.setup2DRendering();
        color(color);
        glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_POINT_BIT);

        int i = 0;
        while (i <= 360) {
            glVertex2d(x + Math.sin((double) i * 3.141526 / 180.0) * (double) radius, y + Math.cos((double) i * 3.141526 / 180.0) * (double) radius);
            ++i;
        }

        glEnd();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
    }


    public static double ticks = 0;
    public static long lastFrame = 0;

    public static void drawCircle(Entity entity, float partialTicks, double rad, int color, float alpha) {
        /*Got this from the people i made the Gui for*/
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        GlStateManager.color(1, 1, 1, 1);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();

        final double x = MathUtils.interpolate(entity.lastTickPosX, entity.posX, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
        final double y = MathUtils.interpolate(entity.lastTickPosY, entity.posY, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY + Math.sin(ticks) + 1;
        final double z = MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;

        glBegin(GL_TRIANGLE_STRIP);

        for (float i = 0; i < (Math.PI * 2); i += (Math.PI * 2) / 64.F) {

            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            color(color, 0);

            glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ);

            color(color, .52f * alpha);


            glVertex3d(vecX, y, vecZ);
        }

        glEnd();


        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
        glBegin(GL_LINE_STRIP);
        GlStateManager.color(1, 1, 1, 1);
        color(color, .5f * alpha);
        for (int i = 0; i <= 180; i++) {
            glVertex3d(x - Math.sin(i * MathHelper.PI2 / 90) * rad, y, z + Math.cos(i * MathHelper.PI2 / 90) * rad);
        }
        glEnd();

        glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor4f(1f, 1f, 1f, 1f);
    }
    public static Color getRainbow(int offset, int speed, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() + offset) % speed) / (float) speed;
        return Color.getHSBColor(hue, saturation, brightness);
    }
    public static void drawIcarusESP(EntityLivingBase target, Color color, float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        float animationY = 0.0f;
        float animationY2 = 0.0f;
        int currentOffset = 0;
        if (animationTimer.hasReached(10)) {
            animationX++;
            animationTimer.reset();
        }

        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = animationX; i < 100 + animationX; i++) {
            final double c = (2 * i * Math.PI / 100);
            GL11.glVertex3d((Math.cos(c) * 0.5), animationY, (Math.sin(c) * 0.5));
            float actualAlpha = .4f * alpha;
            glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
            color(color.getRGB(), actualAlpha);
            animationY += target.height / 100;
            currentOffset += 10;
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 50 + animationX; i < 150 + animationX; i++) {
            final double c = (2 * i * Math.PI / 100);
            float actualAlpha = .4f * alpha;
            glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
            color(color.getRGB(), actualAlpha);
            GL11.glVertex3d((Math.cos(c) * 0.5), animationY2, (Math.sin(c) * 0.5));
            animationY2 += target.height / 100;
            currentOffset += 10;
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    //From rise, alan gave me this
    public static void drawFilledCircleNoGL(int x, int y, double r, int c, int quality) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GLUtil.setup2DRendering();
        color(c);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
        GLUtil.end2DRendering();
    }

    public static void renderBoundingBox(EntityLivingBase entityLivingBase, Color color, float alpha) {
        AxisAlignedBB bb = ESPUtil.getInterpolatedBoundingBox(entityLivingBase);
        GlStateManager.pushMatrix();
        GLUtil.setup2DRendering();
        GLUtil.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH);

        glDisable(GL_DEPTH_TEST);
        glDepthMask(true);
        glLineWidth(1);
        float actualAlpha = .5f * alpha;
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
        color(color.getRGB(), actualAlpha);
        RenderGlobal.renderCustomBoundingBox(bb, false, true);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        GLUtil.disableCaps();
        GLUtil.end2DRendering();

        GlStateManager.popMatrix();
    }

    public static void circleNoSmoothRGB(double x, double y, double radius, int color) {
        radius /= 2;
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        color(color);
        glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = (i * (Math.PI * 2)) / 360;
            glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
    }


    public static void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness, int rectColor, int outlineColor) {
        Gui.drawRect2(x, y, width, height, rectColor);
        glEnable(GL_LINE_SMOOTH);
        color(outlineColor);

        GLUtil.setup2DRendering();

        glLineWidth(outlineThickness);
        float cornerValue = (float) (outlineThickness * .19);

        glBegin(GL_LINES);
        glVertex2d(x, y - cornerValue);
        glVertex2d(x, y + height + cornerValue);
        glVertex2d(x + width, y + height + cornerValue);
        glVertex2d(x + width, y - cornerValue);
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x, y + height);
        glVertex2d(x + width, y + height);
        glEnd();

        GLUtil.end2DRendering();

        glDisable(GL_LINE_SMOOTH);
    }

    // Bad rounded rect method but the shader one requires scaling that sucks
    public static void renderRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtil.drawGoodCircle(x + radius, y + radius, radius, color);
        RenderUtil.drawGoodCircle(x + width - radius, y + radius, radius, color);
        RenderUtil.drawGoodCircle(x + radius, y + height - radius, radius, color);
        RenderUtil.drawGoodCircle(x + width - radius, y + height - radius, radius, color);

        Gui.drawRect2(x + radius, y, width - radius * 2, height, color);
        Gui.drawRect2(x, y + radius, width, height - radius * 2, color);
    }


    // Scales the data that you put in the runnable
    public static void scaleStart(float x, float y, float scale) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void scaleEnd() {
        glPopMatrix();
    }


    // TODO: Replace this with a shader as GL_POINTS is not consistent with gui scales
    public static void drawGoodCircle(double x, double y, float radius, int color) {
        color(color);
        GLUtil.setup2DRendering();

        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glPointSize(radius * (2 * mc.gameSettings.guiScale));

        glBegin(GL_POINTS);
        glVertex2d(x, y);
        glEnd();

        GLUtil.end2DRendering();
    }

    public static void fakeCircleGlow(float posX, float posY, float radius, Color color, float maxAlpha) {
        setAlphaLimit(0);
        glShadeModel(GL_SMOOTH);
        GLUtil.setup2DRendering();
        color(color.getRGB(), maxAlpha);

        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(posX, posY);
        color(color.getRGB(), 0);
        for (int i = 0; i <= 100; i++) {
            double angle = (i * .06283) + 3.1415;
            double x2 = Math.sin(angle) * radius;
            double y2 = Math.cos(angle) * radius;
            glVertex2d(posX + x2, posY + y2);
        }
        glEnd();

        GLUtil.end2DRendering();
        glShadeModel(GL_FLAT);
        setAlphaLimit(1);
    }

    // animation for sliders and stuff
    public static double animate(double endPoint, double current, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static void rotateStart(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        x += width / 2;
        y += height / 3;
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateStartReal(float x, float y, float width, float height, float rotation) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-x, -y, 0);
    }

    public static void rotateEnd() {
        glPopMatrix();
    }

    // Arrow for clickgui
    public static void drawClickGuiArrow(float x, float y, float size, Animation animation, int color) {
        glTranslatef(x, y, 0);
        color(color);

        GLUtil.setup2DRendering();

        glBegin(GL_TRIANGLE_STRIP);
        double interpolation = MathUtils.interpolate(0.0, size / 2.0, animation.getOutput().floatValue());
        if (animation.getOutput().floatValue() >= .48) {
            glVertex2d(size / 2f, MathUtils.interpolate(size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        glVertex2d(0, interpolation);

        if (animation.getOutput().floatValue() < .48) {
            glVertex2d(size / 2f, MathUtils.interpolate(size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        glVertex2d(size, interpolation);

        glEnd();

        GLUtil.end2DRendering();

        glTranslatef(-x, -y, 0);
    }

    // Draws a circle using traditional methods of rendering
    public static void drawCircleNotSmooth(double x, double y, double radius, int color) {
        radius /= 2;
        GLUtil.setup2DRendering();
        glDisable(GL_CULL_FACE);
        color(color);
        glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = i * .01745;
            glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        GLUtil.end2DRendering();
    }

    public static void scissor(double x, double y, double width, double height, Runnable data) {
        glEnable(GL_SCISSOR_TEST);
        scissor(x, y, width, height);
        data.run();
        glDisable(GL_SCISSOR_TEST);
    }
    public static void scissor_(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        double scale = (double)sr.getScaleFactor();
        y = (double)sr.getScaledHeight() - y;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorStart(double x, double y, double width, double height) {
        glEnable(GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorEnd() {
        glDisable(GL_SCISSOR_TEST);
    }


    // This will set the alpha limit to a specified value ranging from 0-1
    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    // This method colors the next avalible texture with a specified alpha value ranging from 0-1
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    // Colors the next texture without a specified alpha value
    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    /**
     * Bind a texture using the specified integer refrence to the texture.
     *
     * @see org.lwjgl.opengl.GL13 for more information about texture bindings
     */
    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    // Sometimes colors get messed up in for loops, so we use this method to reset it to allow new colors to be used
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
    public static void fastRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius) {
        float z;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (paramXStart + radius);
        double y1 = (paramYStart + radius);
        double x2 = (paramXEnd - radius);
        double y2 = (paramYEnd - radius);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
    }
    public static void drawHead(ResourceLocation skin, float x, float y, int width, int height, float alpha, float v) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);    
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        GLUtil.setup2DRendering();
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        color(startColor);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        color(endColor);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
        resetColor();
    }

    public static void drawGradientRectBordered(double left, double top, double right, double bottom, double width, int startColor, int endColor, int borderStartColor, int borderEndColor) {
        drawGradientRect(left + width, top + width, right - width, bottom - width, startColor, endColor);
        drawGradientRect(left + width, top, right - width, top + width, borderStartColor, borderEndColor);
        drawGradientRect(left, top, left + width, bottom, borderStartColor, borderEndColor);
        drawGradientRect(right - width, top, right, bottom, borderStartColor, borderEndColor);
        drawGradientRect(left + width, bottom - width, right - width, bottom, borderStartColor, borderEndColor);
    }


    public static int getRGB(int r, int g, int b) {
        return getRGB(r,g,b,255);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static int getRGB(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
    }
}
