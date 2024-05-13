package cn.molokymc.prideplus.ui;

import java.awt.Color;

public class realpha {
    public static int reAlpha(int n, float n2) {
        Color color = new Color(n);
        return new Color(0.003921569f * (float)color.getRed(), 0.003921569f * (float)color.getGreen(), 0.003921569f * (float)color.getBlue(), n2).getRGB();
    }
}

