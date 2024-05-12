package dev.stable.module.impl.misc;

import dev.stable.Client;
import dev.stable.event.impl.player.ChatReceivedEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.api.events.world.EventPacketReceive;
import dev.stable.module.impl.combat.KillAura;
import dev.stable.module.impl.exploit.Disabler;
import dev.stable.module.impl.player.Manager;
import dev.stable.module.impl.player.Stealer;
import dev.stable.module.impl.render.HUDMod;
import dev.stable.module.settings.ParentAttribute;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.module.settings.impl.StringSetting;
import dev.stable.ui.notifications.NotificationManager;
import dev.stable.ui.notifications.NotificationType;
import dev.stable.utils.Utils;
import dev.stable.utils.client.MathUtil;
import dev.stable.utils.misc.Multithreading;
import dev.stable.utils.misc.SoundUtils;
import dev.stable.utils.player.ChatUtil;
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

    public void onPacketReceiveEvent(final EventPacketReceive event) {
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
