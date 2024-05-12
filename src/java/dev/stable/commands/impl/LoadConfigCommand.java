package dev.stable.commands.impl;

import dev.stable.Client;
import dev.stable.commands.Command;
import dev.stable.config.ConfigManager;
import dev.stable.config.LocalConfig;
import dev.stable.utils.misc.FileUtils;
import dev.stable.utils.player.ChatUtil;

public class LoadConfigCommand extends Command {
    public LoadConfigCommand() {
        super("config", "load / save your current config", ".config [load/save] [config name]");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 0) usage();
        if(args[0].equals("load")) {
            ChatUtil.print("Try to Load...");
                for (LocalConfig i : ConfigManager.localConfigs) {
                    if(i.getName().equals(args[1])) {
                        Client.INSTANCE.getConfigManager().loadConfig(FileUtils.readFile(i.getFile()), true);
                        ChatUtil.print("loaded config.");
                    }
                }
        }

        if(args[0].equals("save")) {
            ChatUtil.print("Try to Save...");
            Client.INSTANCE.getConfigManager().saveConfig(args[1]);
        }
    }
}
