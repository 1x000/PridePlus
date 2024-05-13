package cn.molokymc.prideplus.viamcp.fixes;

import cn.molokymc.prideplus.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class AttackOrder {
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static void sendConditionalSwing(MovingObjectPosition mop) {
      if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
         mc.thePlayer.swingItem();
      }

   }

   public static void sendFixedAttack(EntityPlayer entityIn, Entity target) {
      if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
         mc.thePlayer.swingItem();
         mc.playerController.attackEntity(entityIn, target);
      } else {
         mc.playerController.attackEntity(entityIn, target);
         mc.thePlayer.swingItem();
      }

   }
}
