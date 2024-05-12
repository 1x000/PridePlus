package dev.stable.utils.player;

import dev.stable.utils.EnumFacingUtils;
import dev.stable.utils.Utils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.core.pattern.NotANumber;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* loaded from: Fluid.jar:dev/Fluid/utils/player/PlayerUtil.class */
public final class PlayerUtil {
    public static boolean isBlockBlacklisted(Item item) {
        return (item instanceof ItemAnvilBlock) || item.getUnlocalizedName().contains(TextureUtils.texSand) || item.getUnlocalizedName().contains(TextureUtils.texGravel) || item.getUnlocalizedName().contains("ladder") || item.getUnlocalizedName().contains("tnt") || item.getUnlocalizedName().contains("chest") || item.getUnlocalizedName().contains("web");
    }

    public static EnumFacingUtils getEnumFacing(Vec3 position) {
        for (int x2 = -1; x2 <= 1; x2 += 2) {
            if (!(block(position.xCoord + x2, position.yCoord, position.zCoord) instanceof BlockAir)) {
                if (x2 > 0) {
                    return new EnumFacingUtils(EnumFacing.WEST, new Vec3(x2, 0.0d, 0.0d));
                } else {
                    return new EnumFacingUtils(EnumFacing.EAST, new Vec3(x2, 0.0d, 0.0d));
                }
            }
        }
        for (int y2 = -1; y2 <= 1; y2 += 2) {
            if (!(block(position.xCoord, position.yCoord + y2, position.zCoord) instanceof BlockAir) && y2 < 0) {
                return new EnumFacingUtils(EnumFacing.UP, new Vec3(0.0d, y2, 0.0d));
            }
        }
        for (int z2 = -1; z2 <= 1; z2 += 2) {
            if (!(block(position.xCoord, position.yCoord, position.zCoord + z2) instanceof BlockAir)) {
                if (z2 < 0) {
                    return new EnumFacingUtils(EnumFacing.SOUTH, new Vec3(0.0d, 0.0d, z2));
                } else {
                    return new EnumFacingUtils(EnumFacing.NORTH, new Vec3(0.0d, 0.0d, z2));
                }
            }
        }
        return null;
    }

    public static BlockPos getBlockCorner(BlockPos start, BlockPos end) {
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    BlockPos pos = new BlockPos(end.getX() + x, end.getY() + y, end.getZ() + z);
                    if (!isBlockBetween(start, pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isBlockBetween(BlockPos start, BlockPos end) {
        int startX = start.getX();
        int startY = start.getY();
        int startZ = start.getZ();
        int endX = end.getX();
        int endY = end.getY();
        int endZ = end.getZ();
        double diffX = endX - startX;
        double diffY = endY - startY;
        double diffZ = endZ - startZ;
        double x = startX;
        double y = startY;
        double z = startZ;
        int STEPS = ((int) Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ)))) * 4;
        for (int i = 0; i < STEPS - 1; i++) {
            x += diffX / STEPS;
            y += diffY / STEPS;
            z += diffZ / STEPS;
            if (x != endX || y != endY || z != endZ) {
                BlockPos pos = new BlockPos(x, y, z);
                Block block = Utils.mc.theWorld.getBlockState(pos).getBlock();
                if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) Utils.mc.thePlayer.lastTickPosX, (float) Utils.mc.thePlayer.lastTickPosZ);
        Vector2f to = new Vector2f((float) Utils.mc.thePlayer.posX, (float) Utils.mc.thePlayer.posZ);
        Vector2f diff = new Vector2f(to.x - from.x, to.y - from.y);
        double x = diff.x;
        double z = diff.y;
        if (x != 0.0d && z != 0.0d) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }

    public static boolean isBlockUnder(double height) {
        return isBlockUnder(height, true);
    }

