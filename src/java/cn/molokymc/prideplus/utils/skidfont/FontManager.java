package cn.molokymc.prideplus.utils.skidfont;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    public static FontDrawer normal_bold_14 = new FontDrawer(getFont("harmony_bold.ttf", 14), true, true);
    public static FontDrawer normal_bold_20 = new FontDrawer(getFont("harmony_bold.ttf", 20), true, true);
    public static FontDrawer normal_bold_24 = new FontDrawer(getFont("harmony_bold.ttf", 24), true, true);
    public static FontDrawer small22 = new FontDrawer(getFont("harmony.ttf", 22), true, true);
    public static FontDrawer product_sans_regular_14 = new FontDrawer(getFont("product_sans_regular.ttf", 14), true, true);
    public static FontDrawer product_sans_regular_18 = new FontDrawer(getFont("product_sans_regular.ttf", 18), true, true);
    public static FontDrawer product_sans_regular_20 = new FontDrawer(getFont("product_sans_regular.ttf", 20), true, true);
    public static FontDrawer product_sans_light_18 = new FontDrawer(getFont("product_sans_light.ttf", 18), true, true);
    public static FontDrawer product_sans_medium_36 = new FontDrawer(getFont("product_sans_medium.ttf", 36), true, true);
    public static FontDrawer product_sans_bold_24 = new FontDrawer(getFont("product_sans_bold.ttf", 24), true, true);
    public static FontDrawer product_sans_bold_18 = new FontDrawer(getFont("product_sans_bold.ttf", 18), true, true);
    public static FontDrawer product_sans_medium_20 = new FontDrawer(getFont("product_sans_medium.ttf", 20), true, true);
    public static FontDrawer product_sans_medium_18 = new FontDrawer(getFont("product_sans_medium.ttf", 18), true, true);
    public static FontDrawer product_sans_medium_22 = new FontDrawer(getFont("product_sans_medium.ttf", 22), true, true);
    public static FontDrawer small = new FontDrawer(getFont("harmony.ttf", 17), true, true);
    public static FontDrawer rubik15 = new FontDrawer(getFont("rubik.ttf", 15), true, true);
    public static FontDrawer rubik16 = new FontDrawer(getFont("rubik.ttf", 16), true, true);
    public static FontDrawer rubik17 = new FontDrawer(getFont("rubik.ttf", 17), true, true);
    public static FontDrawer rubik18 = new FontDrawer(getFont("rubik.ttf", 18), true, true);
    public static FontDrawer rubik20 = new FontDrawer(getFont("rubik.ttf", 20), true, true);
    public static FontDrawer edit12 = new FontDrawer(getFont("edit.ttf", 12), true, true);
    public static FontDrawer edit13 = new FontDrawer(getFont("edit.ttf", 13), true, true);
    public static FontDrawer edit14 = new FontDrawer(getFont("edit.ttf", 14), true, true);
    public static FontDrawer edit15 = new FontDrawer(getFont("edit.ttf", 15), true, true);
    public static FontDrawer edit16 = new FontDrawer(getFont("edit.ttf", 16), true, true);
    public static FontDrawer edit17 = new FontDrawer(getFont("edit.ttf", 17), true, true);
    public static FontDrawer edit18 = new FontDrawer(getFont("edit.ttf", 18), true, true);
    public static FontDrawer edit20 = new FontDrawer(getFont("edit.ttf", 20), true, true);
    public static FontDrawer edit25 = new FontDrawer(getFont("edit.ttf", 25), true, true);
    public static FontDrawer edit34 = new FontDrawer(getFont("edit.ttf", 34), true, true);

    public static Font getFont(String name, int size) {
        Font font;
        try {
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/Pride/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
            System.out.println("Loading " + name);
        } catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("Arial", Font.PLAIN, size);
        }
        return font;
    }
}