package cn.molokymc.prideplus.commands.impl;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.commands.Command;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("scriptreload", "Reloads all scripts", ".scriptreload");
    }

    @Override
    public void execute(String[] args) {
        Client.INSTANCE.getScriptManager().reloadScripts();
    }

}
