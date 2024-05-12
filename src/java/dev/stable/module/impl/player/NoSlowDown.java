package dev.stable.module.impl.player;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import dev.stable.Client;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.event.impl.player.SlowDownEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.impl.combat.KillAura;
import dev.stable.module.impl.movement.Scaffold;
import dev.stable.module.impl.movement.Sprint;
import dev.stable.module.settings.Setting;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.player.BlockUtils;
import dev.stable.utils.player.MovementUtils;
import dev.stable.utils.server.PacketUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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

    private boolean synced;

    public NoSlowDown() {
        super("NoSlow","无减速", Category.PLAYER, "prevent item slowdown");
        addSettings(this.mode);
    }

    public void onSlowDownEvent(SlowDownEvent event) {
        if (!(Utils.mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemPotion))
            event.cancel();
    }

    public void onMotionEvent(MotionEvent e) {
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
                            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(Client.NAME, new PacketBuffer(Unpooled.buffer())));
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                        if (isBlocking()) {
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9 ));
                            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(Client.NAME, new PacketBuffer(Unpooled.buffer())));
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
                if (e.isPost() && !mc.thePlayer.isUsingItem() && !Client.INSTANCE.getModuleCollection().get(Scaffold.class).isEnabled()) {
                    mc.thePlayer.setSprinting(true);
                }
            }
        }
    }

    private boolean isBlocking() {
        return ((mc.thePlayer.isBlocking() || (KillAura.wasBlocking)) && mc.thePlayer.getHeldItem() != null  && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword));
    }
}