    public static boolean isBlockUnder(double height, boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                AxisAlignedBB bb = Utils.mc.thePlayer.getEntityBoundingBox().offset(0.0d, -offset, 0.0d);
                if (!Utils.mc.theWorld.getCollidingBoundingBoxes(Utils.mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        for (int offset2 = 0; offset2 < height; offset2++) {
            if (blockRelativeToPlayer(0.0d, -offset2, 0.0d).isFullBlock()) {
                return true;
            }
        }
        return false;
    }

    public static Block blockRelativeToPlayer(double offsetX, double offsetY, double offsetZ) {
        return Utils.mc.theWorld.getBlockState(new BlockPos(Utils.mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static Block block(double x, double y, double z) {
        return Utils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static int findSoup() {
        for (int i = 36; i < 45; i++) {
            ItemStack itemStack = Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && itemStack.getItem().equals(Items.mushroom_stew) && itemStack.stackSize > 0 && (itemStack.getItem() instanceof ItemFood)) {
                return i;
            }
        }
        return -1;
    }

    public static int findItem(int startSlot, int endSlot, Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            ItemStack stack = Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static boolean hasSpaceHotbar() {
        for (int i = 36; i < 45; i++) {
            ItemStack itemStack = Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOnGround(double height) {
        return !Utils.mc.theWorld.getCollidingBoundingBoxes(Utils.mc.thePlayer, Utils.mc.thePlayer.getEntityBoundingBox().offset(0.0d, -height, 0.0d)).isEmpty();
    }

    public static Block getBlock(BlockPos pos) {
        return Utils.mc.theWorld.getBlockState(pos).getBlock();
    }

    public static Block getBlock(double d, double d2, double d3) {
        return Utils.mc.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock();
    }

    public static boolean isOnGround(Entity entity, double height) {
        return !Utils.mc.theWorld.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox().offset(0.0d, -height, 0.0d)).isEmpty();
    }

    public static boolean colorTeam(EntityPlayer sb) {
        String targetName = sb.getDisplayName().getFormattedText().replace("��r", "");
        String clientName = Utils.mc.thePlayer.getDisplayName().getFormattedText().replace("��r", "");
        return targetName.startsWith(NotANumber.VALUE + clientName.charAt(1));
    }

    public static boolean armorTeam(EntityPlayer entityPlayer) {
        if (Utils.mc.thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
            ItemStack myHead = Utils.mc.thePlayer.inventory.armorInventory[3];
            ItemArmor myItemArmor = (ItemArmor) myHead.getItem();
            ItemStack entityHead = entityPlayer.inventory.armorInventory[3];
            ItemArmor entityItemArmor = (ItemArmor) entityHead.getItem();
            return String.valueOf(entityItemArmor.getColor(entityHead)).equals("10511680") || myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead);
        }
        return false;
    }

    public static boolean scoreTeam(EntityPlayer entityPlayer) {
        return Utils.mc.thePlayer.isOnSameTeam(entityPlayer);
    }

    public static boolean MovementInput() {
        return Utils.mc.gameSettings.keyBindForward.isKeyDown() || Utils.mc.gameSettings.keyBindLeft.isKeyDown() || Utils.mc.gameSettings.keyBindRight.isKeyDown() || Utils.mc.gameSettings.keyBindBack.isKeyDown();
    }

    public static boolean isInLiquid() {
        if (Utils.mc.thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        int y = (int) Utils.mc.thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(Utils.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Utils.mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(Utils.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Utils.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                Block block = Utils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    public static boolean isOnLiquid() {
        AxisAlignedBB boundingBox = Utils.mc.thePlayer.getEntityBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        AxisAlignedBB boundingBox2 = boundingBox.contract(0.01d, 0.0d, 0.01d).offset(0.0d, -0.01d, 0.0d);
        boolean onLiquid = false;
        int y = (int) boundingBox2.minY;
        for (int x = MathHelper.floor_double(boundingBox2.minX); x < MathHelper.floor_double(boundingBox2.maxX + 1.0d); x++) {
            for (int z = MathHelper.floor_double(boundingBox2.minZ); z < MathHelper.floor_double(boundingBox2.maxZ + 1.0d); z++) {
                Block block = Utils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.air) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ) {
        List<Vec3> possibilities = new ArrayList<>();
        int range = (int) (5.0d + Math.abs(offsetX) + Math.abs(offsetZ));
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Block block = blockRelativeToPlayer(x, y, z);
                    if (!(block instanceof BlockAir)) {
                        for (int x2 = -1; x2 <= 1; x2 += 2) {
                            possibilities.add(new Vec3(Utils.mc.thePlayer.posX + x + x2, Utils.mc.thePlayer.posY + y, Utils.mc.thePlayer.posZ + z));
                        }
                        for (int y2 = -1; y2 <= 1; y2 += 2) {
                            possibilities.add(new Vec3(Utils.mc.thePlayer.posX + x, Utils.mc.thePlayer.posY + y + y2, Utils.mc.thePlayer.posZ + z));
                        }
                        for (int z2 = -1; z2 <= 1; z2 += 2) {
                            possibilities.add(new Vec3(Utils.mc.thePlayer.posX + x, Utils.mc.thePlayer.posY + y, Utils.mc.thePlayer.posZ + z + z2));
                        }
                    }
                }
            }
        }
        possibilities.removeIf(vec3 -> {
            return Utils.mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) > 5.0d || !(block(vec3.xCoord, vec3.yCoord, vec3.zCoord) instanceof BlockAir);
        });
        if (possibilities.isEmpty()) {
            return null;
        }
        possibilities.sort(Comparator.comparingDouble(vec32 -> {
            double d0 = (Utils.mc.thePlayer.posX + offsetX) - vec32.xCoord;
            double d1 = ((Utils.mc.thePlayer.posY - 1.0d) + offsetY) - vec32.yCoord;
            double d2 = (Utils.mc.thePlayer.posZ + offsetZ) - vec32.zCoord;
            return MathHelper.sqrt_double((d0 * d0) + (d1 * d1) + (d2 * d2));
        }));
        return possibilities.get(0);
    }

    private PlayerUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}