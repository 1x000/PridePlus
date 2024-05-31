package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.impl.game.LegitUpdateEvent;
import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.event.impl.player.SlowDownEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.combat.KillAura;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.player.BlockUtils;
import cn.molokymc.prideplus.utils.player.MovementUtils;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;


public class NoSlowDown extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Vanilla", "NCP","Hypixel","GrimAC");

    public NoSlowDown() {
        super("NoSlow","无减速", Category.PLAYER, "prevent item slowdown");
        addSettings(this.mode);
    }

    public void onSlowDownEvent(SlowDownEvent event) {
        if (mode.is("GrimAC") && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) return;

        if (!(Utils.mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemPotion))
            event.cancel();
    }

    public void onMotionEvent(MotionEvent e) {
        if (mc.thePlayer == null) return;

        setSuffix(mode.getMode());
        switch (this.mode.getMode()) {
            case "Hypixel":
                if (e.isPre()) {
                    if (mc.thePlayer.getHeldItem() == null) {
                        return;

                    } else if (mc.thePlayer.isUsingItem()) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), (ItemStack)null, 0.0F, 0.0F, 0.0F));

                        } else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

                        }
                    }
                }
                break;
            case "NCP":
                if (MovementUtils.isMoving() && Utils.mc.thePlayer.isUsingItem()) {
                    if (e.isPre()) {
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        break;
                    }
                    PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(Utils.mc.thePlayer.getCurrentEquippedItem()));
                }
                break;
            case "GrimAC": {
                boolean anti = true;
                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isValidBlock(movingObjectPosition.getBlockPos())) {
                        anti = false;
                    }
                }
                if (anti) {
                    if (e.isPre()) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem()){
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9 ));
                            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(Pride.NAME, new PacketBuffer(Unpooled.buffer())));
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                        if (isBlocking()) {
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9 ));
                            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(Pride.NAME, new PacketBuffer(Unpooled.buffer())));
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                    }
                    if (e.isPost()){
                        if (isBlocking()){
                            PacketUtils.sendPacketC0F();
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        }
                    }
                }
/*                if (e.isPost() && !mc.thePlayer.isUsingItem() && !Client.INSTANCE.getModuleCollection().get(Scaffold.class).isEnabled()) {

                }*/
            }
        }
    }

    public void onLegitUpdateEvent(LegitUpdateEvent eventMotion) {
        if (!eventMotion.isPre())
            return;

        if (mode.is("GrimAC")) {
            if (mc.thePlayer.getHeldItem() != null) {
                boolean anti = true;
                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isInteractBlock(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock())) {
                        anti = false;
                    }
                }
                if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                    anti = false;
                }
                if (anti) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold) {
                        mc.gameSettings.keyBindUseItem.pressed = false;
                        if (mc.thePlayer.isUsingItem()) {
                            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                        }
                    }
                }
            }
        }
    }

    public void onPacketSendEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer == null) return;
        if (mc.thePlayer.getHeldItem() != null) {
            if (mode.is("GrimAC")) {
                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood){
                    if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                        return;
                    }
                    boolean anti = true;
                    MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                    if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isInteractBlock(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock())) {
                            anti = false;
                        }
                    }
                    if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                        anti = false;
                    }
                    if (anti) {
                        if (packet instanceof C07PacketPlayerDigging){
                            if (((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM){
                                event.cancel();
                            }
                        }
                        if (packet instanceof C08PacketPlayerBlockPlacement){
                            event.cancel();
                        }
                    }
                }
            }
        }
    }

    private boolean isBlocking() {
        return ((mc.thePlayer.isBlocking() || (KillAura.wasBlocking)) && mc.thePlayer.getHeldItem() != null  && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword));
    }
}
