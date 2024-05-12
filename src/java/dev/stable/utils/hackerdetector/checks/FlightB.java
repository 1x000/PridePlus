package dev.stable.utils.hackerdetector.checks;

import dev.stable.utils.hackerdetector.Category;
import dev.stable.utils.hackerdetector.Detection;
import dev.stable.utils.hackerdetector.utils.MovementUtils;
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
