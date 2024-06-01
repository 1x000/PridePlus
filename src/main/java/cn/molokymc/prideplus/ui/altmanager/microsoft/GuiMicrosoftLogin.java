package cn.molokymc.prideplus.ui.altmanager.microsoft;

import java.io.Closeable;
import java.io.IOException;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;

public final class GuiMicrosoftLogin
extends GuiScreen {
    private volatile MicrosoftLogin microsoftLogin;
    private volatile boolean closed = false;
    private final GuiScreen parentScreen;

    public GuiMicrosoftLogin(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        Thread thread = new Thread("MicrosoftLogin Thread"){

            @Override
            public void run() {
                try {
                    GuiMicrosoftLogin.this.microsoftLogin = new MicrosoftLogin();
                    while (!GuiMicrosoftLogin.this.closed) {
                        if (!GuiMicrosoftLogin.this.microsoftLogin.logged) continue;
                        IOUtils.closeQuietly(GuiMicrosoftLogin.this.microsoftLogin);
                        GuiMicrosoftLogin.this.closed = true;
                        GuiMicrosoftLogin.this.microsoftLogin.setStatus("Login successful! " + GuiMicrosoftLogin.this.microsoftLogin.getUserName());
                        GuiMicrosoftLogin.this.mc.session = new Session(GuiMicrosoftLogin.this.microsoftLogin.getUserName(), GuiMicrosoftLogin.this.microsoftLogin.getUuid(), GuiMicrosoftLogin.this.microsoftLogin.getAccessToken(), "mojang");
                        break;
                    }
                }
                catch (Throwable e) {
                    GuiMicrosoftLogin.this.closed = true;
                    e.printStackTrace();
                    IOUtils.closeQuietly(GuiMicrosoftLogin.this.microsoftLogin);
                    GuiMicrosoftLogin.this.microsoftLogin.setStatus("Login failed! " + e.getClass().getName() + ":" + e.getMessage());
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0) {
            if (this.microsoftLogin != null && !this.closed) {
                this.microsoftLogin.close();
                this.closed = true;
            }
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 50, "Back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.microsoftLogin == null) {
            FontManager.normal_bold_20.drawCenteredStringWithShadow(EnumChatFormatting.YELLOW + "Logging in...", (float)this.width / 2.0f, (float)this.height / 2.0f - 5.0f, -1);
        } else {
            FontManager.normal_bold_20.drawCenteredStringWithShadow(this.microsoftLogin.getStatus(), (float)this.width / 2.0f, (float)this.height / 2.0f - 5.0f, -1);
        }
    }
}

