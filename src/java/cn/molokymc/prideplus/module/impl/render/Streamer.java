package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.StringSetting;
import cn.molokymc.prideplus.event.impl.player.ChatReceivedEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.utils.server.ServerUtils;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;

public class Streamer extends Module {

    public static final BooleanSetting hideIP = new BooleanSetting("Hide scoreboard IP", true);
    public static final BooleanSetting hideServerId = new BooleanSetting("Hide server ID", true);
    public static final BooleanSetting hideUsername = new BooleanSetting("Hide username", true);
    public static final StringSetting customName = new StringSetting("Custom name", "You");
    public static boolean enabled;

    public Streamer() {
        super("Streamer","朱波模式", Category.RENDER, "features for content creators");
        customName.addParent(hideUsername, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(hideIP, hideServerId, hideUsername, customName);
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent e) {
        if (ServerUtils.isOnHypixel() && hideServerId.isEnabled()) {
            String message = StringUtils.stripControlCodes(e.message.getUnformattedText());
            if (message.startsWith("Sending you to")) {
                String serverID = message.replace("Sending you to ", "").replace("!", "");
                e.message = new ChatComponentText("§aSending you to §k" + serverID + "§r§a!");
            }
        }
    }

    public static String filter(String text) {
        if (enabled) {
            if (hideUsername.isEnabled() && mc.getSession() != null) {
                String name = mc.getSession().getUsername();
                if (name != null && !name.trim().isEmpty() && !name.equals("Player") && text.contains(name)) {
                    text = text.replace(name, customName.getString().replace('&', '§'));
                    String text2 = StringUtils.stripControlCodes(text);
                    if (text2.contains("You has ")) {
                        text = text.replace(" has", " have");
                    }
                    if (text2.contains("You was ")) {
                        text = text.replace("was ", "were ");
                    }
                    if (text2.contains("You's ")) {
                        text = text.replace("'s ", "'re ");
                    }
                }
            }
            if (mc.theWorld != null) {
                if (hideIP.isEnabled() && ServerUtils.isOnHypixel() && text.startsWith("§ewww.")) {
                    text = StringUtils.stripControlCodes(text)
                            .replaceAll("[^A-Za-z0-9 .]", "")
                            .replace("§eStable.today", "§eStable.today");
                }
                if (hideServerId.isEnabled() && text.startsWith("§7") && text.contains("/") && text.contains("  §8")) {
                    text = text.replace("§8", "§8§k");
                }
            }
        }
        return text;
    }

    @Override
    public void onEnable() {
        enabled = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        enabled = false;
        super.onDisable();
    }

}