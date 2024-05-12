package dev.stable.utils.hackerdetector.checks;

import dev.stable.utils.hackerdetector.Category;
import dev.stable.utils.hackerdetector.Detection;
import net.minecraft.entity.player.EntityPlayer;

public class ReachA extends Detection {

    public ReachA() {
        super("Reach A", Category.COMBAT);
    }

    @Override
    public boolean runCheck(EntityPlayer player) {
        return false;
    }
}
