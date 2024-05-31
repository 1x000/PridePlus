package cn.molokymc.prideplus.viamcp.versionfix;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import cn.molokymc.prideplus.viamcp.common.ViaMCPCommon;

public class VersionFixer {
    public static AxisAlignedBB getCollisionBoundingBox_BlockFarmland(BlockPos blockPos) {
        double f = ViaMCPCommon.getManager().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) ? 1.0 : 0.9375;
        return new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, (double)blockPos.getY() + f, blockPos.getZ() + 1);
    }

    public static float setBlockBoundsBasedOnState_BlockLadder(float value) {
        return ViaMCPCommon.getManager().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) ? value : 0.1875f;
    }

    public static AxisAlignedBB getCollisionBoundingBox_BlockLilyPad(BlockPos pos, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (ViaMCPCommon.getManager().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return new AxisAlignedBB((double)pos.getX() + minX, (double)pos.getY() + minY, (double)pos.getZ() + minZ, (double)pos.getX() + maxX, (double)pos.getY() + maxY, (double)pos.getZ() + maxZ);
        }
        return new AxisAlignedBB((double)pos.getX() + 0.0625, pos.getY(), (double)pos.getZ() + 0.0625, (double)pos.getX() + 0.9375, (double)pos.getY() + 0.09375, (double)pos.getZ() + 0.9375);
    }

    public static float c08PacketFix(float value) {
        if (ViaMCPCommon.getManager().getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
            return 1.0F;
        } else {
            return value;
        }
    }

    public static float entityBorderFix(float value) {
        if (ViaMCPCommon.getManager().getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
            return 0.0F;
        } else {
            return value;
        }
    }

    public static double movementFix(double value) {
        if (ViaMCPCommon.getManager().getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
            return 0.003D;
        } else {
            return value;
        }
    }
}
