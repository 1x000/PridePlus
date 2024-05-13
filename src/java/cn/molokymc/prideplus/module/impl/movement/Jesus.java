package cn.molokymc.prideplus.module.impl.movement;

import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.event.impl.player.BoundingBoxEvent;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import net.minecraft.util.AxisAlignedBB;




public final class Jesus extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus");

    private boolean shouldJesus;

    public Jesus() {
        super("Jesus","水上行走", Category.MOVEMENT, "Walks on water, like jesus");
        this.addSettings(mode);
    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        if(event.getBlock().getMaterial().isLiquid()) {
            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            shouldJesus = true;
            event.setBoundingBox(axisAlignedBB);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        if(event.isPre()) {
            if(shouldJesus) {
                switch(mode.getMode()) {
                    case "Verus":
                        if(mc.thePlayer.ticksExisted % 5 == 0)
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ);
                        break;
                }
            }
            shouldJesus = false;
        }
    }
}
