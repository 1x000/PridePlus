package dev.stable.module.impl.render.targethud;

import dev.stable.module.impl.render.HUDMod;
import dev.stable.utils.animations.ContinualAnimation;
import dev.stable.utils.font.FontUtil;
import dev.stable.utils.misc.MathUtils;
import dev.stable.utils.render.ColorUtil;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.render.RoundedUtil;
import dev.stable.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class StableTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public StableTargetHUD() {
        super("Stable");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(145, FontUtil.tenacityBoldFont26.getStringWidth(target.getName()) + 35));
        setHeight(30);
        float realHealthWidth = getWidth();
        float healthWidth = animation.getOutput();
        Color c1 = new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35);
        Color c2 = new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35);
        Color color = new Color(20, 18, 18, (int) (130 * alpha));
        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + "%";
        float healthPercentme = MathHelper.clamp_float((mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) / (mc.thePlayer.getMaxHealth() + mc.thePlayer.getAbsorptionAmount()), 0, 1);
                int textColor = ColorUtil.applyOpacity(-1, alpha);
        String sb = "";
        //bg
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 2, color);
        RoundedUtil.drawGradientHorizontal(x+2, y+9, 2, 12, 1, c1, c1);
        RoundedUtil.drawGradientHorizontal(x+2, y+12, 2, 6, 1, c1, c1);
        RoundedUtil.drawGradientHorizontal(x+2, y+12, 2, 6, 1, c1, c1);
        //health bar
        RoundedUtil.drawGradientHorizontal(x, y, healthWidth, getHeight(), 2f, c1, c2);
        //renderplayer
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 9, y + 3, 24, 24, 3, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 9, y + 3, 24, 24, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontUtil.rubikFont32.drawCenteredStringWithShadow("?", x + 26, y + 17 - FontUtil.rubikFont32.getHeight() / 2f, textColor);
        }
        //target name
        FontUtil.volte20.drawStringWithShadow(target.getName(), x + 38, y + 5, textColor);
        //health + info
        FontUtil.volte18.drawStringWithShadow(healthText,x+38,y+18,Color.WHITE);
        if(healthPercent>healthPercentme){
            sb="You may lose.";
            FontUtil.volte18.drawStringWithShadow(sb,x+70,y+18,Color.ORANGE);
        }
        if(healthPercentme>=healthPercent){
            sb="You may win.";
            FontUtil.volte18.drawStringWithShadow(sb,x+70,y+18,Color.GREEN);
        }



        float realHealthHeight = 5;
        animation.animate(realHealthWidth * healthPercent, 18);
        Color backgroundHealthColor = new Color(0, 0, 0, ((int) alpha * 110));


        //RoundedUtil.drawRound(x,y, 98, realHealthHeight, 0.5f, backgroundHealthColor);

        //RoundedUtil.drawRound(x + 39, (y + getHeight() - 12), 98, realHealthHeight, 0.5f, backgroundHealthColor);
        //RoundedUtil.drawGradientHorizontal(x + 39, (y + getHeight() - 12), healthWidth, realHealthHeight, 0.5f, c1, c2);


        //FontUtil.rubikFont16.drawStringWithShadow(healthText, x + 34 + Math.min(Math.max(1, healthWidth), realHealthWidth - 11), y + getHeight() - (14 + FontUtil.rubikFont16.getHeight()), textColor);
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 2, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
