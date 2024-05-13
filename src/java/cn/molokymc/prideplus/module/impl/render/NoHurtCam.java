package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.event.impl.render.HurtCamEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam","无受伤抖动", Category.RENDER, "removes shaking after being hit");
    }

    @Override
    public void onHurtCamEvent(HurtCamEvent e) {
        e.cancel();
    }

}
