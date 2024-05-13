package cn.molokymc.prideplus.module.impl.render.targethud;

import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.utils.animations.ContinualAnimation;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.utils.misc.MathUtils;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.render.StencilUtil;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class RadiusTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public RadiusTargetHUD() {
        super("Radius");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(100, FontManager.rubik17.getStringWidth(target.getName()) + 45));
        setHeight(35);
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);
        Color color = new Color(20, 18, 18, (int) (145 * alpha));
        int textColor = ColorUtil.applyOpacity(-1, alpha);
        int bg = new Color(0, 0, 0, 0.4F * alpha).getRGB();


        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 5, color);

        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 1, y + 2, 29, 31, 3, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 1, y + 2, 29, 31, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontManager.small.drawStringWithShadow("?", x + 13, y + 12 - FontManager.small.getHeight() / 2f, textColor);
        }


        FontManager.normal_bold_14.drawStringWithShadow(target.getName(), x + 32, y + 4, textColor);


        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        float realHealthWidth = getWidth() - 38;
        float realHealthHeight = 6;
        animation.animate(realHealthWidth * healthPercent, 30);
        Color backgroundHealthColor = new Color(0, 0, 0, ((int) alpha * 145));

        float healthWidth = animation.getOutput();

        RoundedUtil.drawGradientHorizontal(x + 33.5f, (y + getHeight() - 10), healthWidth, realHealthHeight, 0.5f, c1, c2);
        int mcTextColor = ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha));

        String healthText = MathUtils.round(healthPercentage * 20, .00) + "HP";
        FontManager.small.drawStringWithShadow(healthText, x + 17 + (getWidth() / 2) - FontManager.small.getStringWidth(healthText) / 2f, y + 23.6f, mcTextColor);
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 5, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
