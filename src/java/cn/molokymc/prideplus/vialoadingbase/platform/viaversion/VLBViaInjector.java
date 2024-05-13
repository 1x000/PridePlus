package cn.molokymc.prideplus.vialoadingbase.platform.viaversion;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Iterator;

public class VLBViaInjector implements ViaInjector {
   public void inject() {
   }

   public void uninject() {
   }

   public String getDecoderName() {
      return "via-decoder";
   }

   public String getEncoderName() {
      return "via-encoder";
   }

   public IntSortedSet getServerProtocolVersions() {
      IntSortedSet versions = new IntLinkedOpenHashSet();
      Iterator var2 = ProtocolVersion.getProtocols().iterator();

      while(var2.hasNext()) {
         ProtocolVersion value = (ProtocolVersion)var2.next();
         if (value.getVersion() >= ProtocolVersion.v1_7_1.getVersion()) {
            versions.add(value.getVersion());
         }
      }

      return versions;
   }

   public int getServerProtocolVersion() {
      return this.getServerProtocolVersions().firstInt();
   }

   public JsonObject getDump() {
      return new JsonObject();
   }
}
