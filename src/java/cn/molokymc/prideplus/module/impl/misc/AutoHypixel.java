package cn.molokymc.prideplus.module.impl.misc;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.event.impl.player.ChatReceivedEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.combat.KillAura;
import cn.molokymc.prideplus.module.impl.exploit.Disabler;
import cn.molokymc.prideplus.module.impl.player.Manager;
import cn.molokymc.prideplus.module.impl.player.Stealer;
import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.module.settings.impl.StringSetting;
import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.client.MathUtil;
import cn.molokymc.prideplus.utils.misc.Multithreading;
import cn.molokymc.prideplus.utils.misc.SoundUtils;
import cn.molokymc.prideplus.utils.player.ChatUtil;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoHypixel extends Module {

    private final BooleanSetting autoGG = new BooleanSetting("AutoGG", true);
    private final StringSetting autoGGMessage = new StringSetting("AutoGG Message", "gg");
    private final BooleanSetting autoPlay = new BooleanSetting("AutoPlay", true);
    private final NumberSetting autoPlayDelay = new NumberSetting("AutoPlay Delay", 2.5, 8, 1, 0.5);
    private final BooleanSetting autoHubOnBan = new BooleanSetting("Auto /l on ban", false);
    private final BooleanSetting music = new BooleanSetting("GameOverMusic", false);

    public AutoHypixel() {
        super("AutoPlay", "自动游玩",Category.MISC, "stuff for hypixel");
        autoGGMessage.addParent(autoGG, ParentAttribute.BOOLEAN_CONDITION);
        autoPlayDelay.addParent(autoPlay, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(autoGG, autoGGMessage, autoPlay, autoPlayDelay, autoHubOnBan,music);
    }

    @Override
    public void onPacketReceiveEvent(final PacketReceiveEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packet2 = (S02PacketChat)event.getPacket();
            final String text = packet2.getChatComponent().getUnformattedText();
            Pattern pattern = Pattern.compile("A player has been removed from your game.");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                ChatUtil.send("/lobby");
                NotificationManager.post(NotificationType.WARNING, "", "A player was banned.", 5.0f);
            }
            pattern = Pattern.compile("FIGHT!");
            matcher = pattern.matcher(text);
            if (matcher.find()) {
                NotificationManager.post(NotificationType.INFO, "", "FK Watchdog Bypasses Done!");
            }
            if (text.contains("VICTORY!")) {
                NotificationManager.post(NotificationType.WARNING, "", "You're the winner!", 2.0f);
            }
        }
    }
    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        String message = event.message.getUnformattedText(), strippedMessage = StringUtils.stripControlCodes(message);
        String m = event.message.toString();
        if (m.contains("ClickEvent{action=RUN_COMMAND, value='/play ")) {
            if (autoGG.isEnabled() && !strippedMessage.startsWith("You died!")) {
                ChatUtil.send("/ac " + autoGGMessage.getString());
            }
            if (autoPlay.isEnabled()) {
                sendToGame(m.split("action=RUN_COMMAND, value='")[1].split("'}")[0]);
            }
        }
    }

    private void sendToGame(String mode) {
        Stealer.chests = 0;
        Manager.test = 0;
        Disabler.count = 0;
        float delay = autoPlayDelay.getValue().floatValue();
        NotificationManager.post(NotificationType.SUCCESS, "",
                "Play again in" + (delay > 0 ? " in " + delay + "s" : "") + "!", delay);
        NotificationManager.post(NotificationType.SUCCESS, "",
                "Your Health: " + MathUtil.DF_1.format(Utils.mc.thePlayer.getHealth()), 5.0f);
        if(music.isEnabled()){
            NotificationManager.post(NotificationType.INFO, "", "Now playing the music \"JiaYouYa\"");
            SoundUtils.playSound(new ResourceLocation("Pride/sounds/victory.wav"), 1.2f);
        }
        //NotificationManager.post(NotificationType.INFO, "", "Respawn Check");
        (Client.INSTANCE.getModuleCollection().getModule((Class)Manager.class)).setToggled(false);
        (Client.INSTANCE.getModuleCollection().getModule((Class)Stealer.class)).setToggled(false);
        (Client.INSTANCE.getModuleCollection().getModule((Class)KillAura.class)).setToggled(false);
        Multithreading.schedule(() -> ChatUtil.send(mode), (long) delay, TimeUnit.SECONDS);
    }

}
