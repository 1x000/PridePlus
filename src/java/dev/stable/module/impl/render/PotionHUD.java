package dev.stable.module.impl.render;

import dev.stable.utils.skidfont.FontManager;
import dev.stable.Client;
import dev.stable.event.impl.render.Render2DEvent;
import dev.stable.event.impl.render.ShaderEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.utils.fshShader.FshShaderRender;
import dev.stable.utils.objects.Dragging;
import dev.stable.utils.render.*;
import dev.stable.ui.realpha;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import dev.stable.utils.skidfont.FontDrawer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PotionHUD extends Module {

    private final Dragging Potion_dragging = Client.INSTANCE.createDrag(this, "PotionHUD", 721.0f, 34.0f);
    public static final BooleanSetting bar = new BooleanSetting("Bar", false);
    private final Map<Potion, PotionData> potionMap = new HashMap<Potion, PotionData>();
    FontDrawer font17 = FontManager.rubik17;
    public PotionHUD() {
        super("PotionHUD","药水显示", Category.DISPLAY, "PotionHUD");
        addSettings(bar);
    }
    @Override
    public void onShaderEvent(ShaderEvent event) {
            float x = this.Potion_dragging.getX();
            float y = this.Potion_dragging.getY();
            float potionDisplayx = 120.0f;
            float potionDisplayy = 60.0f;
            this.Potion_dragging.setWidth(potionDisplayx);
            this.Potion_dragging.setHeight(potionDisplayy);
            ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>(PotionHUD.mc.thePlayer.getActivePotionEffects());
            potions.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(I18n.format(e.getEffectName(), new Object[0]))));
            for (PotionEffect potionEffect : potions) {
                int potionMaxTime;
                int potionTime;
                PotionData potionData;
                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                String name = I18n.format(potion.getName(), new Object[0]);
                if (this.potionMap.containsKey(potion) && this.potionMap.get((Object)potion).level == potionEffect.getAmplifier()) {
                    potionData = this.potionMap.get(potion);
                } else {
                    potionData = new PotionData(new Translate(0.0f, -40.0f + y), potionEffect.getAmplifier());
                    this.potionMap.put(potion, potionData);
                }
                boolean flag = true;
                for (PotionEffect checkEffect : PotionHUD.mc.thePlayer.getActivePotionEffects()) {
                    if (checkEffect.getAmplifier() != potionData.level) continue;
                    flag = false;
                    break;
                }
                if (flag) {
                    this.potionMap.remove(potion);
                }
                try {
                    potionTime = Integer.parseInt(Potion.getDurationString(potionEffect).split(":")[0]);
                    potionMaxTime = Integer.parseInt(Potion.getDurationString(potionEffect).split(":")[1]);
                }
                catch (Exception ignored) {
                    potionTime = 100;
                    potionMaxTime = 1000;
                }
                int lifeTime = potionTime * 60 + potionMaxTime;
                if (potionData.getMaxTimer() == 0 || (double)lifeTime > (double)potionData.getMaxTimer()) {
                    potionData.maxTimer = lifeTime;
                }
                float state = 0.0f;
                if ((double)lifeTime >= 0.0) {
                    state = (float)((double)lifeTime / (double)potionData.getMaxTimer() * 100.0);
                }
                int position = Math.round(potionData.translate.getY() + 5.0f);
                state = Math.max(state, 2.0f);
                potionData.translate.interpolate(0.0f, y, 0.1);
                potionData.animationX = (float)this.getAnimationState(potionData.getAnimationX(), 1.2f * state, Math.max(10.0f, Math.abs(potionData.animationX - 1.2f * state) * 15.0f) * 0.3f);
                float posY = potionData.translate.getY() + 13.0f;
                String str1 = PotionHUD.get(name + " " + this.intToRomanByGreedy(potionEffect.getAmplifier() + 1));
                font17.drawString(str1, x + 29.0f, posY - (float)PotionHUD.mc.fontRendererObj.FONT_HEIGHT + 2.0f, Color.WHITE);
                String str = PotionHUD.get(Potion.getDurationString(potionEffect));
                font17.drawString(str, x + 29.0f, posY + 6.0f, Color.WHITE);
                y += 35.0f;
                potionDisplayy += 35.0f;
            }
        }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        float x = this.Potion_dragging.getX();
        float y = this.Potion_dragging.getY();
        float potionDisplayx = 120.0f;
        float potionDisplayy = 60.0f;
        this.Potion_dragging.setWidth(potionDisplayx);
        this.Potion_dragging.setHeight(potionDisplayy);
        ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>(PotionHUD.mc.thePlayer.getActivePotionEffects());
        potions.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(I18n.format(e.getEffectName(), new Object[0]))));
        for (PotionEffect potionEffect : potions) {
            int potionMaxTime;
            int potionTime;
            PotionData potionData;
            Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
            String name = I18n.format(potion.getName(), new Object[0]);
            if (this.potionMap.containsKey(potion) && this.potionMap.get((Object)potion).level == potionEffect.getAmplifier()) {
                potionData = this.potionMap.get(potion);
            } else {
                potionData = new PotionData(new Translate(0.0f, -40.0f + y), potionEffect.getAmplifier());
                this.potionMap.put(potion, potionData);
            }
            boolean flag = true;
            for (PotionEffect checkEffect : PotionHUD.mc.thePlayer.getActivePotionEffects()) {
                if (checkEffect.getAmplifier() != potionData.level) continue;
                flag = false;
                break;
            }
            if (flag) {
                this.potionMap.remove(potion);
            }
            try {
                potionTime = Integer.parseInt(Potion.getDurationString(potionEffect).split(":")[0]);
                potionMaxTime = Integer.parseInt(Potion.getDurationString(potionEffect).split(":")[1]);
            }
            catch (Exception ignored) {
                potionTime = 100;
                potionMaxTime = 1000;
            }
            int lifeTime = potionTime * 60 + potionMaxTime;
            if (potionData.getMaxTimer() == 0 || (double)lifeTime > (double)potionData.getMaxTimer()) {
                potionData.maxTimer = lifeTime;
            }
            float state = 0.0f;
            if ((double)lifeTime >= 0.0) {
                state = (float)((double)lifeTime / (double)potionData.getMaxTimer() * 100.0);
            }
            int position = Math.round(potionData.translate.getY() + 5.0f);
            state = Math.max(state, 2.0f);
            potionData.translate.interpolate(0.0f, y, 0.1);
            potionData.animationX = (float)this.getAnimationState(potionData.getAnimationX(), 1.2f * state, Math.max(10.0f, Math.abs(potionData.animationX - 1.2f * state) * 15.0f) * 0.3f);
            FshShaderRender.drawRoundedShadow(x + 0.0f, potionData.translate.getY(), 120.0f, 30.0f, 5.0f, 0.5f, realpha.reAlpha(Colors.BLACK.c, 0.1f));
            FshShaderRender.drawRoundedShadow(x + 0.0f, potionData.translate.getY(), potionData.animationX, 30.0f, 5.0f, 0.5f, realpha.reAlpha(new Color(0, 0, 0).brighter().getRGB(), 0.3f));
            float posY = potionData.translate.getY() + 13.0f;
            String str1 = PotionHUD.get(name + " " + this.intToRomanByGreedy(potionEffect.getAmplifier() + 1));
            rubikFont20.drawString(str1, x + 29.0f, posY - (float)PotionHUD.mc.fontRendererObj.FONT_HEIGHT + 2.0f, Color.WHITE);
            String str = PotionHUD.get(Potion.getDurationString(potionEffect));
            rubikFont16.drawString(str, x + 29.0f, posY + 6.0f, Color.WHITE);
            if (potion.hasStatusIcon()) {
                int statusIconIndex = potion.getStatusIconIndex();
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                GuiIngame cfr_ignored_0 = PotionHUD.mc.ingameGUI;
                GuiIngame.drawTexturedModalRect(x + 6.0f, y + 6.0f, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
            }
            y += 35.0f;
            potionDisplayy += 35.0f;
        }
    }

    public static String get(String text) {
        return HUDMod.hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
    }

    private String intToRomanByGreedy(int num) {
        int[] values = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; ++i) {
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }
        }
        return stringBuilder.toString();
    }

    private double getAnimationState(double animation, double finalState, double speed) {
        float add = (float)(0.01 * speed);
        animation = animation < finalState ? (animation + (double)add < finalState ? (animation += (double)add) : finalState) : (animation - (double)add > finalState ? (animation -= (double)add) : finalState);
        return animation;
    }
}
