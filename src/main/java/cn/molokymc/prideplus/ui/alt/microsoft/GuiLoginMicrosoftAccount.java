package cn.molokymc.prideplus.ui.alt.microsoft;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiLoginMicrosoftAccount extends GuiScreen {
    private final GuiScreen parentScreen;
    private final Thread thread;
    private boolean threadStarted = false;
    private String status = "登录中";

    public GuiLoginMicrosoftAccount(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.thread = new Thread("Microsoft Login Thread") {
            private MicrosoftAuth auth = null;

            @Override
            public void run() {
                try {
                    auth = new MicrosoftAuth(
                            obj -> onLogin(obj),
                            obj -> status = obj
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                    status = e.getClass().getName() + ":" + e.getMessage();
                }
            }

            public void close() {
                if (auth != null) {
                    auth.close();
                }
                interrupt();
            }
        };
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        FontManager.rubik16.drawCenteredStringWithShadow(status, width / 2.0, height / 2.0 - FontManager.rubik16.getHeight(), -1);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ((IThread) thread).close();
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 50, "Back"));

        if (!threadStarted) {
            thread.setDaemon(true);
            thread.start();
            threadStarted = true;
        }
    }

    protected abstract void onLogin(MicrosoftAuth.Data data);
}


