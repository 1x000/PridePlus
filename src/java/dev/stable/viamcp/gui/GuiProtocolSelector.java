package dev.stable.viamcp.gui;

import dev.stable.utils.Utils;
import dev.stable.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import dev.stable.viamcp.protocolinfo.ProtocolInfo;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class GuiProtocolSelector extends GuiScreen {
   private final GuiScreen parent;
   public SlotList list;

   public GuiProtocolSelector(GuiScreen parent) {
      this.parent = parent;
   }

   public void initGui() {
      super.initGui();
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 25, 200, 20, "Back"));
      this.list = new SlotList(Utils.mc, this.width, this.height, 32, this.height - 32);
   }

   protected void actionPerformed(GuiButton guiButton) throws IOException {
      this.list.actionPerformed(guiButton);
      if (guiButton.id == 1) {
         Utils.mc.displayGuiScreen(this.parent);
      }

   }

   public void handleMouseInput() throws IOException {
      this.list.handleMouseInput();
      super.handleMouseInput();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.list.drawScreen(mouseX, mouseY, partialTicks);
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0D, 2.0D, 2.0D);
      String title = EnumChatFormatting.BOLD + "ViaMCP";
      this.drawString(this.fontRendererObj, title, (int)(((float)this.width - this.fontRendererObj.getStringWidth(title) * 2.0F) / 4.0F), 5, -1);
      GlStateManager.popMatrix();
      this.drawString(this.fontRendererObj, "by EnZaXD/Flori2007", 1, 1, -1);
      this.drawString(this.fontRendererObj, "Discord: EnZaXD#6257", 1, 11, -1);
      ProtocolInfo protocolInfo = ProtocolInfo.fromProtocolVersion(ViaLoadingBase.getInstance().getTargetVersion());
      String versionTitle = "Version: " + ViaLoadingBase.getInstance().getTargetVersion().getName() + " - " + protocolInfo.getName();
      String versionReleased = "Released: " + protocolInfo.getReleaseDate();
      int fixedHeight = (5 + this.fontRendererObj.FONT_HEIGHT) * 2 + 2;
      this.drawString(this.fontRendererObj, "" + EnumChatFormatting.GRAY + EnumChatFormatting.BOLD + "Version Information", (int)(((float)this.width - this.fontRendererObj.getStringWidth("Version Information")) / 2.0F), fixedHeight, -1);
      this.drawString(this.fontRendererObj, versionTitle, (int)(((float)this.width - this.fontRendererObj.getStringWidth(versionTitle)) / 2.0F), fixedHeight + this.fontRendererObj.FONT_HEIGHT, -1);
      this.drawString(this.fontRendererObj, versionReleased, (int)(((float)this.width - this.fontRendererObj.getStringWidth(versionReleased)) / 2.0F), fixedHeight + this.fontRendererObj.FONT_HEIGHT * 2, -1);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   class SlotList extends GuiSlot {
      public SlotList(Minecraft mc, int width, int height, int top, int bottom) {
         super(mc, width, height, top + 30, bottom, 18);
      }

      protected int getSize() {
         return ViaLoadingBase.getProtocols().size();
      }

      protected void elementClicked(int i, boolean b, int i1, int i2) {
         ProtocolVersion protocolVersion = (ProtocolVersion)ViaLoadingBase.getProtocols().get(i);
         ViaLoadingBase.getInstance().reload(protocolVersion);
      }

      protected boolean isSelected(int i) {
         return false;
      }

      protected void drawBackground() {
         GuiProtocolSelector.this.drawDefaultBackground();
      }

      protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5) {
         GuiProtocolSelector.this.drawCenteredString(this.mc.fontRendererObj, (ViaLoadingBase.getInstance().getTargetVersion().getIndex() == i ? EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD : EnumChatFormatting.GRAY.toString()) + ((ProtocolVersion)ViaLoadingBase.getProtocols().get(i)).getName(), this.width / 2, i2 + 2, -1);
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5D, 0.5D, 0.5D);
         GuiProtocolSelector.this.drawCenteredString(this.mc.fontRendererObj, "PVN: " + ((ProtocolVersion)ViaLoadingBase.getProtocols().get(i)).getVersion(), this.width, (i2 + 2) * 2 + 20, -1);
         GlStateManager.popMatrix();
      }
   }
}
