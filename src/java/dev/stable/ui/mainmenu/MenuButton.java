package dev.stable.ui.mainmenu;

import dev.stable.ui.Screen;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.Direction;
import dev.stable.utils.animations.impl.DecelerateAnimation;
import dev.stable.utils.misc.HoveringUtil;
import dev.stable.utils.render.GLUtil;
import dev.stable.utils.render.RenderUtil;
import dev.stable.utils.render.RoundedUtil;
import dev.stable.utils.render.StencilUtil;
import dev.stable.utils.skidfont.FontManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MenuButton implements Screen {

    public final String text;
    private Animation hoverAnimation;
    public float x, y, width, height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }


    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(200, 1);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

//    private static final ResourceLocation rs = new ResourceLocation("stable/MainMenu/menu-rect.png");

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRoundOutline(x, y + -3, width, height + 6, (float)3.5, 0.0015f,  new Color(0, 0, 0,  (int) (20)) , new Color(0, 0, 0,  (int) (20)));
        //RoundedUtil.drawRound(x, y + -3, width, height + 6, (float)3.5, true, new Color(0,0,0,32));
        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        float percent = (float) hoverAnimation.getOut();
        percent = Math.min(1F, percent);
        RoundedUtil.drawRoundOutline(x, y + -3, width, height + 6, (float)3.5, 0.0015f,  new Color(0, 0, 0,  (int) (50 * percent)) , new Color(200, 200, 200,  (int) (60 * percent)));

        rubikFont20.drawCenteredString(text, x + width / 2f, y + rubikFont20.getMiddleOfBox(height), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if(hovered) {
            clickAction.run();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
