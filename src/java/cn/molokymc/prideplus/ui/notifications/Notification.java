package cn.molokymc.prideplus.ui.notifications;

import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.impl.EaseOutSine;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import cn.molokymc.prideplus.utils.tuples.Pair;
import lombok.Getter;

import java.awt.*;


@Getter
public class Notification implements Utils {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;

        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new EaseOutSine(250, 1);
    }

    public void drawDefault(float x, float y, float width, float height, float alpha) {
        RoundedUtil.drawRound(x, y, width, height, 2, new Color(0, 0, 0, 100));
        tenacityBoldFont14.drawString(getTitle(), x + 11, y + 2f, new Color(255, 255, 255, 255));
        FontManager.edit17.drawString(getDescription(), x + 11, y + -10f + FontManager.rubik17.getHeight(), new Color(255, 255, 255, 255));
        RoundedUtil.drawRound(x + 2f, y + 10f, 2, 4, 0.5f,  notificationType.getColor());
        RoundedUtil.drawGradientHorizontal(x, y, ((getTime() - getTimerUtil().getTime()) / getTime()) * width, height, 2,new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
    }

    public void blurDefault(float x, float y, float width, float height, float alpha, boolean glow) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), glow ? .65f : 0), alpha);
        Pair<Color, Color> colors = HUDMod.getClientColors();
        RoundedUtil.drawRound(x, y + height - 1, ((getTime() - getTimerUtil().getTime()) / getTime()) * width, 1, 1, new Color(6, 160, 155));
        RoundedUtil.drawRound(x, y, width, height, 2, Color.BLACK);
        RoundedUtil.drawGradientHorizontal(x, y, ((getTime() - getTimerUtil().getTime()) / getTime()) * width, height, 2,new Color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue(),35),new Color(HUDMod.getClientColors().getSecond().getRed(),HUDMod.getClientColors().getSecond().getGreen(),HUDMod.getClientColors().getSecond().getBlue(),35));
    }
}
