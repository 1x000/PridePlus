package cn.molokymc.prideplus.utils.render.blur;

import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.render.GLUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.ShaderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class KawaseBlur
implements Utils {
    public static ShaderUtil kawaseDown = new ShaderUtil("kawaseDown");
    public static ShaderUtil kawaseUp = new ShaderUtil("kawaseUp");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static int currentIterations;
    private static final List<Framebuffer> framebufferList;

    public static void setupUniforms(float offset) {
        kawaseDown.setUniformf("offset", offset, offset);
        kawaseUp.setUniformf("offset", offset, offset);
    }

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();
        framebuffer = RenderUtil.createFrameBuffer(null);
        framebufferList.add(framebuffer);
        int i = 1;
        while ((float)i <= iterations) {
            Framebuffer currentBuffer = new Framebuffer((int)((double)KawaseBlur.mc.displayWidth / Math.pow(2.0, i)), (int)((double)KawaseBlur.mc.displayHeight / Math.pow(2.0, i)), false);
            currentBuffer.setFramebufferFilter(9729);
            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri((int)3553, (int)10242, (int)33648);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33648);
            GlStateManager.bindTexture(0);
            framebufferList.add(currentBuffer);
            ++i;
        }
    }

    public static void renderBlur(int stencilFrameBufferTexture, int iterations, int offset) {
        int i;
        if (currentIterations != iterations || KawaseBlur.framebuffer.framebufferWidth != KawaseBlur.mc.displayWidth || KawaseBlur.framebuffer.framebufferHeight != KawaseBlur.mc.displayHeight) {
            KawaseBlur.initFramebuffers(iterations);
            currentIterations = iterations;
        }
        KawaseBlur.renderFBO(framebufferList.get(1), KawaseBlur.mc.getFramebuffer().framebufferTexture, kawaseDown, offset);
        for (i = 1; i < iterations; ++i) {
            KawaseBlur.renderFBO(framebufferList.get(i + 1), KawaseBlur.framebufferList.get((int)i).framebufferTexture, kawaseDown, offset);
        }
        for (i = iterations; i > 1; --i) {
            KawaseBlur.renderFBO(framebufferList.get(i - 1), KawaseBlur.framebufferList.get((int)i).framebufferTexture, kawaseUp, offset);
        }
        Framebuffer lastBuffer = framebufferList.get(0);
        lastBuffer.framebufferClear();
        lastBuffer.bindFramebuffer(false);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformi("inTexture", 0);
        kawaseUp.setUniformi("check", 1);
        kawaseUp.setUniformi("textureToCheck", 16);
        kawaseUp.setUniformf("halfpixel", 1.0f / (float)lastBuffer.framebufferWidth, 1.0f / (float)lastBuffer.framebufferHeight);
        kawaseUp.setUniformf("iResolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
        GL13.glActiveTexture((int)34000);
        RenderUtil.bindTexture(stencilFrameBufferTexture);
        GL13.glActiveTexture((int)33984);
        RenderUtil.bindTexture(KawaseBlur.framebufferList.get((int)1).framebufferTexture);
        ShaderUtil.drawQuads();
        kawaseUp.unload();
        mc.getFramebuffer().bindFramebuffer(true);
        RenderUtil.bindTexture(KawaseBlur.framebufferList.get((int)0).framebufferTexture);
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.startBlend();
        ShaderUtil.drawQuads();
        GlStateManager.bindTexture(0);
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

