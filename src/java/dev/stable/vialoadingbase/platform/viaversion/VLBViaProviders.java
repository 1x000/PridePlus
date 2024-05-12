package dev.stable.vialoadingbase.platform.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import dev.stable.vialoadingbase.ViaLoadingBase;
import dev.stable.vialoadingbase.provider.VLBBaseVersionProvider;
import dev.stable.vialoadingbase.provider.VLBMovementTransmitterProvider;

public class VLBViaProviders implements ViaPlatformLoader {
   public void load() {
      ViaProviders providers = Via.getManager().getProviders();
      providers.use(VersionProvider.class, new VLBBaseVersionProvider());
      providers.use(MovementTransmitterProvider.class, new VLBMovementTransmitterProvider());
      if (ViaLoadingBase.getInstance().getProviders() != null) {
         ViaLoadingBase.getInstance().getProviders().accept(providers);
      }

   }

   public void unload() {
   }
}
