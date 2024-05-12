package dev.stable.viamcp.gui;

import dev.stable.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class AsyncVersionSlider extends GuiButton {
   private float dragValue = (float)(ViaLoadingBase.getProtocols().size() - ViaLoadingBase.getInstance().getTargetVersion().getIndex()) / (float)ViaLoadingBase.getProtocols().size();
   private final List<ProtocolVersion> values = ViaLoadingBase.getProtocols();
   private float sliderValue;
   public boolean dragging;

   public AsyncVersionSlider(int buttonId, int x, int y, int widthIn, int heightIn) {
      super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
      Collections.reverse(this.values);
      this.sliderValue = this.dragValue;
      this.displayString = ((ProtocolVersion)this.values.get((int)(this.sliderValue * (float)(this.values.size() - 1)))).getName();
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      super.drawButton(mc, mouseX, mouseY);
   }

   protected int getHoverState(boolean mouseOver) {
      return 0;
   }

   protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.dragging) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.dragValue = this.sliderValue;
            this.displayString = ((ProtocolVersion)this.values.get((int)(this.sliderValue * (float)(this.values.size() - 1)))).getName();
            ViaLoadingBase.getInstance().reload((ProtocolVersion)this.values.get((int)(this.sliderValue * (float)(this.values.size() - 1))));
         }

         mc.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawTexturedModalRect((float)(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8))), (float)this.yPosition, 0, 66, 4, 20);
         drawTexturedModalRect((float)(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4), (float)this.yPosition, 196, 66, 4, 20);
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
         this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
         this.dragValue = this.sliderValue;
         this.displayString = ((ProtocolVersion)this.values.get((int)(this.sliderValue * (float)(this.values.size() - 1)))).getName();
         ViaLoadingBase.getInstance().reload((ProtocolVersion)this.values.get((int)(this.sliderValue * (float)(this.values.size() - 1))));
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   public void mouseReleased(int mouseX, int mouseY) {
      this.dragging = false;
   }

   public void setVersion(int protocol) {
      this.dragValue = (float)(ViaLoadingBase.getProtocols().size() - ViaLoadingBase.fromProtocolId(protocol).getIndex()) / (float)ViaLoadingBase.getProtocols().size();
      this.sliderValue = this.dragValue;
      this.displayString = ((ProtocolVersion)this.values.get((int)(this.sliderValue * (float)(this.values.size() - 1)))).getName();
   }
}
