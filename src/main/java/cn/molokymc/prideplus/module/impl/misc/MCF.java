package cn.molokymc.prideplus.module.impl.misc;

import cn.molokymc.prideplus.commands.impl.FriendCommand;
import cn.molokymc.prideplus.event.impl.game.TickEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import cn.molokymc.prideplus.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;




public class MCF extends Module {

    private boolean wasDown;

    public MCF() {
        super("MCF","中键加好友", Category.MISC, "middle click friends");
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (Utils.mc.inGameHasFocus) {
            boolean down = Utils.mc.gameSettings.keyBindPickBlock.isKeyDown();
            if (down && !wasDown) {
                if (Utils.mc.objectMouseOver != null && Utils.mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) Utils.mc.objectMouseOver.entityHit;
                    String name = StringUtils.stripControlCodes(player.getName());
                    if (FriendCommand.isFriend(name)) {
                        FriendCommand.friends.removeIf(f -> f.equalsIgnoreCase(name));
                        NotificationManager.post(NotificationType.SUCCESS, "Friend Manager", "You are no longer friends with " + name + "!", 2);
                    } else {
                        FriendCommand.friends.add(name);
                        NotificationManager.post(NotificationType.SUCCESS, "Friend Manager", "You are now friends with " + name + "!", 2);
                    }
                    FriendCommand.save();
                    wasDown = true;
                }
            } else if (!down) {
                wasDown = false;
            }
        }
    }

}
