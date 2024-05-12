package dev.stable.module.impl.render;

import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.ui.notifications.Notification;
import dev.stable.ui.notifications.NotificationManager;
import dev.stable.utils.Utils;
import dev.stable.utils.animations.Animation;
import dev.stable.utils.animations.Direction;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

public class NotificationsMod extends Module {
    private final NumberSetting time = new NumberSetting("Time on Screen", 2, 10, 1, .5);
    public static final ModeSetting mode = new ModeSetting("Mode", "UnStable", "UnStable");
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);

    public NotificationsMod() {
        super("Notifications","通知显示", Category.DISPLAY, "Allows you to customize the client notifications");
        this.addSettings(time, mode, toggleNotifications);
        if (!enabled) this.toggleSilent();
    }

    public void render() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(Utils.mc);

        NotificationManager.setToggleTime(time.getValue().floatValue());

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            switch (mode.getMode()) {
                case "UnStable": {
                    animation.setDuration(250);
                    actualOffset = 10;
                    notificationHeight = 24;
                    notificationWidth = (int) Math.max(Utils.tenacityBoldFont22.getStringWidth(notification.getTitle()), Utils.rubikFont18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * (float) animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * (float) GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.drawDefault(x, y, notificationWidth, notificationHeight, (float) animation.getOutput().floatValue());
                    break;

                }
            }
            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }

    public void renderEffects(boolean glow) {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(Utils.mc);


        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            switch (mode.getMode()) {
                case "UnStable":
                    actualOffset = 10;
                    notificationHeight = 24;
                    notificationWidth = (int) Math.max(Utils.tenacityBoldFont22.getStringWidth(notification.getTitle()), Utils.rubikFont18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.blurDefault(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue(), true);
                    break;
            }
            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }
}

