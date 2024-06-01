package cn.molokymc.prideplus.viamcp.versionfix;

import cn.molokymc.prideplus.viamcp.ViaMCP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class VersionFixer {
    public static AxisAlignedBB getCollisionBoundingBox_BlockFarmland(BlockPos blockPos) {
        double f = ViaMCP.getInstance().getVersion() <= 47 ? 1.0 : 0.9375;
        return new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, (double)blockPos.getY() + f, blockPos.getZ() + 1);
    }

    public static float setBlockBoundsBasedOnState_BlockLadder(float value) {
        return ViaMCP.getInstance().getVersion() <= 47 ? value : 0.1875f;
    }

    public static AxisAlignedBB getCollisionBoundingBox_BlockLilyPad(BlockPos pos, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (ViaMCP.getInstance().getVersion() <= 47) {
            return new AxisAlignedBB((double)pos.getX() + minX, (double)pos.getY() + minY, (double)pos.getZ() + minZ, (double)pos.getX() + maxX, (double)pos.getY() + maxY, (double)pos.getZ() + maxZ);
        }
        return new AxisAlignedBB((double)pos.getX() + 0.0625, pos.getY(), (double)pos.getZ() + 0.0625, (double)pos.getX() + 0.9375, (double)pos.getY() + 0.09375, (double)pos.getZ() + 0.9375);
    }

    public static float c08PacketFix(float value) {
        if (ViaMCP.getInstance().getVersion() <= 47) {
            return 1.0F;
        } else {
            return value;
        }
    }

    public static float entityBorderFix(float value) {
        if (ViaMCP.getInstance().getVersion() <= 47) {
            return 0.0F;
        } else {
            return value;
        }
    }

    public static double movementFix(double value) {
        if (ViaMCP.getInstance().getVersion() <= 47) {
            return 0.003D;
        } else {
            return value;
        }
    }
}
