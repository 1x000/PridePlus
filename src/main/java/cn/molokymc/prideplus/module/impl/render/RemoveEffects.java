package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.event.impl.player.UpdateEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveEffects extends Module {
    public RemoveEffects() {
        super("AntiBadEffects","移除负面药水", Category.RENDER,"Remove bad effects.");
    }
    @Override
    public void onUpdateEvent(UpdateEvent e) {
        List effectIdsToRemove = (List)(new ArrayList());
        effectIdsToRemove.add(Potion.blindness.id);
        Iterator effectid = effectIdsToRemove.iterator();
        while(effectid.hasNext()) {
            int effectId = ((Number)effectid.next()).intValue();
             mc.thePlayer.removePotionEffectClient(effectId);
        }
    }




}
