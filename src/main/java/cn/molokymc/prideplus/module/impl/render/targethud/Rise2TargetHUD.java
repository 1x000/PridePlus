package cn.molokymc.prideplus.module.impl.render.targethud;

import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.impl.EaseBackIn;
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

import static cn.molokymc.prideplus.module.impl.combat.KillAura.target;
import static cn.molokymc.prideplus.utils.skidfont.FontManager.*;

public class Rise2TargetHUD extends TargetHUD {


    public Rise2TargetHUD() {
        super("Rise2");
    }
    Animation openAnimation1 = new EaseBackIn(350, 1,(float)1.2);
    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float width;
        float height;
        height = 37;
        width = Math.max(120.0F, product_sans_medium_22.getStringWidth(target.getName()) + 50.0F);
        this.setWidth(width);
        this.setHeight(height);
        //background
        RoundedUtil.drawRoundOutline(x-0.2f, y-0.2f, getWidth()+0.4f, getHeight()+0.4f, 7, 0.0000005f,new Color(0, 0, 0,25),HUDMod.getColor(500));
        //health bar
        RoundedUtil.drawGradientHorizontal(x + 40f, (y + getHeight() - (float)14.6), getWidth() - 48.2F, (float)4.4, 2, new Color(0, 0, 0, 150), new Color(0, 0, 0, 85));
        RoundedUtil.drawGradientHorizontal(x + 40f, (y + getHeight() - (float)14.6), target.getHealth() / target.getMaxHealth() * (getWidth() - 48.2F), (float)4.4, 2, HUDMod.getColor(0), HUDMod.getColor(500));
        //render playerface
        final int scaleOffset = (int) (target.hurtTime * 0.7f);
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + (float)3.5, y + (float)3.5, 30, 30, 5, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + (float)3.5 + scaleOffset / 2f, y + (float)3.5 + scaleOffset / 2f, 30 - scaleOffset, 30 - scaleOffset, (AbstractClientPlayer) target);
            //renderPlayer2D(x + (float)1.5, y + (float)1.5, 29, 29, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        }
        //target name
        product_sans_medium_20.drawString(target.getName(), x + 39f, y + 8, HUDMod.getColor(0).getRGB());
        //health text
        String healthText = String.valueOf((int)target.getHealth());
        product_sans_medium_18.drawString(healthText,x + 40f+getWidth() - 58F,y+8.5,HUDMod.getColor(500).getRGB());

    }
    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        if (glow) {
            RoundedUtil.drawRound(x+0.3f, y+0.3f, getWidth()-0.6f, getHeight()-0.6f, 8, ColorUtil.applyOpacity(HUDMod.getColor(500), 50));
        } else {
            RoundedUtil.drawRound(x+0.3f, y+0.3f, getWidth()-0.6f, getHeight()-0.6f, 8, Color.BLACK);
        }
    }
}