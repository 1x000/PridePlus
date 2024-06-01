package cn.molokymc.prideplus.ui.altmanager;

import java.io.IOException;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiAltLogin
extends GuiScreen {
    private final GuiScreen previousScreen;
    private GuiTextField username;
    protected volatile String status = (Object)((Object)EnumChatFormatting.YELLOW) + "Pending...";

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                if (this.username.getText().length() != 0) {
                    this.onLogin(this.username.getText(), "");
                    break;
                }
                this.status = (Object)((Object)EnumChatFormatting.RED) + "Login failed!";
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 1145: {
                this.onLogin(StringUtils.randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_", 10), "");
            }
        }
    }

    public abstract void onLogin(String var1, String var2);

    @Override
    public void drawScreen(int x2, int y2, float z) {
        this.drawDefaultBackground();
        this.drawBackground(0);
        this.username.drawTextBox();
        FontManager.normal_bold_20.drawCenteredStringWithShadow("Directly Login", (double) this.width / 2, 20.0f, -1);
        FontManager.normal_bold_20.drawCenteredStringWithShadow(this.status, (double) this.width / 2, (double) this.height / 4 + 24 + 38, -1);
        if (this.username.getText().isEmpty() && !this.username.isFocused()) {
            FontManager.normal_bold_20.drawStringWithShadow("Username", (double) this.width / 2 - 96, (double) this.height / 4 + 24 + 72 - 4, -7829368);
        }
        super.drawScreen(x2, y2, z);
    }

    @Override
    public void initGui() {
        int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(1145, this.width / 2 - 100, var3 + 72 + 12 + 48, "Random User Name"));
        this.username = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, var3 + 72 - 12, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);
        if (character == '\t' && this.username.isFocused()) {
            this.username.setFocused(!this.username.isFocused());
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) throws IOException {
        super.mouseClicked(x2, y2, button);
        this.username.mouseClicked(x2, y2, button);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
    }
}

