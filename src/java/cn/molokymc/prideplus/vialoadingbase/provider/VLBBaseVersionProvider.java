package cn.molokymc.prideplus.vialoadingbase.provider;

import cn.molokymc.prideplus.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;

public class VLBBaseVersionProvider extends BaseVersionProvider {
   public int getClosestServerProtocol(UserConnection connection) throws Exception {
      return connection.isClientSide() ? ViaLoadingBase.getInstance().getTargetVersion().getVersion() : super.getClosestServerProtocol(connection);
   }
}
