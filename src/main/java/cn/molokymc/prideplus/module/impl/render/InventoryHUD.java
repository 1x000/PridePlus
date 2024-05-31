package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.event.impl.render.ShaderEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.objects.Dragging;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import cn.molokymc.prideplus.utils.skidfont.FontDrawer;

import java.awt.*;

public class InventoryHUD extends Module {


    private final Dragging Inventory_dragging = Pride.INSTANCE.createDrag(this, "Show your Inventory", 5, 150);
    public static final BooleanSetting bar = new BooleanSetting("Bar", false);
    float inventory_width = 165;
    float inventory_height = 75;
    FontDrawer font14 = FontManager.rubik16;
    FontDrawer font17 = FontManager.rubik17;

    public InventoryHUD() {
        super("InventoryHUD","背包显示", Category.DISPLAY, "InventoryHUD");
        addSettings(bar);
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        font17.drawStringWithShadow("InventoryHUD", Inventory_dragging.getX() + 28, Inventory_dragging.getY() + 1, -1);
        Inventory_dragging.setWidth(inventory_width);
        Inventory_dragging.setHeight(inventory_height);
        RoundedUtil.drawRound(Inventory_dragging.getX(), Inventory_dragging.getY(),
                Inventory_dragging.getWidth(), Inventory_dragging.getHeight() - 0.9f, 1f, Color.BLACK);
    }
    @Override
    public void onRender2DEvent(Render2DEvent event) {
        Inventory_dragging.setWidth(inventory_width);
        Inventory_dragging.setHeight(inventory_height);
        Color c1 = new Color(146, 146, 146,255);
        RoundedUtil.drawRound(Inventory_dragging.getX(), Inventory_dragging.getY(), 165, 14.2f, 0.1f, new Color(0, 0, 0, 165));
        RoundedUtil.drawRound(Inventory_dragging.getX(), Inventory_dragging.getY() + 15.5f, Inventory_dragging.getWidth(), inventory_height - 16.2f, 1f, new Color(0, 0, 0, 105));

        if (bar.isEnabled()) {
            RenderUtil.drawGradientSideways(Inventory_dragging.getX() - 0.2f, -1.2 + Inventory_dragging.getY(), 112.2F + Inventory_dragging.getX() + 54, Inventory_dragging.getY(), HUDMod.getClientColors().getFirst().getRGB(), HUDMod.getClientColors().getFirst().getRGB());
            //GlowUtils.drawGlow(Inventory_dragging.getX() + 2, Inventory_dragging.getY() + -3, 165, 3, 12, new Color(HUDMod.getClientColors().getFirst().getRed(), HUDMod.getClientColors().getFirst().getGreen(), HUDMod.getClientColors().getFirst().getBlue(), 180));
        }

        font17.drawStringWithShadow("Inventory", Inventory_dragging.getX() + 60, Inventory_dragging.getY() + 3, -1);
            if (emptyInventory()) {
                font17.drawStringWithShadow("Empty ", 66 + Inventory_dragging.getX(), Inventory_dragging.getY() + 35, -1);
                return;
            }
        //渲染背包物品
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        boolean hasStacks = false;
        for (int i1 = 9; i1 < mc.thePlayer.inventoryContainer.inventorySlots.size() - 9; ++i1) {
            Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i1);
            if (slot.getHasStack()) hasStacks = true;
            int i = slot.xDisplayPosition -7;
            int j = slot.yDisplayPosition -7;
            mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), (int) (Inventory_dragging.getX() + i + 2), (int) (Inventory_dragging.getY() + j - 60));
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, slot.getStack(), (int) (Inventory_dragging.getX() + i + 2), (int) (Inventory_dragging.getY() + j - 60), null);
        }
    }

    private boolean emptyInventory() {
        for (int i = 0; i < 45; i++) {
            if (i < 9) {
                continue;
            }
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                return false;
            }
        }
        return true;
    }
}
