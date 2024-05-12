package dev.stable.utils.hackerdetector.checks;

import dev.stable.utils.hackerdetector.Category;
import dev.stable.utils.hackerdetector.Detection;
import dev.stable.utils.hackerdetector.utils.MovementUtils;
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
