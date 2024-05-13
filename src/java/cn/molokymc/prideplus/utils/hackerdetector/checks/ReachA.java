package cn.molokymc.prideplus.utils.hackerdetector.checks;

import cn.molokymc.prideplus.utils.hackerdetector.Category;
import cn.molokymc.prideplus.utils.hackerdetector.Detection;
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
