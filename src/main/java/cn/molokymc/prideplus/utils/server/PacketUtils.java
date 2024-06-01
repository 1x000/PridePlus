package cn.molokymc.prideplus.utils.server;

import cn.molokymc.prideplus.module.impl.exploit.Disabler;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.client.MathUtil;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.exception.CancelException;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

public class PacketUtils implements Utils {

    public static void sendPacket(Packet<?> packet, boolean silent) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(packet, silent);
        }
    }
    public static void sendPacketNoEvent(Packet packet) {
        sendPacket(packet, true);
    }

    public static void sendPacket(Packet packet) {
        sendPacket(packet, false);
    }

    public static boolean isPacketValid(final Packet packet) {
        return !(packet instanceof C00PacketLoginStart) && !(packet instanceof C00Handshake) && !(packet instanceof C00PacketServerQuery) && !(packet instanceof C01PacketPing);
    }

    public static void sendPacketC0F() {
        if (!Disabler.getGrimPost()) {
            sendPacket(new C0FPacketConfirmTransaction(MathUtil.getRandom(102, 1000024123), (short) MathUtil.getRandom(102, 1000024123), true));
        }

    }

    public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) {
        try {
            if (currentThread) {
                packet.sendToServer(packetProtocol, skipCurrentPipeline);
            } else {
                packet.scheduleSendToServer(packetProtocol, skipCurrentPipeline);
            }
        } catch (CancelException var5) {
        } catch (Exception var6) {
            Exception ex = var6;
            ex.printStackTrace();
        }

    }
}
