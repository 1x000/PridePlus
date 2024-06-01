package cn.molokymc.prideplus.ui.mainmenu;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.ui.SplashScreen;
import cn.molokymc.prideplus.utils.client.RegionalAbuseUtil;
import cn.molokymc.prideplus.utils.misc.SoundUtils;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.compatibility.display.Display;

import java.awt.*;

import static cn.molokymc.prideplus.utils.render.RenderUtil.drawCircle;
import static org.lwjgl.opengl.GL11.*;

public class GuiStart extends GuiScreen {
    public final ShaderProgram menuShader = new ShaderProgram("fragment/load.fsh");
    private int count = 0;
    private float time;
    public GuiStart(){
        RegionalAbuseUtil.getAddressByIP();
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        count++;
        drawRect(0, 0, width, height,Color.BLACK.getRGB());
        //this.render(new ScaledResolution(mc));
        if(count==30){
            SoundUtils.playSound(new ResourceLocation(Pride.NAME + "/sounds/login.wav"), 0.8f);
        }
        if(count<=50){
            for (int i = 0; i < 2; ++i) {
                final int rot = (int)(System.nanoTime() / 2000000L * i % 360L);
                drawCircle((float) width / 2,  (float)(height / 2), (float)(i * 8), rot - 160-15, rot+15,new Color(30,30,30).getRGB());
            }
            RenderUtil.drawLoadingCircle((float) width / 2, (float) (height / 2));
            FontManager.edit20.drawCenteredString("正在加载"+ SplashScreen.CURRENT, (double) width / 2, (double) height / 2 + 50 , new Color(255, 255, 255, 255).getRGB());

        }
        if(count>50){
            Display.setTitle(Pride.NAME + " " + Pride.VERSION  + " | " + "你是来自 " + RegionalAbuseUtil.country + " 的觉醒者");
            FontManager.edit25.drawCenteredString("初始化完毕！", (double) width / 2, (double) height / 2, Color.WHITE.getRGB());
            //FontManager.edit18.drawCenteredString("单击屏幕以进行下一步", width / 2, height / 2+3, Color.WHITE.getRGB());
        }
        if(count>200)
        {
            mc.displayGuiScreen(new GuiWelcome());
        }
    }

    public void setupUniforms() {
        menuShader.setUniformf("time", time);
        menuShader.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }

    public final void render(final ScaledResolution scaledResolution) {
        {
            menuShader.init();
            setupUniforms();
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            menuShader.renderCanvas(scaledResolution);
            glDisable(GL_BLEND);
            menuShader.uninit();
            time += Minecraft.getDebugFPS() < 60 ? 0 : (float) (0.002 * RenderUtil.deltaTime);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(count>50){
            mc.displayGuiScreen(new GuiWelcome());
        }
    }
}
