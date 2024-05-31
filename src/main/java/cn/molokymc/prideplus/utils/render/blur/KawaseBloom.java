package cn.molokymc.prideplus.utils.render.blur;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.render.GLUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.ShaderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class KawaseBloom
implements Utils {
    public static ShaderUtil kawaseDown = new ShaderUtil("kawaseDownBloom");
    public static ShaderUtil kawaseUp = new ShaderUtil("kawaseUpBloom");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, true);
    private static int currentIterations;
    private static final List<Framebuffer> framebufferList;

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();
        framebuffer = RenderUtil.createFrameBuffer(null, true);
        framebufferList.add(framebuffer);
        int i = 1;
        while ((float)i <= iterations) {
            Framebuffer currentBuffer = new Framebuffer((int)((double)KawaseBloom.mc.displayWidth / Math.pow(2.0, i)), (int)((double)KawaseBloom.mc.displayHeight / Math.pow(2.0, i)), true);
            currentBuffer.setFramebufferFilter(9729);
            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri((int)3553, (int)10242, (int)33648);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33648);
            GlStateManager.bindTexture(0);
            framebufferList.add(currentBuffer);
            ++i;
        }
    }

    public static void renderBlur(final int framebufferTexture, final int iterations, final int offset) {
        if (KawaseBloom.currentIterations != iterations || KawaseBloom.framebuffer.framebufferWidth != Pride.mc.displayWidth || KawaseBloom.framebuffer.framebufferHeight != Pride.mc.displayHeight) {
            initFramebuffers((float)iterations);
            KawaseBloom.currentIterations = iterations;
        }
        RenderUtil.setAlphaLimit(0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(1, 1);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderFBO(KawaseBloom.framebufferList.get(1), framebufferTexture, KawaseBloom.kawaseDown, (float)offset);
        for (int i = 1; i < iterations; ++i) {
            renderFBO(KawaseBloom.framebufferList.get(i + 1), KawaseBloom.framebufferList.get(i).framebufferTexture, KawaseBloom.kawaseDown, (float)offset);
        }
        for (int i = iterations; i > 1; --i) {
            renderFBO(KawaseBloom.framebufferList.get(i - 1), KawaseBloom.framebufferList.get(i).framebufferTexture, KawaseBloom.kawaseUp, (float)offset);
        }
        final Framebuffer lastBuffer = KawaseBloom.framebufferList.get(0);
        lastBuffer.framebufferClear();
        lastBuffer.bindFramebuffer(false);
        KawaseBloom.kawaseUp.init();
        KawaseBloom.kawaseUp.setUniformf("offset", (float)offset, (float)offset);
        KawaseBloom.kawaseUp.setUniformi("inTexture", 0);
        KawaseBloom.kawaseUp.setUniformi("check", 1);
        KawaseBloom.kawaseUp.setUniformi("textureToCheck", 16);
        KawaseBloom.kawaseUp.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
        KawaseBloom.kawaseUp.setUniformf("iResolution", (float)lastBuffer.framebufferWidth, (float)lastBuffer.framebufferHeight);
        GlStateManager.setActiveTexture(34000);
        RenderUtil.bindTexture(framebufferTexture);
        GlStateManager.setActiveTexture(33984);
        RenderUtil.bindTexture(KawaseBloom.framebufferList.get(1).framebufferTexture);
        ShaderUtil.drawQuads();
        KawaseBloom.kawaseUp.unload();
        GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Pride.mc.getFramebuffer().bindFramebuffer(false);
        RenderUtil.bindTexture(KawaseBloom.framebufferList.get(0).framebufferTexture);
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.startBlend();
        ShaderUtil.drawQuads();
        GlStateManager.bindTexture(0);
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.startBlend();
    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shader.init();
        RenderUtil.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("halfpixel", 1.0f / (float)framebuffer.framebufferWidth, 1.0f / (float)framebuffer.framebufferHeight);
        shader.setUniformf("iResolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);
        ShaderUtil.drawQuads();
        shader.unload();
    }

    static {
        framebufferList = new ArrayList<Framebuffer>();
    }
}

