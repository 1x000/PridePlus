package cn.molokymc.prideplus.ui.altmanager;

import cn.molokymc.prideplus.utils.client.MathUtil;
import java.awt.Color;

public final class ColorUtils {
    public static int getRGB(int r2, int g2, int b2) {
        return ColorUtils.getRGB(r2, g2, b2, 255);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(MathUtil.interpolateInt(color1.getRed(), color2.getRed(), amount), MathUtil.interpolateInt(color1.getGreen(), color2.getGreen(), amount), MathUtil.interpolateInt(color1.getBlue(), color2.getBlue(), amount), MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static int getRGB(int r2, int g2, int b2, int a) {
        return (a & 0xFF) << 24 | (r2 & 0xFF) << 16 | (g2 & 0xFF) << 8 | b2 & 0xFF;
    }

    public static int[] splitRGB(int rgb) {
        int[] ints = new int[]{rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF};
        return ints;
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtils.interpolateColorHue(start, end, (float)angle / 360.0f) : ColorUtils.interpolateColorC(start, end, (float)angle / 360.0f);
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color resultColor = Color.getHSBColor(MathUtil.interpolateFloat(color1HSB[0], color2HSB[0], amount), MathUtil.interpolateFloat(color1HSB[1], color2HSB[1], amount), MathUtil.interpolateFloat(color1HSB[2], color2HSB[2], amount));
        return ColorUtils.applyOpacity(resultColor, (float)MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount) / 255.0f);
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1.0f, Math.max(0.0f, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * opacity));
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return ColorUtils.interpolateColorC(color1, color2, amount).getRGB();
    }

    public static Color brighter(Color color, float FACTOR) {
        int r2 = color.getRed();
        int g2 = color.getGreen();
        int b2 = color.getBlue();
        int alpha = color.getAlpha();
        int i = (int)(1.0 / (1.0 - (double)FACTOR));
        if (r2 == 0 && g2 == 0 && b2 == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r2 > 0 && r2 < i) {
            r2 = i;
        }
        if (g2 > 0 && g2 < i) {
            g2 = i;
        }
        if (b2 > 0 && b2 < i) {
            b2 = i;
        }
        return new Color(Math.min((int)((float)r2 / FACTOR), 255), Math.min((int)((float)g2 / FACTOR), 255), Math.min((int)((float)b2 / FACTOR), 255), alpha);
    }

    public static int getRGB(int rgb) {
        return 0xFF000000 | rgb;
    }

    public static int reAlpha(int rgb, int alpha) {
        return ColorUtils.getRGB(ColorUtils.getRed(rgb), ColorUtils.getGreen(rgb), ColorUtils.getBlue(rgb), alpha);
    }

    public static int getRed(int rgb) {
        return rgb >> 16 & 0xFF;
    }

    public static int getGreen(int rgb) {
        return rgb >> 8 & 0xFF;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static int getAlpha(int rgb) {
        return rgb >> 24 & 0xFF;
    }
}

