//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package cn.molokymc.prideplus.utils.player;

import cn.molokymc.prideplus.event.impl.player.EventMoveInput;
import cn.molokymc.prideplus.module.api.events.world.EventMove;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import cn.molokymc.prideplus.utils.math.*;

public class MoveUtil
{
    private static final Minecraft mc;
    public static final double WALK_SPEED = 0.221;
    
    public static void setSpeed(final double speed) {
        MoveUtil.mc.thePlayer.motionX = -Math.sin(getDirection()) * speed;
        MoveUtil.mc.thePlayer.motionZ = Math.cos(getDirection()) * speed;
    }
    
    public static int getSpeedAmplifier() {
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return 1 + MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
        }
        return 0;
    }
    
    public static void jump(final EventMove event) {
        double jumpY = MoveUtil.mc.thePlayer.getJumpUpwardsMotion();
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.jump)) {
            jumpY += (MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
        }
        event.setY(MoveUtil.mc.thePlayer.motionY = jumpY);
    }
    
    public static void strafeNoTargetStrafe(final EventMove event, final double speed) {
        final float direction = (float)Math.toRadians(getPlayerDirection());
        if (isMoving()) {
            event.setX(MoveUtil.mc.thePlayer.motionX = -Math.sin(direction) * speed);
            event.setZ(MoveUtil.mc.thePlayer.motionZ = Math.cos(direction) * speed);
        }
        else {
            event.setX(MoveUtil.mc.thePlayer.motionX = 0.0);
            event.setZ(MoveUtil.mc.thePlayer.motionZ = 0.0);
        }
    }
    
    public static float[] incrementMoveDirection(float forward, float strafe) {
        if (forward != 0.0f || strafe != 0.0f) {
            final float value = (forward != 0.0f) ? Math.abs(forward) : Math.abs(strafe);
            if (forward > 0.0f) {
                if (strafe > 0.0f) {
                    strafe = 0.0f;
                }
                else if (strafe == 0.0f) {
                    strafe = -value;
                }
                else if (strafe < 0.0f) {
                    forward = 0.0f;
                }
            }
            else if (forward == 0.0f) {
                if (strafe > 0.0f) {
                    forward = value;
                }
                else {
                    forward = -value;
                }
            }
            else if (strafe < 0.0f) {
                strafe = 0.0f;
            }
            else if (strafe == 0.0f) {
                strafe = value;
            }
            else if (strafe > 0.0f) {
                forward = 0.0f;
            }
        }
        return new float[] { forward, strafe };
    }
    
    public static double direction() {
        float rotationYaw = MoveUtil.mc.thePlayer.movementYaw;
        if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        }
        else if (MoveUtil.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MoveUtil.mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 70.0f * forward;
        }
        if (MoveUtil.mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 70.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }
    
    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0) {
            forward = -0.5f;
        }
        else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }
    
    public static void stop() {
        MoveUtil.mc.thePlayer.motionX = 0.0;
        MoveUtil.mc.thePlayer.motionZ = 0.0;
    }
    
    public static double predictedMotion(final double motion, final int ticks) {
        if (ticks == 0) {
            return motion;
        }
        double predicted = motion;
        for (int i = 0; i < ticks; ++i) {
            predicted = (predicted - 0.08) * 0.9800000190734863;
        }
        return predicted;
    }
    
    public static void fixMovement(final EventMoveInput event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();
        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(MoveUtil.mc.thePlayer.rotationYaw, forward, strafe)));
        if (forward == 0.0f && strafe == 0.0f) {
            return;
        }
        float closestForward = 0.0f;
        float closestStrafe = 0.0f;
        float closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1.0f; predictedForward <= 1.0f; ++predictedForward) {
            for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; ++predictedStrafe) {
                if (predictedStrafe != 0.0f || predictedForward != 0.0f) {
                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                    final double difference = Math.abs(angle - predictedAngle);
                    if (difference < closestDifference) {
                        closestDifference = (float)difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }
        }
        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }
    
    public static float getMoveYaw(float yaw) {
        final Vector2f from = new Vector2f((float)MoveUtil.mc.thePlayer.lastTickPosX, (float)MoveUtil.mc.thePlayer.lastTickPosZ);
        final Vector2f to = new Vector2f((float)MoveUtil.mc.thePlayer.posX, (float)MoveUtil.mc.thePlayer.posZ);
        final Vector2f diff = new Vector2f(to.x - from.x, to.y - from.y);
        final double x = diff.x;
        final double z = diff.y;
        if (x != 0.0 && z != 0.0) {
            yaw = (float)Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }
    
    public static boolean isPositionValidity(final Vec3 vec3) {
        final BlockPos pos = new BlockPos(vec3);
        return !isBlockSolid(pos) && !isBlockSolid(pos.add(0, 1, 0)) && isSafeToWalkOn(pos.add(0, -1, 0));
    }
    
    public static boolean isBlockNearBy(final double distance) {
        final double smallX = Math.min(MoveUtil.mc.thePlayer.posX - distance, MoveUtil.mc.thePlayer.posX + distance);
        final double smallY = Math.min(MoveUtil.mc.thePlayer.posY, MoveUtil.mc.thePlayer.posY);
        final double smallZ = Math.min(MoveUtil.mc.thePlayer.posZ - distance, MoveUtil.mc.thePlayer.posZ + distance);
        final double bigX = Math.max(MoveUtil.mc.thePlayer.posX - distance, MoveUtil.mc.thePlayer.posX + distance);
        final double bigY = Math.max(MoveUtil.mc.thePlayer.posY, MoveUtil.mc.thePlayer.posY);
        final double bigZ = Math.max(MoveUtil.mc.thePlayer.posZ - distance, MoveUtil.mc.thePlayer.posZ + distance);
        for (int x = (int)smallX; x <= bigX; ++x) {
            for (int y = (int)smallY; y <= bigY; ++y) {
                for (int z = (int)smallZ; z <= bigZ; ++z) {
                    if (!isPositionValidity(new Vec3((double)x, (double)y, (double)z)) && isPositionValidity(new Vec3((double)x, (double)(y + 1), (double)z))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean isBlockSolid(final BlockPos pos) {
        final Block block = MoveUtil.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockSlab || block instanceof BlockStairs || block instanceof BlockCactus || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSkull || block instanceof BlockPane || block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockGlass || block instanceof BlockPistonBase || block instanceof BlockPistonExtension || block instanceof BlockPistonMoving || block instanceof BlockStainedGlass || block instanceof BlockTrapDoor;
    }
    
    private static boolean isSafeToWalkOn(final BlockPos pos) {
        final Block block = MoveUtil.mc.theWorld.getBlockState(pos).getBlock();
        return !(block instanceof BlockFence) && !(block instanceof BlockWall);
    }
    
    public static Block getBlockUnderPlayer(final EntityPlayer entityPlayer, final double height) {
        return MoveUtil.mc.theWorld.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY - height, entityPlayer.posZ)).getBlock();
    }
    
    public static float[] getRotationsBlock(final BlockPos block, final EnumFacing face) {
        final double x = block.getX() + 0.5 - MoveUtil.mc.thePlayer.posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + 0.5 - MoveUtil.mc.thePlayer.posZ + face.getFrontOffsetZ() / 2.0;
        final double y = block.getY() + 0.5;
        final double d1 = MoveUtil.mc.thePlayer.posY + MoveUtil.mc.thePlayer.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(Math.atan2(d1, d2) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[] { yaw, pitch };
    }
    
    public static float getDistanceToGround(final Entity e) {
        if (MoveUtil.mc.thePlayer.isCollidedVertically && MoveUtil.mc.thePlayer.onGround) {
            return 0.0f;
        }
        float a = (float)e.posY;
        while (a > 0.0f) {
            final int[] stairs = { 53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180 };
            final int[] exemptIds = { 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175, 176, 177 };
            final Block block = MoveUtil.mc.theWorld.getBlockState(new BlockPos(e.posX, (double)(a - 1.0f), e.posZ)).getBlock();
            if (!(block instanceof BlockAir)) {
                if (Block.getIdFromBlock(block) == 44 || Block.getIdFromBlock(block) == 126) {
                    return Math.max((float)(e.posY - a - 0.5), 0.0f);
                }
                int[] arrayOfInt1;
                for (int j = (arrayOfInt1 = stairs).length, i = 0; i < j; ++i) {
                    final int id = arrayOfInt1[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return Math.max((float)(e.posY - a - 1.0), 0.0f);
                    }
                }
                for (int j = (arrayOfInt1 = exemptIds).length, i = 0; i < j; ++i) {
                    final int id = arrayOfInt1[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return Math.max((float)(e.posY - a), 0.0f);
                    }
                }
                return (float)(e.posY - a + block.getBlockBoundsMaxY() - 1.0);
            }
            else {
                --a;
            }
        }
        return 0.0f;
    }
    
    public static Block getBlockAtPosC(final double x, final double y, final double z) {
        final EntityPlayer inPlayer = (EntityPlayer)MoveUtil.mc.thePlayer;
        return MoveUtil.mc.theWorld.getBlockState(new BlockPos(inPlayer.posX + x, inPlayer.posY + y, inPlayer.posZ + z)).getBlock();
    }
    
    public static boolean isMoving() {
        return MoveUtil.mc.thePlayer != null && (MoveUtil.mc.thePlayer.movementInput.moveForward != 0.0f || MoveUtil.mc.thePlayer.movementInput.moveStrafe != 0.0f);
    }
    
    public static float getSpeed() {
        return (float)Math.sqrt(MoveUtil.mc.thePlayer.motionX * MoveUtil.mc.thePlayer.motionX + MoveUtil.mc.thePlayer.motionZ * MoveUtil.mc.thePlayer.motionZ);
    }
    
    public static double getSpeed(final double motionX, final double motionZ) {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }
    
    public static int getSpeedEffect() {
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }
    
    public static double getBaseMoveSpeed() {
        double baseSpeed = MoveUtil.mc.thePlayer.capabilities.getWalkSpeed() * 2.873;
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0 + 0.2 * (MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
    
    public static int getSpeedEffect(final EntityPlayer player) {
        if (player.isPotionActive(Potion.moveSpeed)) {
            return player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }
    
    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.jump)) {
            final int amplifier = MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (amplifier + 1) * 0.1f;
        }
        return baseJumpHeight;
    }
    
    public static int getJumpEffect() {
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.jump)) {
            return MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        }
        return 0;
    }
    
    public static void strafe() {
        strafe(getSpeed());
    }
    
    public static void strafe(final EventMove eventMove) {
        strafe(eventMove, getSpeed());
    }
    
    public static void strafe(final double speed) {
        if (!isMoving()) {
            return;
        }
        final double yaw = getDirection();
        MoveUtil.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        MoveUtil.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
    
    public static void strafe(final EventMove eventMove, final double speed) {
        if (!isMoving()) {
            return;
        }
        final double yaw = getDirection();
        eventMove.setX(MoveUtil.mc.thePlayer.motionX = -Math.sin(yaw) * speed);
        eventMove.setZ(MoveUtil.mc.thePlayer.motionZ = Math.cos(yaw) * speed);
    }
    
    public static void strafe(final double speed, float yaw) {
        if (!isMoving()) {
            return;
        }
        yaw = (float)Math.toRadians(yaw);
        MoveUtil.mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        MoveUtil.mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }
    
    public static void setMotion(final double speed) {
        setMotion(speed, MoveUtil.mc.thePlayer.rotationYaw);
    }
    
    public static void setMotion(final EventMove e, final double speed, float yaw) {
        double forward = MoveUtil.mc.thePlayer.movementInput.moveForward;
        double strafe = MoveUtil.mc.thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            final EntityPlayerSP thePlayer = MoveUtil.mc.thePlayer;
            final double n = 0.0;
            e.x = n;
            thePlayer.motionX = n;
            final EntityPlayerSP thePlayer2 = MoveUtil.mc.thePlayer;
            final double n2 = 0.0;
            e.z = n2;
            thePlayer2.motionZ = n2;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final EntityPlayerSP thePlayer3 = MoveUtil.mc.thePlayer;
            final double n3 = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            e.x = n3;
            thePlayer3.motionX = n3;
            final EntityPlayerSP thePlayer4 = MoveUtil.mc.thePlayer;
            final double n4 = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
            e.z = n4;
            thePlayer4.motionZ = n4;
        }
    }
    
    public static void setMotion(final EventMove e, final double speed) {
        setMotion(e, speed, MoveUtil.mc.thePlayer.rotationYaw);
    }
    
    public static void setMotion(final double speed, float yaw) {
        double forward = MoveUtil.mc.thePlayer.movementInput.moveForward;
        double strafe = MoveUtil.mc.thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            MoveUtil.mc.thePlayer.motionX = 0.0;
            MoveUtil.mc.thePlayer.motionZ = 0.0;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MoveUtil.mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            MoveUtil.mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }
    
    public static void hypixelTeleport(final double[] startPos, final BlockPos endPos) {
        final double dist = Math.sqrt(MoveUtil.mc.thePlayer.getDistanceSq(endPos));
        final double distanceEntreLesPackets = 0.31 + getSpeedEffect() / 20;
        double ztp = 0.0;
        if (dist > distanceEntreLesPackets) {
            final double nbPackets = (double)(Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1L);
            double xtp = MoveUtil.mc.thePlayer.posX;
            double ytp = MoveUtil.mc.thePlayer.posY;
            ztp = MoveUtil.mc.thePlayer.posZ;
            double count = 0.0;
            for (int i = 1; i < nbPackets; ++i) {
                final double xdi = (endPos.getX() - MoveUtil.mc.thePlayer.posX) / nbPackets;
                xtp += xdi;
                final double zdi = (endPos.getZ() - MoveUtil.mc.thePlayer.posZ) / nbPackets;
                ztp += zdi;
                final double ydi = (endPos.getY() - MoveUtil.mc.thePlayer.posY) / nbPackets;
                ytp += ydi;
                ++count;
                if (!MoveUtil.mc.theWorld.getBlockState(new BlockPos(xtp, ytp - 1.0, ztp)).getBlock().isFullBlock()) {
                    if (count <= 2.0) {
                        ytp += 2.0E-8;
                    }
                    else if (count >= 4.0) {
                        count = 0.0;
                    }
                }
                final C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, false);
                PacketUtils.sendPacketNoEvent((Packet)Packet);
            }
            MoveUtil.mc.thePlayer.setPosition(endPos.getX() + 0.5, (double)endPos.getY(), endPos.getZ() + 0.5);
        }
        else {
            MoveUtil.mc.thePlayer.setPosition((double)endPos.getX(), (double)endPos.getY(), (double)endPos.getZ());
        }
    }
    
    public static boolean checkTeleport(final double x, final double y, final double z, final double distBetweenPackets) {
        final double distx = MoveUtil.mc.thePlayer.posX - x;
        final double disty = MoveUtil.mc.thePlayer.posY - y;
        final double distz = MoveUtil.mc.thePlayer.posZ - z;
        final double dist = Math.sqrt(MoveUtil.mc.thePlayer.getDistanceSq(x, y, z));
        final double distanceEntreLesPackets = distBetweenPackets;
        final double nbPackets = (double)(Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1L);
        double xtp = MoveUtil.mc.thePlayer.posX;
        double ytp = MoveUtil.mc.thePlayer.posY;
        double ztp = MoveUtil.mc.thePlayer.posZ;
        for (int i = 1; i < nbPackets; ++i) {
            final double xdi = (x - MoveUtil.mc.thePlayer.posX) / nbPackets;
            xtp += xdi;
            final double zdi = (z - MoveUtil.mc.thePlayer.posZ) / nbPackets;
            ztp += zdi;
            final double ydi = (y - MoveUtil.mc.thePlayer.posY) / nbPackets;
            ytp += ydi;
            final AxisAlignedBB bb = new AxisAlignedBB(xtp - 0.3, ytp, ztp - 0.3, xtp + 0.3, ytp + 1.8, ztp + 0.3);
            if (!MoveUtil.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtil.mc.thePlayer, bb).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public static double getDirection() {
        float rotationYaw = MoveUtil.mc.thePlayer.rotationYaw;
        if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        }
        else if (MoveUtil.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MoveUtil.mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MoveUtil.mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }
    
    public static float getPlayerDirection() {
        float direction = MoveUtil.mc.thePlayer.rotationYaw;
        if (MoveUtil.mc.thePlayer.moveForward > 0.0f) {
            if (MoveUtil.mc.thePlayer.moveStrafing > 0.0f) {
                direction -= 45.0f;
            }
            else if (MoveUtil.mc.thePlayer.moveStrafing < 0.0f) {
                direction += 45.0f;
            }
        }
        else if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            if (MoveUtil.mc.thePlayer.moveStrafing > 0.0f) {
                direction -= 135.0f;
            }
            else if (MoveUtil.mc.thePlayer.moveStrafing < 0.0f) {
                direction += 135.0f;
            }
            else {
                direction -= 180.0f;
            }
        }
        else if (MoveUtil.mc.thePlayer.moveStrafing > 0.0f) {
            direction -= 90.0f;
        }
        else if (MoveUtil.mc.thePlayer.moveStrafing < 0.0f) {
            direction += 90.0f;
        }
        return direction;
    }
    
    public static void useDiagonalSpeed() {
        final KeyBinding[] gameSettings = { MoveUtil.mc.gameSettings.keyBindForward, MoveUtil.mc.gameSettings.keyBindRight, MoveUtil.mc.gameSettings.keyBindBack, MoveUtil.mc.gameSettings.keyBindLeft };
        final int[] down = { 0 };
        final boolean active = down[0] == 1;
        if (!active) {
            return;
        }
        final double groundIncrease = 0.0026000750109401644;
        final double airIncrease = 5.199896488849598E-4;
        final double increase = MoveUtil.mc.thePlayer.onGround ? 0.0026000750109401644 : 5.199896488849598E-4;
        moveFlying(increase);
    }
    
    public static void moveFlying(final double increase) {
        if (!isMoving()) {
            return;
        }
        final double yaw = direction();
        final EntityPlayerSP thePlayer = MoveUtil.mc.thePlayer;
        thePlayer.motionX += -MathHelper.sin((float)yaw) * increase;
        final EntityPlayerSP thePlayer2 = MoveUtil.mc.thePlayer;
        thePlayer2.motionZ += MathHelper.cos((float)yaw) * increase;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
