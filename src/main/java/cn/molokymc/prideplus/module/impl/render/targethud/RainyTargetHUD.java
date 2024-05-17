package cn.molokymc.prideplus.module.impl.render.targethud;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.ContinualAnimation;
import cn.molokymc.prideplus.utils.font.CustomFont;
import cn.molokymc.prideplus.utils.misc.MathUtils;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class RainyTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public RainyTargetHUD() {
        super("Rainy");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(100, FontManager.rubik17.getStringWidth(target.getName()) + 45));
        setHeight(32);
        double healthPercentage = MathHelper.clamp_float((target.getHealth()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        //Color c1 = new Color(246, 72, 60,255);
        Color colorbg = new Color(0, 0, 0, 105);
        Color color = new Color(0, 0, 0, 125);
        int textColor = ColorUtil.applyOpacity(-1, alpha);

        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 1, colorbg);
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 1, y + 1, 30, 30, 1, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 1, y + 1, 30, 30, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            RenderUtil.drawHead(mc.thePlayer.getLocationSkin(), x + 1, y + 1 , 30, 30, 4, 255);
        }

        FontManager.rubik17.drawStringWithShadow(target.getName(), x + 31.9f, y + 0, textColor);

        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        float realHealthWidth = getWidth() - 35.5f;
        float realHealthHeight = 8;
        animation.animate(realHealthWidth * healthPercent, 30);

        float healthWidth = animation.getOutput();

        RoundedUtil.drawGradientHorizontal(x + 33.5f, (y + getHeight() - 21), 64.4f, realHealthHeight, 0.02f, color, color);
        RoundedUtil.drawGradientHorizontal(x + 33.5f, (y + getHeight() - 21), healthWidth, realHealthHeight, 0.02f, HUDMod.getColor(0), HUDMod.getColor(500));
        //RoundedUtil.drawGradientHorizontal(x + 33.5f, (y + getHeight() - 21), healthWidth, realHealthHeight, 0.02f, c1, c1);

        int mcTextColor = ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha));

        String healthText = MathUtils.round(healthPercent * 20,0) + "";
        String i = String.valueOf((int)target.getHealth());
        FontManager.rubik17.drawStringWithShadow(i, x + 5f + (getWidth() / 3) - FontManager.rubik17.getStringWidth(i) / 2f, y + 22.1f, mcTextColor);
        CustomFont health = Utils.fluxICON14;
        health.drawStringWithShadow("s", x - 5.5f + (getWidth() / 2)  , y + 25.5f , HUDMod.getColor(0));
        //health.drawStringWithShadow("s", x - 5.5f + (getWidth() / 2)  , y + 25.5f , Color.WHITE);
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 0, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
