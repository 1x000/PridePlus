package dev.stable.commands.impl;

import dev.stable.Client;
import dev.stable.commands.Command;
import dev.stable.module.Module;

public class ClearConfigCommand extends Command {

    public ClearConfigCommand(){
        super("clearconfig", "Turns off all enabled modules", ".clearconfig");
    }

    @Override
    public void execute(String[] args) {
        Client.INSTANCE.getModuleCollection().getModules().stream().filter(Module::isEnabled).forEach(Module::toggle);
    }
}
