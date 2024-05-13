package cn.molokymc.prideplus.vialoadingbase.model;

import cn.molokymc.prideplus.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class ComparableProtocolVersion extends ProtocolVersion {
   private final int index;

   public ComparableProtocolVersion(int version, String name, int index) {
      super(version, name);
      this.index = index;
   }

   public boolean isOlderThan(ProtocolVersion other) {
      return this.getIndex() > ViaLoadingBase.fromProtocolVersion(other).getIndex();
   }

   public boolean isOlderThanOrEqualTo(ProtocolVersion other) {
      return this.getIndex() >= ViaLoadingBase.fromProtocolVersion(other).getIndex();
   }

   public boolean isNewerThan(ProtocolVersion other) {
      return this.getIndex() < ViaLoadingBase.fromProtocolVersion(other).getIndex();
   }

   public boolean isNewerThanOrEqualTo(ProtocolVersion other) {
      return this.getIndex() <= ViaLoadingBase.fromProtocolVersion(other).getIndex();
   }

   public boolean isEqualTo(ProtocolVersion other) {
      return this.getIndex() == ViaLoadingBase.fromProtocolVersion(other).getIndex();
   }

   public int getIndex() {
      return this.index;
   }
}
