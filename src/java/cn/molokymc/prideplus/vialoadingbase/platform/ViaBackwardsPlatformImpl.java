package cn.molokymc.prideplus.vialoadingbase.platform;

import cn.molokymc.prideplus.vialoadingbase.ViaLoadingBase;
import com.viaversion.viabackwards.api.ViaBackwardsPlatform;

import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatformImpl implements ViaBackwardsPlatform {
   private final File directory;

   public ViaBackwardsPlatformImpl(File directory) {
      this.init(this.directory = directory);
   }

   public Logger getLogger() {
      return ViaLoadingBase.LOGGER;
   }

   public boolean isOutdated() {
      return false;
   }

   public void disable() {
   }

   public File getDataFolder() {
      return this.directory;
   }
}
