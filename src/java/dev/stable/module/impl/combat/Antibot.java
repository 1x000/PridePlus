package dev.stable.module.impl.combat;

import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.ParentAttribute;
import dev.stable.module.settings.impl.BooleanSetting;
import dev.stable.module.settings.impl.NumberSetting;
import net.minecraft.entity.EntityLivingBase;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;



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
