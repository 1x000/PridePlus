package dev.stable.vialoadingbase.model;

import dev.stable.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class ProtocolRange {
   private final ComparableProtocolVersion lowerBound;
   private final ComparableProtocolVersion upperBound;

   public ProtocolRange(ProtocolVersion lowerBound, ProtocolVersion upperBound) {
      this(ViaLoadingBase.fromProtocolVersion(lowerBound), ViaLoadingBase.fromProtocolVersion(upperBound));
   }

   public ProtocolRange(ComparableProtocolVersion lowerBound, ComparableProtocolVersion upperBound) {
      if (lowerBound == null && upperBound == null) {
         throw new RuntimeException("Invalid protocol range");
      } else {
         this.lowerBound = lowerBound;
         this.upperBound = upperBound;
      }
   }

   public static ProtocolRange andNewer(ProtocolVersion version) {
      return new ProtocolRange((ProtocolVersion)null, version);
   }

   public static ProtocolRange singleton(ProtocolVersion version) {
      return new ProtocolRange(version, version);
   }

   public static ProtocolRange andOlder(ProtocolVersion version) {
      return new ProtocolRange(version, (ProtocolVersion)null);
   }

   public boolean contains(ComparableProtocolVersion protocolVersion) {
      if (this.lowerBound != null && protocolVersion.getIndex() < this.lowerBound.getIndex()) {
         return false;
      } else {
         return this.upperBound == null || protocolVersion.getIndex() <= this.upperBound.getIndex();
      }
   }

   public String toString() {
      if (this.lowerBound == null) {
         return this.upperBound.getName() + "+";
      } else if (this.upperBound == null) {
         return this.lowerBound.getName() + "-";
      } else {
         return this.lowerBound == this.upperBound ? this.lowerBound.getName() : this.lowerBound.getName() + " - " + this.upperBound.getName();
      }
   }
}
