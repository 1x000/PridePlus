package dev.stable.module.impl.player;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.event.impl.player.SlowDownEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.Setting;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.player.MovementUtils;
import dev.stable.utils.server.PacketUtils;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;


public class NoSlowDown extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", new String[] { "Vanilla", "NCP","Hypixel" });

    private boolean synced;

    public NoSlowDown() {
        super("NoSlow","无减速", Category.PLAYER, "prevent item slowdown");
        addSettings(new Setting[] { (Setting)this.mode });
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
                        PacketUtils.sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        break;
                    }
                    PacketUtils.sendPacket((Packet)new C08PacketPlayerBlockPlacement(Utils.mc.thePlayer.getCurrentEquippedItem()));
                }
                break;
        }
    }
}
