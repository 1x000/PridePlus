package dev.stable.module.impl.player;

import dev.stable.event.impl.network.PacketSendEvent;
import dev.stable.event.impl.player.MotionEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import dev.stable.utils.server.PacketUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;




public class SpeedMine extends Module {

    private final NumberSetting speed = new NumberSetting("Speed", 1.4, 3, 1, 0.1);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost;
    private float damage;

    public SpeedMine() {
        super("SpeedMine","加速挖方块", Category.PLAYER, "mines blocks faster");
        this.addSettings(speed);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (e.isPre()) {
            Utils.mc.playerController.blockHitDelay = 0;
            if (pos != null && boost) {
                IBlockState blockState = Utils.mc.theWorld.getBlockState(pos);
                if (blockState == null) return;

                try {
                    damage += blockState.getBlock().getPlayerRelativeBlockHardness(Utils.mc.thePlayer) * speed.getValue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                if (damage >= 1) {
                    try {
                        Utils.mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 11);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                    PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                    damage = 0;
                    boost = false;
                }
            }
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) e.getPacket();
            if (packet.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                boost = true;
                pos = packet.getPosition();
                facing = packet.getFacing();
                damage = 0;
            } else if (packet.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK | packet.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                boost = false;
                pos = null;
                facing = null;
            }
        }
    }

}
