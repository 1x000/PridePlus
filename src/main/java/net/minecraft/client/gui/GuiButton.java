package net.minecraft.client.gui;

import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.skidfont.FontDrawer;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import java.awt.*;

public class GuiButton extends Gui
{
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    public int cs;
    public int alpha;
    /** Button width in pixels */
    protected int width;

    /** Button height in pixels */
    protected int height;

    /** The x position of this control. */
    public int xPosition;

    /** The y position of this control. */
    public int yPosition;

    /** The string displayed on this control. */
    public String displayString;
    public int id;

    /** True if this control is enabled, false to disable. */
    public boolean enabled;

    /** Hides the button completely if false. */
    public boolean visible;
    protected boolean hovered;
    public final Animation animation;
    public GuiButton(int buttonId, int x, int y, String buttonText)
    {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
        this.animation = new DecelerateAnimation(200, 1.0);
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        Color color1 = new Color(0F, 0F, 0F, this.alpha / 255.0F);
        int col1 = color1.getRGB();
        if (this.visible) {
            //IFontRenderer var4 = mc.fontRendererObj;
            //FontDrawer var4 = FontManager.edit16;
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
            this.animation.setDirection(this.hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            RoundedUtil.drawRound((float)this.xPosition, (float)this.yPosition, (float)this.width, (float)this.height, 2f, new Color(0, 0, 0, 90));
            RoundedUtil.drawRound((float)this.xPosition, (float)this.yPosition, (float) this.width, (float)(this.animation.getOutput() * this.height), 2f, new Color(0, 0, 0, 200));
            RenderUtil.resetColor();
            mouseDragged(mc, mouseX, mouseY);
            //int var5 = 14737632;
            FontManager.edit16.drawCenteredString(StringUtils.stripControlCodes(this.displayString), this.xPosition + this.width / 2, this.yPosition + (this.height - 5) / 2 - 2,Color.WHITE.getRGB());
        }
    }
    private static int HUDColor(){
        return new Color(HUDMod.color1.getColor().getRed(),HUDMod.color1.getColor().getGreen(), HUDMod.color1.getColor().getBlue(),200).getRGB();
    }
    private void updatefade() {
        if (this.enabled)
            if (this.hovered) {
                this.alpha += 10;
                if (this.alpha >= 210)
                    this.alpha = 210;
            } else {
                this.alpha -= 10;
                if (this.alpha <= 120)
                    this.alpha = 120;
            }
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    /**
     * Whether the mouse cursor is currently over the button.
     */
    public boolean isMouseOver()
    {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY)
    {
    }

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
}
