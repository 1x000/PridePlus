package cn.molokymc.prideplus.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import cn.molokymc.prideplus.vialoadingbase.ViaLoadingBase;
import cn.molokymc.prideplus.viamcp.gui.AsyncVersionSlider;
import java.io.File;

public class ViaMCP {
   public static final int NATIVE_VERSION = 47;
   public static ViaMCP INSTANCE;
   private static AsyncVersionSlider asyncVersionSlider;

   public static void create() {
      INSTANCE = new ViaMCP();
   }

   public ViaMCP() {
      ViaLoadingBase.ViaLoadingBaseBuilder.create().runDirectory(new File("ViaMCP")).nativeVersion(ProtocolVersion.v1_12_2.getVersion()).onProtocolReload((comparableProtocolVersion) -> {
         if (this.getAsyncVersionSlider() != null) {
            this.getAsyncVersionSlider().setVersion(comparableProtocolVersion.getVersion());
         }

      }).build();
   }

   public void initAsyncSlider() {
      this.initAsyncSlider(5, 5, 110, 20);
   }

   public static void initAsyncSlider(int x, int y, int width, int height) {
      asyncVersionSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height);
   }

   public AsyncVersionSlider getAsyncVersionSlider() {
      return this.asyncVersionSlider;
   }
}
