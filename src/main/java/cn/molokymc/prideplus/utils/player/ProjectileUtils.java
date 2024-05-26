package cn.molokymc.prideplus.utils.player;

import cn.molokymc.prideplus.utils.math.MathUtil;
import cn.molokymc.prideplus.utils.math.Rotation;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static cn.molokymc.prideplus.utils.Utils.mc;

public class ProjectileUtils
{
    public static ProjectileHit predict(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, final double motionSlowdown, final double size, final double gravity, final boolean draw) {
        MovingObjectPosition landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;
        if (draw) {
            enableRender3D(true);
            RenderUtil.color(new Color(230, 230, 230).getRGB(),0f);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
        }
        while (!hasLanded && posY > -60.0) {
            Vec3 posBefore = new Vec3(posX, posY, posZ);
            Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3(posX, posY, posZ);
            posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
            }
            final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            final List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(Entity.class, arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (int i = 0; i < entityList.size(); ++i) {
                final Entity var18 = entityList.get(i);
                if (var18.canBeCollidedWith() && var18 != mc.thePlayer) {
                    final AxisAlignedBB var19 = var18.getEntityBoundingBox().expand(size, size, size);
                    final MovingObjectPosition possibleEntityLanding = var19.calculateIntercept(posBefore, posAfter);
                    if (possibleEntityLanding != null) {
                        hitEntity = true;
                        hasLanded = true;
                        landingPosition = possibleEntityLanding;
                    }
                }
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            final BlockPos var20 = new BlockPos(posX, posY, posZ);
            final Block var21 = mc.theWorld.getBlockState(var20).getBlock();
            if (var21.getBlockState().getBlock().getMaterial() == Material.water) {
                motionX *= 0.6;
                motionY *= 0.6;
                motionZ *= 0.6;
            }
            else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }
            motionY -= gravity;
            if (draw) {
                GL11.glVertex3d(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            }
        }
        return new ProjectileHit(posX, posY, posZ, hitEntity, hasLanded, landingPosition);
    }
    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }
    public static class EnderPearlPredictor
    {
        public double predictX;
        public double predictY;
        public double predictZ;
        public double minMotionY;
        public double maxMotionY;

        public EnderPearlPredictor(final double predictX, final double predictY, final double predictZ, final double minMotionY, final double maxMotionY) {
            this.predictX = predictX;
            this.predictY = predictY;
            this.predictZ = predictZ;
            this.minMotionY = minMotionY;
            this.maxMotionY = maxMotionY;
        }

        public double assessRotation(final Rotation rotation) {
            double mul = 1.0;
            int cnt = 0;
            for (double rate = 0.0; rate <= 1.0; rate += 0.3333) {
                for (int yaw = -1; yaw <= 1; ++yaw) {
                    for (int pitch = -1; pitch <= 1; ++pitch) {
                        mul *= this.assessSingleRotation(new Rotation(rotation.yaw + yaw * 0.5f, rotation.pitch + pitch * 0.5f), MathUtil.interpolate(this.minMotionY, this.maxMotionY, rate));
                        ++cnt;
                    }
                }
                if (this.minMotionY == this.maxMotionY) {
                    break;
                }
            }
            return Math.pow(mul, 1.0 / cnt);
        }

