package cn.molokymc.prideplus.module.impl.misc;

import cn.molokymc.prideplus.event.impl.game.TickEvent;
import cn.molokymc.prideplus.utils.hackerdetector.Detection;
import cn.molokymc.prideplus.utils.hackerdetector.DetectionManager;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.MultipleBoolSetting;
import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;


public class HackerDetector extends Module {

    private final DetectionManager detectionManager = new DetectionManager();
    private final TimerUtil timer = new TimerUtil();
    private int count = 0;
    private final MultipleBoolSetting detections = new MultipleBoolSetting("Detections",
            new BooleanSetting("Flight A", true),
            new BooleanSetting("Flight B", true),
            new BooleanSetting("Reach A", true));

    public HackerDetector() {
        super("HackerDetector","黑客识别", Category.MISC, "Detects people using cheats inside your game");
        this.addSettings(detections);
    }

    @Override
    public void onTickEvent(TickEvent event) {
        setSuffix(String.valueOf(count));
        if(Utils.mc.theWorld == null || Utils.mc.thePlayer == null) return;
        for(Entity entity : Utils.mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) entity;
                if(entityPlayer != Utils.mc.thePlayer) {
                    for(Detection d : detectionManager.getDetections()) {
                        if(detections.getSetting(d.getName()).isEnabled()) {
                            if(d.runCheck(entityPlayer) && System.currentTimeMillis() > d.getLastViolated() + 500) {
                                count++;
                                NotificationManager.post(NotificationType.WARNING, entityPlayer.getName(), "has flagged " + d.getName() + " | " + EnumChatFormatting.BOLD + entityPlayer.VL);
                                entityPlayer.VL++;
                                d.setLastViolated(System.currentTimeMillis());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        count = 0;
        super.onEnable();
    }
}
