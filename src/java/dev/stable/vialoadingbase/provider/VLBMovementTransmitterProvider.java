package dev.stable.vialoadingbase.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;

public class VLBMovementTransmitterProvider extends MovementTransmitterProvider {
   public Object getFlyingPacket() {
      return null;
   }

   public Object getGroundPacket() {
      return null;
   }

   public void sendPlayer(UserConnection userConnection) {
      if (userConnection.getProtocolInfo().getState() == State.PLAY) {
         if (userConnection.getEntityTracker(Protocol1_9To1_8.class).clientEntityId() != -1) {
            MovementTracker movementTracker = (MovementTracker)userConnection.get(MovementTracker.class);
            movementTracker.incrementIdlePacket();

            try {
               PacketWrapper c03 = PacketWrapper.create(ServerboundPackets1_8.PLAYER_MOVEMENT, (UserConnection)userConnection);
               c03.write(Type.BOOLEAN, movementTracker.isGround());
               c03.scheduleSendToServer(Protocol1_9To1_8.class);
            } catch (Throwable var4) {
               var4.printStackTrace();
            }

         }
      }
   }
}