        private double assessSingleRotation(final Rotation rotation, final double motionYOffset) {
            if (rotation.pitch > 90.0f) {
                rotation.pitch = 90.0f;
            }
            if (rotation.pitch < -90.0f) {
                rotation.pitch = -90.0f;
            }
            final float motionFactor = 1.5f;
            final float gravity = 0.03f;
            final float size = 0.25f;
            final float motionSlowdown = 0.99f;
            final double posX = this.predictX - MathHelper.cos(rotation.yaw / 180.0f * 3.1415927f) * 0.16f;
            final double posY = this.predictY + mc.thePlayer.getEyeHeight() - 0.10000000149011612;
            final double posZ = this.predictZ - MathHelper.sin(rotation.pitch / 180.0f * 3.1415927f) * 0.16f;
            double motionX = -MathHelper.sin(rotation.yaw / 180.0f * 3.1415927f) * MathHelper.cos(rotation.pitch / 180.0f * 3.1415927f) * 0.4;
            double motionY = -MathHelper.sin(rotation.pitch / 180.0f * 3.1415927f) * 0.4;
            double motionZ = MathHelper.cos(rotation.yaw / 180.0f * 3.1415927f) * MathHelper.cos(rotation.pitch / 180.0f * 3.1415927f) * 0.4;
            final float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= 1.5;
            motionY *= 1.5;
            motionZ *= 1.5;
            motionY += motionYOffset;
            final ProjectileHit projectileHit = ProjectileUtils.predict(posX, posY, posZ, motionX, motionY, motionZ, 0.9900000095367432, 0.25, 0.029999999329447746, false);
            if (!projectileHit.hasLanded) {
                return 0.05;
            }
            final EnumFacing facing = projectileHit.landingPosition.sideHit;
            final BlockPos landPos = projectileHit.landingPosition.getBlockPos().add(facing.getDirectionVec());
            return ((facing == EnumFacing.UP || facing == EnumFacing.DOWN) ? this.assessPlainBlockPos(landPos) : this.assessSideBlockPos(landPos, facing)) * this.distanceFunction(new Vec3(this.predictX, this.predictY, this.predictZ).distanceTo(new Vec3(projectileHit.posX, projectileHit.posY, projectileHit.posZ)));
        }

        private double assessPlainBlockPos(final BlockPos pos) {
            double mul = 1.0;
            mul *= Math.pow(this.assessSingleBlockPos(pos.add(0, 0, 0)), 2.0);
            mul *= this.assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(-1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(0, 0, 1));
            mul *= this.assessSingleBlockPos(pos.add(0, 0, -1));
            return Math.pow(mul, 0.16666666666666666);
        }

        private double assessSideBlockPos(final BlockPos pos, final EnumFacing facing) {
            double mul = 1.0;
            mul *= Math.pow(this.assessSingleBlockPos(pos.add(0, 0, 0)), 2.0);
            mul *= this.assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(facing.getDirectionVec()));
            return Math.pow(mul, 0.3333333333333333);
        }

        private double assessSingleBlockPos(final BlockPos pos) {
            for (int y = 0; y >= -5; --y) {
                final IBlockState blockState = mc.theWorld.getBlockState(pos.add(0, y, 0));
                if (y == 0 && blockState.getBlock().isFullBlock()) {
                    return 0.4;
                }
                if (blockState.getBlock().isFullBlock()) {
                    return 1.0;
                }
            }
            return 0.05;
        }

        private double distanceFunction(double d) {
            d /= 1000.0;
            return (d + 3.0) / (d + 2.0) / 1.5;
        }
    }

    public static class ProjectileHit
    {
        private final double posX;
        private final double posY;
        private final double posZ;
        private final boolean hitEntity;
        private final boolean hasLanded;
        private final MovingObjectPosition landingPosition;

        public ProjectileHit(final double posX, final double posY, final double posZ, final boolean hitEntity, final boolean hasLanded, final MovingObjectPosition landingPosition) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.hitEntity = hitEntity;
            this.hasLanded = hasLanded;
            this.landingPosition = landingPosition;
        }

        public double getPosX() {
            return this.posX;
        }

        public double getPosY() {
            return this.posY;
        }

        public double getPosZ() {
            return this.posZ;
        }

        public boolean isHitEntity() {
            return this.hitEntity;
        }

        public boolean isHasLanded() {
            return this.hasLanded;
        }

        public MovingObjectPosition getLandingPosition() {
            return this.landingPosition;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.posX, this.posY, this.posZ, this.hitEntity, this.hasLanded, this.landingPosition);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            final ProjectileHit that = (ProjectileHit)obj;
            return Double.compare(that.posX, this.posX) == 0 && Double.compare(that.posY, this.posY) == 0 && Double.compare(that.posZ, this.posZ) == 0 && this.hitEntity == that.hitEntity && this.hasLanded == that.hasLanded && Objects.equals(this.landingPosition, that.landingPosition);
        }

        @Override
        public String toString() {
            return "ProjectileHit{posX=" + this.posX + ", posY=" + this.posY + ", posZ=" + this.posZ + ", hitEntity=" + this.hitEntity + ", hasLanded=" + this.hasLanded + ", landingPosition=" + this.landingPosition + '}';
        }
    }
}

