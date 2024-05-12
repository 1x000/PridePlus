package dev.stable.commands.impl;

import dev.stable.Client;
import dev.stable.commands.Command;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("scriptreload", "Reloads all scripts", ".scriptreload");
    }

    @Override
    public void execute(String[] args) {
        Client.INSTANCE.getScriptManager().reloadScripts();
    }

}
