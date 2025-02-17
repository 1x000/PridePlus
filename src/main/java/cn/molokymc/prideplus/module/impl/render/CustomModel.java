package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.impl.game.WorldEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.ColorSetting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.tuples.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @see net.minecraft.client.model.ModelPlayer#render(Entity, float, float, float, float, float, float)
 */
public class CustomModel extends Module {

    public static final ResourceLocation amongusModel = new ResourceLocation(Pride.NAME + "/Models/amogus.png");
    public static final ResourceLocation rabbitModel = new ResourceLocation(Pride.NAME + "/Models/rabbit.png");

    public static boolean enabled = false;

    public static final ModeSetting model = new ModeSetting("Model", "Among Us", "Among Us", "Rabbit");
    private static final ModeSetting mogusColorMode = new ModeSetting("Among Us Mode", "Random", "Random", "Sync", "Custom");
    private static final ColorSetting amongusColor = new ColorSetting("Among Us Color", Color.RED);

    public CustomModel() {
        super("Custom Model","自定义模型", Category.RENDER, "Renders an custom model on every player");
        mogusColorMode.addParent(model, modeSetting -> modeSetting.is("Among Us"));
        amongusColor.addParent(mogusColorMode, modeSetting -> modeSetting.is("Custom"));
        addSettings(model, mogusColorMode, amongusColor);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        enabled = false;
    }

    private static final Map<Object, Color> entityColorMap = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        entityColorMap.clear();
        enabled = true;
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        entityColorMap.clear();
    }

    public static Color getColor(Entity entity) {
        Color color = Color.WHITE;
        switch (mogusColorMode.getMode()) {
            case "Sync":
                Pair<Color, Color> clientColors = HUDMod.getClientColors();
                color = ColorUtil.interpolateColorsBackAndForth(15, 1, clientColors.getFirst(), clientColors.getSecond(), false);
                break;
            case "Random":
                if (entityColorMap.containsKey(entity)) {
                    color = entityColorMap.get(entity);
                } else {
                    color = ColorUtil.getRandomColor();
                    entityColorMap.put(entity, color);
                }
                break;
            case "Custom":
                color = amongusColor.getColor();
                break;
        }

        return color;
    }

    public static double getYOffset() {
        switch (model.getMode()) {
            case "Among Us":
                return 0.25;
        }
        return 0;
    }


}
