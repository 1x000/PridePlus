package cn.molokymc.prideplus.utils.hackerdetector.checks;

import cn.molokymc.prideplus.utils.hackerdetector.Category;
import cn.molokymc.prideplus.utils.hackerdetector.Detection;
import cn.molokymc.prideplus.utils.hackerdetector.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightB extends Detection {

    public FlightB() {
        super("Flight B", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return player.airTicks > 20 && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
