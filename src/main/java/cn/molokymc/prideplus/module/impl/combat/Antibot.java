package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import net.minecraft.entity.EntityLivingBase;



public class Antibot extends Module {
    private static BooleanSetting ticksExisted = new BooleanSetting("ticksExisted", false);
    private static NumberSetting TicksExisted = new NumberSetting("ticks Existed", 10, 200, 1, 1);
    private static BooleanSetting unfairName = new BooleanSetting("unfairName", false);
    public Antibot() {
        super("AntiBot", "防机器人",Category.COMBAT, "Anti NPC-Check");
        addSettings(ticksExisted, TicksExisted, unfairName);
        TicksExisted.addParent(ticksExisted, ParentAttribute.BOOLEAN_CONDITION);
    }

    public boolean onMotionEvent(EntityLivingBase e){
        if(ticksExisted.isEnabled() && e.ticksExisted > TicksExisted.getValue()){
            return true;
        }

        return unfairName.isEnabled() && (e.getName().length() < 16 || e.getName().length() >= 3);
    }
}
