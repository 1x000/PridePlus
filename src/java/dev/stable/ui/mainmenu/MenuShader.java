package dev.stable.ui.mainmenu;

import dev.stable.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import static org.lwjgl.opengl.GL11.*;

public class MenuShader {
    public final ShaderProgram menuShader = new ShaderProgram("fragment/" + "2.frag");
    protected final Minecraft mc = Minecraft.getMinecraft();
    public final ShaderProgram menuCustomShader = new ShaderProgram("fragment/" + CustomMainMenu.background +".frag");
    private float time;
    public final void render(final ScaledResolution scaledResolution, boolean useCustomFrag) {
        if (useCustomFrag) {
            menuCustomShader.init();
            setupUniforms();
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            menuCustomShader.renderCanvas(scaledResolution);
            glDisable(GL_BLEND);
            menuCustomShader.uninit();
        } else {
            menuShader.init();
            setupUniforms();
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            menuShader.renderCanvas(scaledResolution);
            glDisable(GL_BLEND);
            menuShader.uninit();
        }
        time += mc.getDebugFPS() < 60 ? 0 : (float) (0.002 * RenderUtil.deltaTime);
    }

    public void setupUniforms() {
        menuShader.setUniformf("time", time);
        menuShader.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }

}
