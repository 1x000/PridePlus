package dev.stable.module.impl.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.stable.event.impl.render.Render2DEvent;
import dev.stable.module.api.EventHandler;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.module.*;
import dev.stable.utils.Utils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Locale;

public class MotionBlur extends Module
{
    public final NumberSetting blurAmount;
    private Framebuffer framebuffer = null;
    private Framebuffer framebuffer_ = null;


    public MotionBlur() {
        super("MotionBlur","动态模糊", Category.RENDER, "motionblur your sb screen");
        this.blurAmount = new NumberSetting("Amount", 6.0, 10.0, 1.0, 1.0);
        addSettings(blurAmount);
    }
    /*@Override
    public void onEnable() {
            mc.entityRenderer.theShaderGroup = null;
            if(Utils.mc.theWorld != null) {
                try {
                    mc.entityRenderer.loadCustomShader();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

    public JsonObject getJsonObject() {
        String JSON = "{\"targets\":[\"swap\",\"previous\"],\"passes\":[{\"name\":\"phosphor\",\"intarget\":\"minecraft:main\",\"outtarget\":\"swap\",\"auxtargets\":[{\"name\":\"PrevSampler\",\"id\":\"previous\"}],\"uniforms\":[{\"name\":\"Phosphor\",\"values\":[%.2f, %.2f, %.2f]}]},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"previous\"},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"minecraft:main\"}]}";
        double amount = 0.7 + this.blurAmount.getValue() / 100.0 * 3.0 - 0.01;
        JsonParser parser = new JsonParser();
        return parser.parse(String.format(Locale.ENGLISH, JSON, amount, amount, amount)).getAsJsonObject();
    }
}
