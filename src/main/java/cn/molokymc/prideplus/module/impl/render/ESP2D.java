package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.utils.skidfont.FontDrawer;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.utils.tuples.Pair;
import cn.molokymc.prideplus.commands.impl.FriendCommand;
import cn.molokymc.prideplus.event.impl.render.NametagRenderEvent;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.event.impl.render.Render3DEvent;
import cn.molokymc.prideplus.event.impl.render.ShaderEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.module.settings.impl.*;
import cn.molokymc.prideplus.utils.font.AbstractFontRenderer;
import cn.molokymc.prideplus.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class ESP2D extends Module {

    private final MultipleBoolSetting target = new MultipleBoolSetting("target",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true));
    public final BooleanSetting boxEsp = new BooleanSetting("Box", true);
    private final ModeSetting boxColorMode = new ModeSetting("Box Mode", "Sync", "Sync", "Custom");
    private final ColorSetting boxColor = new ColorSetting("Box Color", Color.PINK);
    private final BooleanSetting equipmentVisual = new BooleanSetting("Equipment", true);
    private final BooleanSetting nametags = new BooleanSetting("Nametags", true);
    private final NumberSetting scale = new NumberSetting("Tag Scale", .75, 1, .35, .05);

    private final BooleanSetting redTags = new BooleanSetting("Red Tags", false);
    private final MultipleBoolSetting nametagSettings = new MultipleBoolSetting("Nametag Settings",
            redTags,
            new BooleanSetting("Formatted Tags", false),
            new BooleanSetting("Add PostProcessing", false),
            new BooleanSetting("Health Text", true),
            new BooleanSetting("Background", true),
            new BooleanSetting("Round", true));

    public ESP2D() {
        super("ESP","目标标记", Category.RENDER, "Draws a box in 2D space around entitys");
        boxColorMode.addParent(boxEsp, ParentAttribute.BOOLEAN_CONDITION);
        boxColor.addParent(boxColorMode, modeSetting -> modeSetting.is("Custom"));

        scale.addParent(nametags, ParentAttribute.BOOLEAN_CONDITION);
        nametagSettings.addParent(nametags, ParentAttribute.BOOLEAN_CONDITION);

        addSettings(target, boxEsp, boxColorMode, boxColor,
                equipmentVisual, nametags, scale, nametagSettings);
    }


    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();

    @Override
    public void onNametagRenderEvent(NametagRenderEvent e) {
        if (nametags.isEnabled()) e.cancel();
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && ESPUtil.isInView(entity)) {
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
            }
        }
    }

    @Override
    public void onShaderEvent(ShaderEvent e) {

        if (nametagSettings.getSetting("Add PostProcessing").isEnabled() && nametags.isEnabled()) {
            for (Entity entity : entityPosition.keySet()) {
                Vector4f pos = entityPosition.get(entity);
                float x = pos.getX(), y = pos.getY(), right = pos.getZ(), bottom = pos.getW();

                if (entity instanceof EntityLivingBase) {
                    AbstractFontRenderer font = productregular18;
                    EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                    StringBuilder text = new StringBuilder(
                            (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : redTags.isEnabled() ? "§c" : "§f") + name);
                    if (nametagSettings.getSetting("Health Text").isEnabled()) {
                        text.append(String.format(" §7§r%s HP§7", df.format(renderingEntity.getHealth())));
                    }
                    double fontScale = scale.getValue();
                    float middle = x + ((right - x) / 2);
                    float textWidth = 0;
                    double fontHeight;
                    textWidth = font.getStringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;
                    fontHeight = font.getHeight() * fontScale;

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                    RenderUtil.resetColor();
                    GLUtil.startBlend();
                    /*if (nametagSettings.getSetting("Round").isEnabled()) {

                        RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (float) ((fontHeight / fontScale) + 4), 2, new Color(0, 0, 0,255));
                    } else {
                        Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (fontHeight / fontScale) + 4, new Color(0, 0, 0,255).getRGB());
                    }*/
                        if (nametagSettings.getSetting("Round").isEnabled()) {
                            RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 5)), textWidth + 6,
                                    (float) ((fontHeight / fontScale) + 6), 5, Color.BLACK);
                        } else {
                            Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 5)), textWidth + 6,
                                    (fontHeight / fontScale) + 6, Color.BLACK.getRGB());
                        }

                    glPopMatrix();

                }
            }
        }
    }


    private final NumberFormat df = new DecimalFormat("0.#");
    private Color firstColor = Color.BLACK, secondColor = Color.BLACK, thirdColor = Color.BLACK, fourthColor = Color.BLACK;


    @Override
    public void onRender2DEvent(Render2DEvent e) {

        if (boxColorMode.is("Custom")) {
            if (boxColor.isRainbow()) {
                firstColor = boxColor.getRainbow().getColor(0);
                secondColor = boxColor.getRainbow().getColor(90);
                thirdColor = boxColor.getRainbow().getColor(180);
                fourthColor = boxColor.getRainbow().getColor(270);
            } else {
                gradientColorWheel(Pair.of(boxColor.getColor(), boxColor.getAltColor()));
            }
        } else {
            if (HUDMod.isRainbowTheme()) {
                firstColor = HUDMod.color1.getRainbow().getColor(0);
                secondColor = HUDMod.color1.getRainbow().getColor(90);
                thirdColor = HUDMod.color1.getRainbow().getColor(180);
                fourthColor = HUDMod.color1.getRainbow().getColor(270);
            } else {
                gradientColorWheel(HUDMod.getClientColors());
            }
        }

        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ(),
                    bottom = pos.getW();

            if (entity instanceof EntityLivingBase) {
                AbstractFontRenderer font = productregular18;
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                if (nametags.isEnabled()) {
                    float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                    Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                    StringBuilder text = new StringBuilder(
                            (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : redTags.isEnabled() ? "§c" : "§f") + name);
                    if (nametagSettings.getSetting("Health Text").isEnabled()) {
                        text.append(String.format(" §7§r%s HP§7", df.format(renderingEntity.getHealth())));
                    }
                    double fontScale = scale.getValue();
                    float middle = x + ((right - x) / 2);
                    float textWidth;
                    double fontHeight = font.getHeight() * fontScale;
                    textWidth = font.getStringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);


                    if (nametagSettings.getSetting("Background").isEnabled()) {
                        if (nametagSettings.getSetting("Round").isEnabled()) {
                            RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 5)), textWidth + 6,
                                    (float) ((fontHeight / fontScale) + 6), 5, new Color(0, 0, 0,125));
                        } else {
                            Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 5)), textWidth + 6,
                                    (fontHeight / fontScale) + 6, new Color(0, 0, 0,125).getRGB());
                        }
                    }


                    RenderUtil.resetColor();
                    productregular18.drawString(text.toString(), middle, (float) (y - (fontHeight + 2)), HUDMod.getColor(0).getRGB());
                    }
                    glPopMatrix();

                if (equipmentVisual.isEnabled()) {
                    float scale = .4f;
                    float equipmentX = right + 5;
                    float equipmentY = y - 1;
                    glPushMatrix();
                    glTranslated(equipmentX, equipmentY, 0);
                    glScaled(scale, scale, 1);
                    glTranslated(-equipmentX, -y, 0);
                    RenderUtil.resetColor();
                    RenderHelper.enableGUIStandardItemLighting();
                    float seperation = 0f;
                    float length = (bottom - y) - 2;
                    for (int i = 3; i >= 0; i--) {
                        if (renderingEntity.getCurrentArmor(i) == null) {
                            seperation += (length / 3) / scale;
                            continue;
                        }
                        mc.getRenderItem().renderItemAndEffectIntoGUI(renderingEntity.getCurrentArmor(i), (int) equipmentX, (int) (equipmentY + seperation));
                        seperation += (length / 3) / scale;
                    }

                    RenderHelper.disableStandardItemLighting();
                    glPopMatrix();
                }
           }
            if (boxEsp.isEnabled()) {
                float outlineThickness = .5f;
                RenderUtil.resetColor();
                //top
                GradientUtil.drawGradientLR(x, y, (right - x), 1, 1, firstColor, secondColor);
                //left
                GradientUtil.drawGradientTB(x, y, 1, bottom - y, 1, firstColor, fourthColor);
                //bottom
                GradientUtil.drawGradientLR(x, bottom, right - x, 1, 1, fourthColor, thirdColor);
                //right
                GradientUtil.drawGradientTB(right, y, 1, (bottom - y) + 1, 1, secondColor, thirdColor);
            }

        }

    }

    private void gradientColorWheel(Pair<Color, Color> colors) {
        firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, colors.getFirst(), colors.getSecond(), false);
        secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90, colors.getFirst(), colors.getSecond(), false);
        thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180, colors.getFirst(), colors.getSecond(), false);
        fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270, colors.getFirst(), colors.getSecond(), false);
    }

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (target.getSetting("Players").isEnabled() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        if (target.getSetting("Animals").isEnabled() && entity instanceof EntityAnimal) {
            return true;
        }

        return target.getSetting("mobs").isEnabled() && entity instanceof EntityMob;
    }


}
