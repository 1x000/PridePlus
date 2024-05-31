package cn.molokymc.prideplus.commands.impl;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.commands.Command;
import cn.molokymc.prideplus.module.Module;

public class ClearConfigCommand extends Command {

    public ClearConfigCommand(){
        super("clearconfig", "Turns off all enabled modules", ".clearconfig");
    }

    @Override
    public void execute(String[] args) {
        Pride.INSTANCE.getModuleCollection().getModules().stream().filter(Module::isEnabled).forEach(Module::toggle);
    }
}
