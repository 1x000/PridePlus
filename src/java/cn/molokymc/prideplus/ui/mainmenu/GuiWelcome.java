package cn.molokymc.prideplus.ui.mainmenu;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.utils.animations.AnimationUtil;
import cn.molokymc.prideplus.utils.misc.SoundUtils;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.skidfont.FontDrawer;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;


import java.awt.*;
import java.util.Random;

public class GuiWelcome extends GuiScreen {
    private long startTime;
    private float alpha = 1;
    private boolean needBlack;
    private int sb = 0;
    private int count = 3;
    public GuiWelcome() {
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis();
        }
        if(sb==150){
            count--;
        }
        if(sb==300){
            count--;
        }
        if(sb==450){
            count--;
        }
        drawRect(0, 0, width, height, RenderUtil.reAlpha(Color.BLACK.getRGB(), Math.abs(alpha)));
        FontManager.product_sans_medium_36.drawCenteredString("Welcome to "+Client.NAME+"!", width / 2, height / 2 - 20, Color.WHITE.getRGB());
        FontManager.product_sans_regular_20.drawCenteredString("User: You", width / 2, height / 2+3, Color.WHITE.getRGB());
        FontManager.edit15.drawCenteredString("轻点以进入主界面 ("+count+"s)", width / 2, height-15, Color.WHITE.darker().getRGB());
        sb++;
        if (needBlack) {
            if (alpha != 1) {
                alpha = AnimationUtil.getAnimationStateFlux(alpha, 1, 10f);
            } else {
                mc.displayGuiScreen(new CustomMainMenu());
            }
        } else {
            if (alpha != 0 && startTime + 1000L <= System.currentTimeMillis()) {
                alpha = AnimationUtil.getAnimationStateFlux(alpha, 0, 10f);
            }
            if (alpha <= 0 && startTime + 5000L <= System.currentTimeMillis()) {
                alpha = 0;
                needBlack = true;
            }
        }
        if(count==0) {
            needBlack = true;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        needBlack = true;
    }
}
