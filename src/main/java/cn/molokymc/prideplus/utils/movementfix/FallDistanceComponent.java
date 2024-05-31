package cn.molokymc.prideplus.utils.movementfix;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.ListenerAdapter;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;

import static cn.molokymc.prideplus.utils.Utils.mc;

public final class FallDistanceComponent extends ListenerAdapter {
    public static float distance;
    private float lastDistance;


    public void register() {
        Pride.INSTANCE.getEventProtocol().register(this);
    }

    public void unregister() {
        Pride.INSTANCE.getEventProtocol().unregister(this);
    }

    public void onMotionEvent(final MotionEvent event) {
        if (event.isPre()) {
            final float fallDistance = mc.thePlayer.fallDistance;
            if (fallDistance == 0.0f) {
                FallDistanceComponent.distance = 0.0f;
            }
            FallDistanceComponent.distance += fallDistance - this.lastDistance;
            this.lastDistance = fallDistance;
        }
    }
}
