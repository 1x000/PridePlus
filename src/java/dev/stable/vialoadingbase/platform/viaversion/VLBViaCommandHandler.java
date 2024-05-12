package dev.stable.vialoadingbase.platform.viaversion;

import dev.stable.vialoadingbase.command.impl.LeakDetectSubCommand;
import com.viaversion.viaversion.commands.ViaCommandHandler;

public class VLBViaCommandHandler extends ViaCommandHandler {
   public VLBViaCommandHandler() {
      this.registerVLBDefaults();
   }

   public void registerVLBDefaults() {
      this.registerSubCommand(new LeakDetectSubCommand());
   }
}
