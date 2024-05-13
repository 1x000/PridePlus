/*
 * Decompiled with CFR 0.152.
 */
package cn.molokymc.prideplus.utils.fshShader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class FshShaderRender {
    public static Minecraft mc = Minecraft.getMinecraft();
    private static final Tessellator TESSELLATOR = Tessellator.getInstance();
    private static final WorldRenderer BUILDER = TESSELLATOR.getWorldRenderer();
    private static final FshShaderLoader SHADOW = new FshShaderLoader("Shaders/shadow.fsh", true);

    public static void drawRoundedShadow(float x, float y, float x2, float y2, float softness, float radius, int color) {
        float[] c = ColorUtility.getRGBAf(color);
        GlStateManager.color(0.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        SHADOW.useProgram();
        SHADOW.setupUniform2f("size", (x2 - radius) * 2.0f, (y2 - radius) * 2.0f);
        SHADOW.setupUniform1f("softness", softness);
        SHADOW.setupUniform1f("radius", radius);
        SHADOW.setupUniform4f("color", c[0], c[1], c[2], c[3]);
        FshShaderRender.allocTextureRectangle(x - softness / 2.0f, y - softness / 2.0f, x2 + softness, y2 + softness);
        SHADOW.unloadProgram();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public static void drawImage(ResourceLocation tex, float x, float y, float x2, float y2) {
        mc.getTextureManager().bindTexture(tex);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        FshShaderRender.allocTextureRectangle(x, y, x2, y2);
        GlStateManager.bindTexture(0);
    }

    public static void allocTextureRectangle(float x, float y, float width, float height) {
        BUILDER.begin(7, DefaultVertexFormats.POSITION_TEX);
        BUILDER.pos(x, y, 0.0).tex(0.0, 0.0).endVertex();
        BUILDER.pos(x, y + height, 0.0).tex(0.0, 1.0).endVertex();
        BUILDER.pos(x + width, y + height, 0.0).tex(1.0, 1.0).endVertex();
        BUILDER.pos(x + width, y, 0.0).tex(1.0, 0.0).endVertex();
        TESSELLATOR.draw();
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        FshShaderRender.drawGradientRect(x, y, width, height, color, color, color, color);
    }

    public static void drawCRect(float x, float y, float width, float height, int color) {
        FshShaderRender.drawGradientRect(x, y, width - x, height, color, color, color, color);
    }

    public static void drawGradientRect(float x, float y, float width, float height, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorUtility.getRGBAf(color1);
        float[] c2 = ColorUtility.getRGBAf(color2);
        float[] c3 = ColorUtility.getRGBAf(color3);
        float[] c4 = ColorUtility.getRGBAf(color4);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR);
        BUILDER.pos(x, height + y, 0.0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        BUILDER.pos(width + x, height + y, 0.0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
        BUILDER.pos(width + x, y, 0.0).color(c3[0], c3[1], c3[2], c3[3]).endVertex();
        BUILDER.pos(x, y, 0.0).color(c4[0], c4[1], c4[2], c4[3]).endVertex();
        TESSELLATOR.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}

