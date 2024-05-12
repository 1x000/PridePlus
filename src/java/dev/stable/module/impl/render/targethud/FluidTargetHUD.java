package dev.stable.module.impl.render.targethud;

import dev.stable.module.impl.render.HUDMod;
import dev.stable.utils.render.ColorUtil;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.render.RoundedUtil;
import java.awt.Color;
import java.text.DecimalFormat;

import dev.stable.utils.render.StencilUtil;
import dev.stable.utils.skidfont.FontManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class FluidTargetHUD extends TargetHUD {
    public FluidTargetHUD() {
        super("Test");
    }

    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float width;
        float height;
            width = Math.max(120.0F, tenacityBoldFont18.getStringWidth(target.getName()) + 50.0F);
            height = 35.0F;
            this.setWidth(width);
            this.setHeight(height);
            RoundedUtil.drawRound(x, y, width, height, 2.0F, new Color(0, 0, 0, 100));
        int textColor = ColorUtil.applyOpacity(-1, alpha);
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 2, y + 2, 29, 31, 2, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 2, y + 2, 29, 31, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontManager.small.drawStringWithShadow("?", x + 13, y + 12 - FontManager.small.getHeight() / 2f, textColor);
        }
            volteFont.size(18).drawStringWithShadow(target.getName(), x + 36.0F, y + 5.0F, new Color(((Color) HUDMod.getClientColors().getFirst()).getRGB()));
            RoundedUtil.drawRound(x + 36.0F, y + 10.0F + (float) volteFont.size(18).getHeight(), width - 40.0F, 5.0F, 2.0F, new Color(0, 0, 0, 30));
            //绘制血条
            RoundedUtil.drawGradientHorizontal(x + 36.0F, y + 10.0F + (float) volteFont.size(18).getHeight(), target.getHealth() / target.getMaxHealth() * (width - 40.0F), 5.0F, 2.0F, (Color) HUDMod.getClientColors().getFirst(), (Color) HUDMod.getClientColors().getSecond());
            tenacityBoldFont14.drawString((new DecimalFormat("0.00")).format((double) target.getHealth()) + " HP", x + 36.0F, y + height - 8.0F, new Color(-1));
        }

    public void renderEffects(float x, float y, float alpha, boolean glow) {
                RoundedUtil.drawRound(x, y, this.getWidth(), this.getHeight(), 2.0F, ColorUtil.applyOpacity(Color.BLACK, alpha));
            }
        }
