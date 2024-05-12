package dev.stable.module.impl.render;

import dev.stable.event.impl.render.HurtCamEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam","无受伤抖动", Category.RENDER, "removes shaking after being hit");
    }

    @Override
    public void onHurtCamEvent(HurtCamEvent e) {
        e.cancel();
    }

}
