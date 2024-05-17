package cn.molokymc.prideplus.utils.hackerdetector.checks;

import cn.molokymc.prideplus.utils.hackerdetector.Category;
import cn.molokymc.prideplus.utils.hackerdetector.Detection;
import cn.molokymc.prideplus.utils.hackerdetector.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class FlightA extends Detection {

    public FlightA() {
        super("Flight A", Category.MOVEMENT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return !player.onGround && player.motionY == 0 && MovementUtils.isMoving(player);
    }
}
