package cn.molokymc.prideplus.vialoadingbase.platform.viaversion;

import cn.molokymc.prideplus.vialoadingbase.command.impl.LeakDetectSubCommand;
import com.viaversion.viaversion.commands.ViaCommandHandler;

public class VLBViaCommandHandler extends ViaCommandHandler {
   public VLBViaCommandHandler() {
      this.registerVLBDefaults();
   }

   public void registerVLBDefaults() {
      this.registerSubCommand(new LeakDetectSubCommand());
   }
}
