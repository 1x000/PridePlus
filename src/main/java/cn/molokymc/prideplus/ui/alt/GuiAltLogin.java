package cn.molokymc.prideplus.ui.alt;

import utils.hodgepodge.object.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public abstract class GuiAltLogin extends GuiScreen {
    private final String STRING_RANDOM_SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private GuiTextField password;
    private final GuiScreen previousScreen;
    private GuiTextField username;
    private GuiTextField combined;
    protected volatile String status = EnumChatFormatting.YELLOW + "等待中...";

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected void actionPerformed(GuiButton button) {
        String data;
        switch (button.id) {
            case 0:
                this.onLogin(username.getText(),password.getText());
                break;
            case 1:
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            case 2:
                data = getClipboardString();

                if (data.contains(":")) {
                    String[] credentials = data.split(":");
                    this.username.setText(credentials[0]);
                    this.password.setText(credentials[1]);
                }
                break;
            case 1145:
                this.username.setText(StringUtils.randomString(10, STRING_RANDOM_SEED));
                this.password.setText("");
        }
    }

    public abstract void onLogin(String account,String password);

    public void drawScreen(int x, int y, float z) {
        this.drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.combined.drawTextBox();
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString("Alt Login", (float)(this.width / 2), 20.0F, -1);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString(status, (float) (this.width / 2), 29.0F, -1);
        if (this.username.getText().isEmpty() && !this.username.isFocused()) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Username / E-Mail", (float)(this.width / 2 - 96), 66.0F, -7829368);
        }

        if (this.password.getText().isEmpty() && !this.password.isFocused()) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Password", (float)(this.width / 2 - 96), 106.0F, -7829368);
        }

        if (this.combined.getText().isEmpty() && !this.combined.isFocused()) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Email:Password", (float)(this.width / 2 - 96), 146.0F, -7829368);
        }

        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        final int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(1145, this.width / 2 - 100, var3 + 72 + 12 + 48, "Random User Name"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, var3 + 72 + 12 - 24, "Import user:pass"));
        this.username = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.password = new GuiTextField(11,this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        this.combined = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 140, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
        this.password.setMaxStringLength(200);
        this.combined.setMaxStringLength(200);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);

        if (character == '\t' && (this.username.isFocused() || this.combined.isFocused() || this.password.isFocused())) {
            this.username.setFocused(!this.username.isFocused());
            this.password.setFocused(!this.password.isFocused());
            this.combined.setFocused(!this.combined.isFocused());
        }

        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
        this.combined.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        this.username.mouseClicked(x, y, button);
        this.password.mouseClicked(x, y, button);
        this.combined.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
        this.combined.updateCursorCounter();
    }
}
