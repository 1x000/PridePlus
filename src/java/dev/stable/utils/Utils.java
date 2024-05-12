package dev.stable.utils;

import dev.stable.utils.font.CustomFont;
import dev.stable.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;




public interface Utils {
    Minecraft mc = Minecraft.getMinecraft();
    IFontRenderer fr = mc.fontRendererObj;

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    FontUtil.FontType rubikFont = FontUtil.FontType.RUBIK,
            iconFont = FontUtil.FontType.ICON,
            tahomaFont = FontUtil.FontType.TAHOMA,
            fluxICON = FontUtil.FontType.FluxICON,
            harmony = FontUtil.FontType.HARMONY;
            FontUtil.FontType volteFont = FontUtil.FontType.VOLTE;
    FontUtil.FontType productFont = FontUtil.FontType.PRODUCT;

    CustomFont productregular18 = productFont.size(18),
            productregular16 = productFont.size(16),
            productregular14 = productFont.size(14),
            productregular12 = productFont.size(12),
            productregular20 = productFont.size(20);
    //Regular Fonts
    CustomFont rubikFont12 = rubikFont.size(12),
            rubikFont14 = rubikFont.size(14),
            rubikFont16 = rubikFont.size(16),
            rubikFont18 = rubikFont.size(18),
            rubikFont20 = rubikFont.size(20),
            rubikFont22 = rubikFont.size(22),
            rubikFont24 = rubikFont.size(24),
            rubikFont26 = rubikFont.size(26),
            rubikFont28 = rubikFont.size(28),
            rubikFont32 = rubikFont.size(32),
            rubikFont40 = rubikFont.size(40),
            rubikFont80 = rubikFont.size(80);
    //Bold Fonts
    CustomFont tenacityBoldFont12 = rubikFont12.getBoldFont(),
            tenacityBoldFont14 = rubikFont14.getBoldFont(),
            tenacityBoldFont16 = rubikFont16.getBoldFont(),
            tenacityBoldFont18 = rubikFont18.getBoldFont(),
            tenacityBoldFont20 = rubikFont20.getBoldFont(),
            tenacityBoldFont22 = rubikFont22.getBoldFont(),
            tenacityBoldFont26 = rubikFont26.getBoldFont(),
            tenacityBoldFont32 = rubikFont32.getBoldFont(),
            tenacityBoldFont40 = rubikFont40.getBoldFont();
    //Icon Fontsor i
    CustomFont iconFont16 = iconFont.size(16),

            iconFont20 = iconFont.size(20),
            iconFont35 = iconFont.size(35),
            iconFont40 = iconFont.size(40);

//FluxIcon
    CustomFont fluxICON14 = fluxICON.size(14);

    CustomFont volte18 = volteFont.size(18);
    CustomFont volte20 = volteFont.size(20);
//鸿蒙

}
