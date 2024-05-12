package dev.stable.commands.impl;

import dev.stable.commands.Command;
import dev.stable.utils.player.ChatUtil;

public final class VClipCommand extends Command {

    public VClipCommand() {
        super("vclip", "vertical clip", ".vclip [distance]");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            usage();
        } else {
            try {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Double.parseDouble(args[0]), mc.thePlayer.posZ);
                ChatUtil.print("Clipped " + Double.parseDouble(args[0]) + " blocks!");
            } catch (NumberFormatException e) {
                usage();
            }
        }
    }

}
