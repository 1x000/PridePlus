package cn.molokymc.prideplus.commands.impl;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.commands.Command;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("scriptreload", "Reloads all scripts", ".scriptreload");
    }

    @Override
    public void execute(String[] args) {
        Pride.INSTANCE.getScriptManager().reloadScripts();
    }

}
