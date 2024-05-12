package dev.stable.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import static dev.stable.ui.altmanager.helpers.Alt.mc;

public class RotationsUtil {
    public static Vec3 getNearestPointBB(final Vec3 eye, final AxisAlignedBB box) {
        final double[] origin = { eye.xCoord, eye.yCoord, eye.zCoord };
        final double[] destMins = { box.minX, box.minY, box.minZ };
        final double[] destMaxs = { box.maxX, box.maxY, box.maxZ };
        for (int i = 0; i < 3; ++i) {
            if (origin[i] > destMaxs[i]) {
                origin[i] = destMaxs[i];
            }
            else if (origin[i] < destMins[i]) {
                origin[i] = destMins[i];
            }
        }
        return new Vec3(origin[0], origin[1], origin[2]);
    }
    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.thePlayer.posX;
        double deltaY = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double deltaZ = z - mc.thePlayer.posZ;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));

        return new float[] {yaw, pitch};
    }
    public static float getYaw(Vec3 to) {
        float x = (float) (to.xCoord - mc.thePlayer.posX);
        float z = (float) (to.zCoord - mc.thePlayer.posZ);
        float var1 = (float) (StrictMath.atan2(z, x) * 180.0D / StrictMath.PI) - 90.0F;
        float rotationYaw = mc.thePlayer.rotationYaw;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    public static float[] getRotationsToPosition(double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(-Math.atan2(dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, horizontalDistance));

        return new float[] {yaw, pitch};
    }

    public static float[] getRotationsToEntity(EntityLivingBase entity, boolean usePartialTicks) {
        float partialTicks = mc.timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks : entity.posZ;

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(entityX, finalEntityY, entityZ);
    }

    public static float[] getRotationsToEntity(double x, double y, double z, EntityLivingBase entity, boolean usePartialTicks) {
        float partialTicks = mc.timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks : entity.posZ;

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? y + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(x, y + mc.thePlayer.getEyeHeight(), z, entityX, finalEntityY, entityZ);
    }

    public static float[] getRotationsToEntityRandomised(EntityLivingBase entity, boolean usePartialTicks, double randomAmount) {
        float partialTicks = mc.timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX + (Math.random() * randomAmount - randomAmount * 0.5) - entity.lastTickPosX) * partialTicks : entity.posX + (Math.random() * randomAmount - randomAmount * 0.5);
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY + (Math.random() * randomAmount - randomAmount * 0.5) - entity.lastTickPosY) * partialTicks : entity.posY + (Math.random() * randomAmount - randomAmount * 0.9);
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ + (Math.random() * randomAmount - randomAmount * 0.5) - entity.lastTickPosZ) * partialTicks : entity.posZ + (Math.random() * randomAmount - randomAmount * 0.5);

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(entityX, finalEntityY, entityZ);
    }


    public static float getGCD() {
        return (float) (Math.pow(mc.gameSettings.mouseSensitivity * 0.6 + 0.2, 3) * 1.2);
    }

}