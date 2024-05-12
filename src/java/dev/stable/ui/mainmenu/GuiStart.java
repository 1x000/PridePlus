package dev.stable.ui.mainmenu;

import dev.stable.Client;
import dev.stable.module.impl.render.HUDMod;
import dev.stable.ui.SplashScreen;
import dev.stable.utils.client.RegionalAbuseUtil;
import dev.stable.utils.misc.SoundUtils;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.skidfont.FontManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import java.awt.*;

import static dev.stable.utils.render.RenderUtil.drawCircle;
import static org.lwjgl.opengl.GL11.*;

@NativeObfuscation
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
            SoundUtils.playSound(new ResourceLocation("Pride/sounds/login.wav"), 0.8f);
        }
        if(count<=50){
            for (int i = 0; i < 2; ++i) {
                final int rot = (int)(System.nanoTime() / 2000000L * i % 360L);
                drawCircle(width / 2,  (float)(height / 2), (float)(i * 8), rot - 160-15, rot+15,new Color(30,30,30).getRGB());
            }
            RenderUtil.drawLoadingCircle(width / 2, (float)(height / 2));
            FontManager.edit20.drawCenteredString("正在加载"+ SplashScreen.CURRENT, width / 2, height / 2+50 , new Color(255, 255, 255, 255).getRGB());

        }
        if(count>50){
            Display.setTitle(Client.NAME + " " + Client.VERSION  + " | "+ "我是"+RegionalAbuseUtil.country+"的"+Client.INSTANCE.username);
            FontManager.edit25.drawCenteredString("初始化完毕！", width / 2, height / 2, Color.WHITE.getRGB());
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
            time += mc.getDebugFPS() < 60 ? 0 : (float) (0.002 * RenderUtil.deltaTime);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(count>50){
            mc.displayGuiScreen(new GuiWelcome());
        }
    }
}
