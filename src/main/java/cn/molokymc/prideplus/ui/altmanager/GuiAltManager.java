package cn.molokymc.prideplus.ui.altmanager;

import cn.molokymc.prideplus.ui.altmanager.altimpl.MicrosoftAlt;
import cn.molokymc.prideplus.ui.altmanager.microsoft.GuiMicrosoftLogin;
import cn.molokymc.prideplus.ui.altmanager.microsoft.MicrosoftLogin;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import com.mojang.authlib.exceptions.AuthenticationException;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class GuiAltManager
extends GuiScreen {
    private final GuiScreen parentScreen;
    private GuiButton buttonLogin;
    private GuiButton buttonRemove;
    private volatile String status = (Object)((Object)EnumChatFormatting.YELLOW) + "Pending...";
    private volatile MicrosoftLogin microsoftLogin;
    private volatile Thread runningThread;
    private static Alt selectAlt;

    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            if (this.microsoftLogin != null) {
                this.status = this.microsoftLogin.getStatus();
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        this.drawDefaultBackground();
        this.drawBackground(0);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        FontManager.normal_bold_20.drawCenteredStringWithShadow((Object)((Object)EnumChatFormatting.YELLOW) + "Current user name: " + this.mc.session.getUsername(), (float)this.width / 2.0f, (float)this.height / 2.0f - 10.0f, -1);
        FontManager.normal_bold_20.drawCenteredStringWithShadow(this.status, (float)this.width / 2.0f, (float)this.height / 2.0f, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (this.runningThread != null) {
                this.runningThread.interrupt();
            }
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 2) {
            if (selectAlt != null) {
                Thread thread = getThread();
                this.setRunningThread(thread);
            }
        } else if (button.id == 3) {
            if (selectAlt != null) {
                AltManager.Instance.getAltList().remove(selectAlt);
                selectAlt = null;
            }
        } else if (button.id == 4) {
            this.mc.displayGuiScreen(new GuiAltLogin(this){

                @Override
                public void onLogin(final String account, final String password) {
                    Thread thread = new Thread(){

                        @Override
                        public void run() {
                            try {
                                status = EnumChatFormatting.YELLOW + "Logging in...";
                                AltManager.LoginStatus loginStatus = AltManager.loginAlt(account, password);
                                switch (loginStatus) {
                                    case FAILED: {
                                        status = EnumChatFormatting.RED + "Login failed!";
                                        break;
                                    }
                                    case SUCCESS: {
                                        status = EnumChatFormatting.GREEN + "Login successful! " + mc.session.getUsername();
                                    }
                                }
                            }
                            catch (AuthenticationException e) {
                                e.printStackTrace();
                                status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                            }
                            this.interrupt();
                        }
                    };
                    thread.setDaemon(true);
                    thread.start();
                    GuiAltManager.this.setRunningThread(thread);
                }
            });
        } else if (button.id == 5) {
            this.mc.displayGuiScreen(new GuiMicrosoftLogin(this));
        }
        super.actionPerformed(button);
    }

    private Thread getThread() {
        Thread thread = new Thread(() -> {
            this.status = EnumChatFormatting.YELLOW + "Logging in...";
            switch (selectAlt.getAccountType()) {
                case OFFLINE: {
                    Minecraft.getMinecraft().session = new Session(selectAlt.getUserName(), "", "", "mojang");
                    this.status = EnumChatFormatting.GREEN + "Login successful! " + this.mc.session.getUsername();
                    break;
                }
                case MICROSOFT: {
                    try {
                        this.microsoftLogin = new MicrosoftLogin(((MicrosoftAlt)selectAlt).getRefreshToken());
                        while (Minecraft.getMinecraft().running) {
                            if (!this.microsoftLogin.logged) continue;
                            System.out.print("");
                            this.mc.session = new Session(this.microsoftLogin.getUserName(), this.microsoftLogin.getUuid(), this.microsoftLogin.getAccessToken(), "mojang");
                            this.status = EnumChatFormatting.GREEN + "Login successful! " + this.mc.session.getUsername();
                            break;
                        }
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                        this.status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                    }
                    this.microsoftLogin = null;
                }
            }
        }, "AltManager Login Thread");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(4, this.width / 2 - 120, this.height - 48, 70, 20, "Directly Login"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 40, this.height - 48, 70, 20, "Microsoft"));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 40, this.height - 48, 70, 20, "Back"));
        this.buttonLogin = new GuiButton(2, -1145141919, -1145141919, 70, 20, "Login");
        this.buttonList.add(this.buttonLogin);
        this.buttonRemove = new GuiButton(3, -1145141919, -1145141919, 70, 20, "Delete");
        this.buttonList.add(this.buttonRemove);
        super.initGui();
    }

    public void setRunningThread(Thread runningThread) {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
        }
        this.runningThread = runningThread;
    }
}

