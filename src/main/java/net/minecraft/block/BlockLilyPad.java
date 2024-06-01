package net.minecraft.block;

import cn.molokymc.prideplus.viamcp.versionfix.VersionFixer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockLilyPad extends BlockBush {
    protected BlockLilyPad() {
        this.setBlockBounds(0f, 0f, 0f, 1f, 0.015625f, 1f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        if (collidingEntity == null || !(collidingEntity instanceof EntityBoat))
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        // ViaForgeMCP
        return VersionFixer.getCollisionBoundingBox_BlockLilyPad(pos, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
        // End
    }

    public int getBlockColor() {
        return 7455580;
    }

    public int getRenderColor(IBlockState state) {
        return 7455580;
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
        return 2129968;
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.water;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());
            return iblockstate.getBlock().getMaterial() == Material.water && iblockstate.getValue(BlockLiquid.LEVEL) == 0;
        } else
            return false;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
