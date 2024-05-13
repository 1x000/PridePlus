package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.game.WorldEvent;
import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.misc.AutoHypixel;
import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.font.AbstractFontRenderer;
import cn.molokymc.prideplus.utils.player.RotationUtils;
import cn.molokymc.prideplus.utils.skidfont.FontDrawer;
import cn.molokymc.prideplus.utils.skidfont.FontManager;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Stealer extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 80, 300, 0, 10);
    private final BooleanSetting aura = new BooleanSetting("Aura", false);
    private final NumberSetting auraRange = new NumberSetting("Aura Range", 3, 6, 1, 1);
    public static BooleanSetting stealingIndicator = new BooleanSetting("Stealing Indicator", false);
    public static BooleanSetting titleCheck = new BooleanSetting("Title Check", true);
    public static BooleanSetting freeLook = new BooleanSetting("Free Look", true);
    private final BooleanSetting reverse = new BooleanSetting("Reverse", false);
    public static final BooleanSetting silent = new BooleanSetting("Silent", false);
    private final BooleanSetting smart = new BooleanSetting("Smart", false);

    private final List<BlockPos> openedChests = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    public static boolean stealing;
    private Manager Manager;
    private boolean clear;
    public static int count = 0;

    public Stealer() {
        super("Stealer","箱子小偷", Category.PLAYER, "auto loot chests");
        auraRange.addParent(aura, ParentAttribute.BOOLEAN_CONDITION);
        stealingIndicator.addParent(silent, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(delay, aura, auraRange, stealingIndicator, titleCheck, freeLook, reverse, silent, smart);
    }

    public static int chests = 0;
    public int checkClose = 0;
    @Override
    public void onMotionEvent(MotionEvent e) {
        setSuffix("Gay");
        /*if(Utils.mc.thePlayer.openContainer instanceof ContainerChest){
            count += 1;
        }*/
        if (e.isPre()) {
            setSuffix(smart.isEnabled() ? "Smart" : null);
            setSuffix(String.valueOf(chests));
            if (Manager == null) Manager = Client.INSTANCE.getModuleCollection().getModule(Manager.class);
            if (aura.isEnabled()) {
                final int radius = auraRange.getValue().intValue();
                for (int x = -radius; x < radius; x++) {
                    for (int y = -radius; y < radius; y++) {
                        for (int z = -radius; z < radius; z++) {
                            final BlockPos pos = new BlockPos(Utils.mc.thePlayer.posX + x, Utils.mc.thePlayer.posY + y, Utils.mc.thePlayer.posZ + z);
                            if (pos.getBlock() == Blocks.chest && !openedChests.contains(pos)) {
                                if (Utils.mc.playerController.onPlayerRightClick(Utils.mc.thePlayer, Utils.mc.theWorld, Utils.mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, new Vec3(pos))) {
                                    Utils.mc.thePlayer.swingItem();
                                    final float[] rotations = RotationUtils.getFacingRotations2(pos.getX(), pos.getY(), pos.getZ());
                                    e.setRotations(rotations[0], rotations[1]);
                                    RotationUtils.setVisualRotations(rotations[0], rotations[1]);
                                    openedChests.add(pos);
                                }
                            }
                        }
                    }
                }
            }
            if (Utils.mc.thePlayer.openContainer instanceof ContainerChest) {
                setSuffix("Stealing...");
                ++checkClose;
                if (checkClose == 1) {
                    ++chests;
                }
                ContainerChest chest = (ContainerChest) Utils.mc.thePlayer.openContainer;
                IInventory chestInv = chest.getLowerChestInventory();
                if (titleCheck.isEnabled() && (!(chestInv instanceof ContainerLocalMenu) || !((ContainerLocalMenu) chestInv).realChest))
                    return;
                clear = true;


                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < chestInv.getSizeInventory(); i++) {
                    ItemStack is = chestInv.getStackInSlot(i);
                    if (is != null && (!smart.isEnabled() || !(Manager.isBadItem(is, -1, true) || items.contains(is.getItem())))) {
                        slots.add(i);
                    }
                }

                if (reverse.isEnabled()) {
                    Collections.reverse(slots);
                }

                slots.forEach(s -> {
                    ItemStack is = chestInv.getStackInSlot(s);
                    Item item = is != null ? is.getItem() : null;
                    if (item != null && !items.contains(item) && (delay.getValue() == 0 || timer.hasTimeElapsed(delay.getValue().longValue(), true))) {
                        if (smart.isEnabled() && !(item instanceof ItemBlock)) {
                            items.add(is.getItem());
                        }
                        Utils.mc.playerController.windowClick(chest.windowId, s, 0, 1, Utils.mc.thePlayer);
                    }
                });

                if (slots.isEmpty() || isInventoryFull()) {
                    items.clear();
                    clear = false;
                    stealing = false;
                    Utils.mc.thePlayer.closeScreen();
                }
            } else if (clear) {
                checkClose = 0;
                items.clear();
                clear = false;
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (stealingIndicator.isEnabled() && stealing) {
            ScaledResolution sr = new ScaledResolution(Utils.mc);
            if( HUDMod.customFont.isEnabled()) {
                FontManager.edit20.drawStringWithShadow("Stealing...", sr.getScaledWidth() / 2.0F - fr.getStringWidth("Stealing...") / 2.0F, sr.getScaledHeight() / 2.0F + 3, HUDMod.getColor(0).getRGB());
            }
            else{
                fr.drawStringWithShadow("Stealing...", sr.getScaledWidth() / 2.0F - fr.getStringWidth("Stealing...") / 2.0F, sr.getScaledHeight() / 2.0F + 3, HUDMod.getClientColors().getFirst());
            }
        }
    }

    @Override
    public void onEnable() {
        chests = 0;
        openedChests.clear();
        super.onEnable();
    }
    private boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (Utils.mc.thePlayer.inventoryContainer.getSlot(i).getStack() == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean canSteal() {
        if (Client.INSTANCE.isEnabled(Stealer.class) && Utils.mc.currentScreen instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) Utils.mc.thePlayer.openContainer;
            IInventory chestInv = chest.getLowerChestInventory();
            return !titleCheck.isEnabled() || (chestInv instanceof ContainerLocalMenu && ((ContainerLocalMenu) chestInv).realChest);
        }
        return false;
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        if (event instanceof WorldEvent.Load) {
            openedChests.clear();
        }
    }

}
