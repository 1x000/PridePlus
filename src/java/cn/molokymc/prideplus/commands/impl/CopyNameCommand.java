package cn.molokymc.prideplus.commands.impl;

import cn.molokymc.prideplus.commands.Command;
import cn.molokymc.prideplus.utils.misc.IOUtils;

public class CopyNameCommand extends Command {
    public CopyNameCommand() {
        super("name", "copies your name to the clipboard", ".name");
    }

    @Override
    public void execute(String[] args) {
        IOUtils.copy(mc.thePlayer.getName());
        sendChatWithInfo("Copied your name to the clipboard");
    }
}
