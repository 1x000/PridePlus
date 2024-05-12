package dev.stable.module.impl.render.targethud;

import dev.stable.utils.font.FontUtil;
import dev.stable.utils.skidfont.FontManager;
import dev.stable.module.impl.render.HUDMod;
import dev.stable.utils.Utils;
import dev.stable.utils.animations.ContinualAnimation;
import dev.stable.utils.font.CustomFont;
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

import static dev.stable.module.impl.combat.KillAura.target;
import static dev.stable.utils.skidfont.FontManager.*;

public class RiseTargetHUD extends TargetHUD {


    public RiseTargetHUD() {
        super("Rise");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float width;
        float height;
        height = 32;
        width = Math.max(120.0F, product_sans_regular_20.getStringWidth(target.getName()) + 50.0F);
        this.setWidth(width);
        this.setHeight(height);
        //background
        RoundedUtil.drawRound(x, y, width, getHeight(), 4, new Color(0, 0, 0,120));
        //health bar
        RoundedUtil.drawGradientHorizontal(x + 34f, (y + getHeight() - 13), width - 37.2F, 8, 1, new Color(0, 0, 0, 150), new Color(0, 0, 0, 85));
        RoundedUtil.drawGradientHorizontal(x + 34f, (y + getHeight() - 13), target.getHealth() / target.getMaxHealth() * (width - 37.2F), 8, 1, HUDMod.getColor(0), HUDMod.getColor(400));
        //render playerface
        final int scaleOffset = (int) (target.hurtTime * 0.7f);
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 2, y + 2, 28, 28, 2, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 2 + scaleOffset / 2f, y + 2 + scaleOffset / 2f, 29 - scaleOffset, 29 - scaleOffset, (AbstractClientPlayer) target);
            //renderPlayer2D(x + (float)1.5, y + (float)1.5, 29, 29, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        }
        //target name
        product_sans_regular_20.drawString(target.getName(), x + 33f, y + 3.5, Color.WHITE.getRGB());
        //health text
        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + ".0%";
        product_sans_light_18.drawString(healthText,x+58,y+18,Color.WHITE.getRGB());

    }
    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        if (glow) {
            RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, HUDMod.getColor(0));
        } else {
            RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, ColorUtil.applyOpacity(Color.BLACK, 100));
        }
    }
}