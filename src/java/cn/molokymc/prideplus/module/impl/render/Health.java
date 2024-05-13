//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package cn.molokymc.prideplus.module.impl.render;

import java.util.*;
import java.text.*;

import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.event.impl.render.RenderChestEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.utils.render.Colors;
import net.minecraft.client.gui.inventory.*;
import java.awt.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import net.minecraft.potion.*;
import net.minecraft.client.gui.*;

public final class Health extends Module
{
    private final DecimalFormat decimalFormat;
    private final Random random;
    private int width;

    public Health() {
        super("Health","血量显示", Category.DISPLAY,"render sb health bar");
        this.decimalFormat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));
        this.random = new Random();
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (!(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)) {
            this.renderHealth();
        }

    }

    private void renderHealth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GuiScreen screen = mc.currentScreen;
        float absorptionHealth = mc.thePlayer.getAbsorptionAmount();
        String string = this.decimalFormat.format((double)(mc.thePlayer.getHealth() / 2.0F)) + "§c❤ " + (absorptionHealth <= 0.0F ? "" : "§e" + this.decimalFormat.format((double)(absorptionHealth / 2.0F)) + "§6❤");
        int offsetY = 0;
        if (mc.thePlayer.getHealth() >= 0.0F && mc.thePlayer.getHealth() < 10.0F || mc.thePlayer.getHealth() >= 10.0F && mc.thePlayer.getHealth() < 100.0F) {
            this.width = 3;
        }

        if (screen instanceof GuiInventory) {
            offsetY = 70;
        } else if (screen instanceof GuiContainerCreative) {
            offsetY = 80;
        } else if (screen instanceof GuiChest) {
            offsetY = ((GuiChest)screen).ySize / 2 - 15;
        }

        int x = (new ScaledResolution(mc)).getScaledWidth() / 2 - this.width;
        int y = (new ScaledResolution(mc)).getScaledHeight() / 2 + 25 + offsetY;
        Color color = Colors.blendColors(new float[]{0.0F, 0.5F, 1.0F}, new Color[]{new Color(255, 37, 0), Color.YELLOW, Color.GREEN}, mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth());
        mc.fontRendererObj.drawString(string, absorptionHealth > 0.0F ? (float)x - 15.5F : (float)x - 3.5F, (float)y, color.getRGB(), true);
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(Gui.icons);
        this.random.setSeed((long) mc.ingameGUI.getUpdateCounter() * 312871L);
        float width = (float)scaledResolution.getScaledWidth() / 2.0F - mc.thePlayer.getMaxHealth() / 2.5F * 10.0F / 2.0F;
        float maxHealth = mc.thePlayer.getMaxHealth();
        int lastPlayerHealth = mc.ingameGUI.lastPlayerHealth;
        int healthInt = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
        int l2 = -1;
        boolean flag = mc.ingameGUI.healthUpdateCounter > (long) mc.ingameGUI.getUpdateCounter() && (mc.ingameGUI.healthUpdateCounter - (long) mc.ingameGUI.getUpdateCounter()) / 3L % 2L == 1L;
        if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
            l2 = mc.ingameGUI.getUpdateCounter() % MathHelper.ceiling_float_int(maxHealth + 5.0F);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for(int i6 = MathHelper.ceiling_float_int(maxHealth / 2.0F) - 1; i6 >= 0; --i6) {
            int xOffset = 16;
            if (mc.thePlayer.isPotionActive(Potion.poison)) {
                xOffset += 36;
            } else if (mc.thePlayer.isPotionActive(Potion.wither)) {
                xOffset += 72;
            }

            int k3 = 0;
            if (flag) {
                k3 = 1;
            }

            float renX = width + (float)(i6 % 10 * 8);
            float renY = (float)scaledResolution.getScaledHeight() / 2.0F + 15.0F + (float)offsetY;
            if (healthInt <= 4) {
                renY += (float)this.random.nextInt(2);
            }

            if (i6 == l2) {
                renY -= 2.0F;
            }

            int yOffset = 0;
            if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                yOffset = 5;
            }

            Gui.drawTexturedModalRect(renX, renY, 16 + k3 * 9, 9 * yOffset, 9, 9);
            if (flag) {
                if (i6 * 2 + 1 < lastPlayerHealth) {
                    Gui.drawTexturedModalRect(renX, renY, xOffset + 54, 9 * yOffset, 9, 9);
                }

                if (i6 * 2 + 1 == lastPlayerHealth) {
                    Gui.drawTexturedModalRect(renX, renY, xOffset + 63, 9 * yOffset, 9, 9);
                }
            }

            if (i6 * 2 + 1 < healthInt) {
                Gui.drawTexturedModalRect(renX, renY, xOffset + 36, 9 * yOffset, 9, 9);
            }

            if (i6 * 2 + 1 == healthInt) {
                Gui.drawTexturedModalRect(renX, renY, xOffset + 45, 9 * yOffset, 9, 9);
            }
        }

        GL11.glPopMatrix();
    }
}
