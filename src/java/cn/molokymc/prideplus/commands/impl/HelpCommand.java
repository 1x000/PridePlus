package cn.molokymc.prideplus.commands.impl;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.commands.Command;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.apache.commons.lang3.StringUtils;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Displays information about the commands", ".help");
    }

    @Override
    public void execute(String[] args) {
        ChatComponentText response = new ChatComponentText("\n§l§dStable §r§d" + Client.INSTANCE.getVersion()  + " §7- §r§6Hover to see command usages.");
        ChatComponentText temp = new ChatComponentText("");
        float maxLength = 0;
        for (Command cmd : Client.INSTANCE.getCommandHandler().getCommands()) {
            String info = String.format("\n§r§b.%s • \2477%s", cmd.getName().toLowerCase(), cmd.getDescription());
            temp.appendSibling(
                    new ChatComponentText(info)
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(String.format("\2477Usage: \247e%s", cmd.getUsage())))))
            );
            if (mc.fontRendererObj.getStringWidth(info) > maxLength)
                maxLength = mc.fontRendererObj.getStringWidth(info);

        }
        response.appendSibling(new ChatComponentText(String.format("\n§7§m%s\247r", StringUtils.repeat(" ", (int) (maxLength / 4)))))
                .appendSibling(temp);
        mc.thePlayer.addChatMessage(response);
    }

}

