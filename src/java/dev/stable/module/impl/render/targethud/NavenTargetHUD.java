package dev.stable.module.impl.render.targethud;

import dev.stable.utils.skidfont.FontManager;
import dev.stable.utils.animations.ContinualAnimation;
import dev.stable.utils.objects.GradientColorWheel;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.render.RoundedUtil;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.text.DecimalFormat;

public class NavenTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1O = new DecimalFormat("0.#");

    public NavenTargetHUD() {
        super("Naven");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float width = 130;
        float height = 50;
        setWidth(width);
        setHeight(height);

        RoundedUtil.drawRound(x, y, width, height, 5, new Color(10, 10, 30, 120));
        RenderUtil.drawHead(target.getLocationSkin(), x + 7, y + 7, 30, 30, 255F, 255.0F);
        RoundedUtil.drawRound(x + 5, y + height - 7, (target.getHealth() / target.getMaxHealth()) * width - 10, 3, 2, new Color(160, 42,  42));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        FontManager.normal_bold_24.drawString(target.getName(), x + 40, y + 8, new Color(-1));
        FontManager.rubik17.drawString("Health: " + decimalFormat.format(target.getHealth()), x + 40, y + 22, new Color(-1));
        FontManager.rubik17.drawString("Distance: " + decimalFormat.format(target.getDistanceToEntity(mc.thePlayer)), x + 40, y + 30, new Color(-1));
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        GradientColorWheel colors = new GradientColorWheel();
        RoundedUtil.drawGradientRound(x, y, getWidth(), getHeight(), 5, colors.getColor1(), colors.getColor4(), colors.getColor2(), colors.getColor3());

    }

}