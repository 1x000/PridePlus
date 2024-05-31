package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.impl.render.ShaderEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.MultipleBoolSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.ui.clickguis.modern.ModernClickGui;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.blur.KawaseBloom;
import cn.molokymc.prideplus.utils.render.blur.KawaseBlur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

public class PostProcessing
        extends Module {
    public final BooleanSetting blur = new BooleanSetting("Blur", true);
    private final NumberSetting iterations = new NumberSetting("Blur Iterations", 2.0, 8.0, 1.0, 1.0);
    private final NumberSetting offset = new NumberSetting("Blur Offset", 3.0, 10.0, 1.0, 1.0);
    private final BooleanSetting bloom = new BooleanSetting("Bloom", true);
    public static MultipleBoolSetting glowOptions = new MultipleBoolSetting("Glow Options",
            new BooleanSetting("Arraylist", true),
            new BooleanSetting("Watermark", true),
            new BooleanSetting("Statistics", true),
            new BooleanSetting("CliCkgui", true),
            new BooleanSetting("TargetHud", true),
            new BooleanSetting("Notifications", false));
    private final NumberSetting shadowRadius = new NumberSetting("Bloom Iterations", 3.0, 8.0, 1.0, 1.0);
    private final NumberSetting shadowOffset = new NumberSetting("Bloom Offset", 1.0, 10.0, 1.0, 1.0);
    private String currentMode;
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public PostProcessing() {
        super("PostProcessing","后处理", Category.RENDER, "blurs shit");
        this.shadowRadius.addParent(this.bloom, ParentAttribute.BOOLEAN_CONDITION);
        this.shadowOffset.addParent(this.bloom, ParentAttribute.BOOLEAN_CONDITION);
        glowOptions.addParent(this.bloom, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(this.blur, this.iterations, this.offset, this.bloom, glowOptions, this.shadowRadius, this.shadowOffset);
    }

    public void stuffToBlur(boolean bloom) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (PostProcessing.mc.currentScreen instanceof GuiChat) {
            Gui.drawRect2(2.0, (float)sr.getScaledHeight() - 14.0f * GuiChat.openingAnimation.getOutput().floatValue(), sr.getScaledWidth() - 4, 12.0, Color.BLACK.getRGB());
        }
        if (PostProcessing.mc.currentScreen == ClickGUIMod.dropdownClickGui) {
            ClickGUIMod.dropdownClickGui.renderEffects();
        }
        if (PostProcessing.mc.currentScreen == ClickGUIMod.dropdownClickGui || PostProcessing.mc.currentScreen == ClickGUIMod.modernClickGui || PostProcessing.mc.currentScreen == ClickGUIMod.compactClickgui) {
            Pride.INSTANCE.getSideGui().drawForEffects(bloom);
            Pride.INSTANCE.getSearchBar().drawEffects();
        }
        RenderUtil.resetColor();
        PostProcessing.mc.ingameGUI.getChatGUI().renderChatBox();
        RenderUtil.resetColor();
        PostProcessing.mc.ingameGUI.renderScoreboardBlur(sr);
        RenderUtil.resetColor();
        NotificationsMod notificationsMod = Pride.INSTANCE.getModuleCollection().getModule(NotificationsMod.class);
        if (notificationsMod.isEnabled()) {
            notificationsMod.renderEffects(glowOptions.getSetting("Notifications").isEnabled());
        }
        if (bloom && PostProcessing.mc.currentScreen instanceof ModernClickGui) {
            ClickGUIMod.modernClickGui.drawBigRect();
        }
    }

    public void blurScreen() {
        if (!this.enabled) {
            return;
        }
        if (this.blur.isEnabled()) {
            (this.stencilFramebuffer = RenderUtil.createFrameBuffer(this.stencilFramebuffer)).framebufferClear();
            this.stencilFramebuffer.bindFramebuffer(false);
            Pride.INSTANCE.getEventProtocol().handleEvent(new ShaderEvent(false, glowOptions));
            this.stuffToBlur(false);
            this.stencilFramebuffer.unbindFramebuffer();
            KawaseBlur.renderBlur(this.stencilFramebuffer.framebufferTexture, this.iterations.getValue().intValue(), this.offset.getValue().intValue());
        }
        if (this.bloom.isEnabled()) {
            this.stencilFramebuffer = RenderUtil.createFrameBuffer(this.stencilFramebuffer);
            this.stencilFramebuffer.framebufferClear();
            this.stencilFramebuffer.bindFramebuffer(false);
            Pride.INSTANCE.getEventProtocol().handleEvent(new ShaderEvent(true, glowOptions));
            this.stuffToBlur(true);
            this.stencilFramebuffer.unbindFramebuffer();
            KawaseBloom.renderBlur(this.stencilFramebuffer.framebufferTexture, this.shadowRadius.getValue().intValue(), this.shadowOffset.getValue().intValue());
        }
    }
}

