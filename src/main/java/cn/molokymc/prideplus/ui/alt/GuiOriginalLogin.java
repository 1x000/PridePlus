package cn.molokymc.prideplus.ui.alt;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.ui.alt.altimpl.OriginalAlt;
import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.UUID;

public class GuiOriginalLogin extends GuiScreen {
    private final GuiScreen parentScreen;
    private final boolean direct;
    private GuiTextField userName;
    private GuiTextField accessToken;
    private GuiTextField uuid;
    private GuiButton legacyButton;
    private GuiButton mojangButton;

    private String selectedType = "mojang";
    private String status = EnumChatFormatting.YELLOW + "等待中...";

    public GuiOriginalLogin(GuiScreen parentScreen, boolean direct) {
        this.parentScreen = parentScreen;
        this.direct = direct;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        float x = width / 2f;
        float y = height / 2f;

        mc.fontRendererObj.drawCenteredStringWithShadow(status, x, y - 50f, -1);

        userName.drawTextBox();
        accessToken.drawTextBox();
        uuid.drawTextBox();

        if (!userName.isFocused() && (userName.getText() == null || userName.getText().isEmpty())) {
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + "UserName", x - 77f, y - 30f, -1);
        }

        if (!accessToken.isFocused() && (accessToken.getText() == null || accessToken.getText().isEmpty())) {
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + "AccessToken", x - 77f, y - 5f, -1);
        }

        if (!uuid.isFocused() && (uuid.getText() == null || uuid.getText().isEmpty())) {
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + "UUID", x - 77f, y + 20f, -1);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        userName.textboxKeyTyped(typedChar, keyCode);
        accessToken.textboxKeyTyped(typedChar, keyCode);
        uuid.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        userName.mouseClicked(mouseX, mouseY, mouseButton);
        accessToken.mouseClicked(mouseX, mouseY, mouseButton);
        uuid.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == null) return;

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(parentScreen);
                break;
            case 1:
                if (direct) {
                    mc.session = new Session(userName.getText(), uuid.getText(), accessToken.getText(), selectedType);
                    status = EnumChatFormatting.GREEN + "登录成功! Type:" + selectedType + " UserName:" + userName.getText() + " AccessToken:" + accessToken.getText() + " UUID:" + uuid.getText();
                } else {
                    AltManager.Instance.addAlt(new OriginalAlt(userName.getText(), accessToken.getText(), uuid.getText(), selectedType));
                    status = EnumChatFormatting.GREEN + "添加成功! Type:" + selectedType + " UserName:" + userName.getText() + " AccessToken:" + accessToken.getText() + " UUID:" + uuid.getText();
                }
                break;
            case 2:
                selectedType = "legacy";
                legacyButton.enabled = false;
                mojangButton.enabled = true;
                break;
            case 3:
                selectedType = "mojang";
                legacyButton.enabled = true;
                mojangButton.enabled = false;
                break;
            case 4:
                uuid.setText(UUID.randomUUID().toString().replace("-", ""));
                break;
            case 5:
                NotificationManager.post(NotificationType.INFO, "OriginalLogin", "你的用户名为:" + mc.session.getUsername(), 15000);
                NotificationManager.post(NotificationType.INFO,"OriginalLogin", "你的AccessToken为:" + mc.session.getToken(), 15000);
                NotificationManager.post(NotificationType.INFO, "OriginalLogin", "你的UUID为:" + mc.session.getPlayerID(), 15000);

                Pride.LOGGER.info("OriginalLogin: UserName:{}", mc.session.getUsername());
                Pride.LOGGER.info("OriginalLogin: Token:{}", mc.session.getToken());
                Pride.LOGGER.info("OriginalLogin: UUID:{}", mc.session.getPlayerID());
                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = width / 2;
        int y = height / 2;

        userName = new GuiTextField(0, mc.fontRendererObj, x - 80, y - 35, 160, 20);
        userName.setMaxStringLength(500);

        accessToken = new GuiTextField(1, mc.fontRendererObj, x - 80, y - 10, 160, 20);
        accessToken.setMaxStringLength(500);

        uuid = new GuiTextField(2, mc.fontRendererObj, x - 80, y + 15, 160, 20);
        uuid.setMaxStringLength(500);

        buttonList.add(new GuiButton(0, x + 40, y + 45, 40, 20, "返回"));
        buttonList.add(new GuiButton(1, x - 80, y + 45, 40, 20, "登录"));

        legacyButton = new GuiButton(2, x - 125, y - 35, 40, 20, "Legacy");
        buttonList.add(legacyButton);

        mojangButton = new GuiButton(3, x - 125, y - 10, 40, 20, "Mojang");
        mojangButton.enabled = false;
        buttonList.add(mojangButton);

        buttonList.add(new GuiButton(4, x - 30, y + 45, 50, 20, "随机UUID"));

        buttonList.add(new GuiButton(5, x - 80, y + 70, 160, 20, "获得当前信息"));
    }
}


