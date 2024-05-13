package cn.molokymc.prideplus.ui.clickguis.test;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

public class TestClickgui
extends GuiScreen {
    float x = 100.0f;
    float y = 100.0f;
    private final float rectWidth = 360;
    private final float categoryRectHeight = 290;
    float dragX;
    float dragY;
    boolean isDragging;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.isDragging) {
            this.x = this.dragX + (float)mouseX;
            this.y = this.dragY + (float)mouseY;
        }
        Color bg = new Color(16, 16, 16, 255);
        Color textColor = new Color(126, 126, 126, 255);

        RoundedUtil.drawRound(this.x, this.y, rectWidth, categoryRectHeight, 8f, bg);

        float yAdd = 62.0f;
        for (Category category : Category.values()) {
            RenderUtil.resetColor();
            float textWidth = FontManager.rubik16.getStringWidth(category.name) / 20 + yAdd;
            FontManager.rubik16.drawString(category.name, x + ((50 - textWidth + 30)),
                    y + Utils.tenacityBoldFont22.getMiddleOfBox( 20), textColor);
            yAdd -= 62.0f;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && RenderUtil.isHovered(mouseX, mouseY, this.x, this.y, rectWidth, categoryRectHeight)) {
            this.dragX = this.x - (float)mouseX;
            this.dragY = this.y - (float)mouseY;
            this.isDragging = true;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.isDragging = false;
        }
    }
}

