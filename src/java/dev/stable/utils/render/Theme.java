package dev.stable.utils.render;

import dev.stable.module.impl.render.HUDMod;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.utils.tuples.Pair;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Theme {
    GREEN_SPIRIT("Green Spirit", new Color(0, 135, 62), new Color(159, 226, 191), true),
    HOT_PINK("Hot Pink", new Color(231, 84, 128), new Color(172, 79, 198), true),
    PURPLE_FIRE("Purple Fire", new Color(104, 71, 141), new Color(177, 162, 202), true),
    SUNSET_PINK("Sunset Pink", new Color(255, 145, 20), new Color(245, 105, 231), true),
    PINK_BLOOD("Pink Blood", new Color(228, 0, 70), new Color(255, 166, 201), true),
    RED_NEON("Red Neon", new Color(255, 0, 0), new Color(128, 42, 16), true),
    DEEP_OCEAN("Deep Ocean", new Color(60, 82, 145), new Color(0, 20, 64), true),
    TROPICAL_ICE("Tropical Ice", new Color(102, 255, 209), new Color(6, 149, 255), true),
    Stable("Stable", dev.stable.Client.INSTANCE.getClientColor(), dev.stable.Client.INSTANCE.getAlternateClientColor(), true),
    CUSTOM_THEME("Custom Theme", HUDMod.color1.getColor(), HUDMod.color2.getColor());

    private static final Map<String, Theme> themeMap = new HashMap<>();

    private final String name;
    private final Pair<Color, Color> colors;
    private final boolean  gradient;

    Theme(String name, Color color, Color colorAlt) {
        this(name, color, colorAlt, false);
    }

    Theme(String name, Color color, Color colorAlt, boolean gradient) {
        this.name = name;
        colors = Pair.of(color, colorAlt);
        this.gradient = gradient;
    }

    public static void init() {
        Arrays.stream(values()).forEach(theme -> themeMap.put(theme.getName(), theme));
    }

    public Pair<Color, Color> getColors() {
        if (this.equals(Theme.CUSTOM_THEME)) {
            if (HUDMod.color1.isRainbow()) {
                return Pair.of(HUDMod.color1.getColor(), HUDMod.color1.getAltColor());
            } else return Pair.of(HUDMod.color1.getColor(), HUDMod.color2.getColor());
        } else return colors;
    }

    public static Pair<Color, Color> getThemeColors(String name) {
        return get(name).getColors();
    }

    public static ModeSetting getModeSetting(String name, String defaultValue) {
        return new ModeSetting(name, defaultValue, Arrays.stream(Theme.values()).map(Theme::getName).toArray(String[]::new));
    }

    public static Theme get(String name) {
        return themeMap.get(name);
    }

    public static Theme getCurrentTheme() {
        return Theme.get(HUDMod.theme.getMode());
    }
}
