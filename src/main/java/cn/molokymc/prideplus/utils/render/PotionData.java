package cn.molokymc.prideplus.utils.render;

import cn.molokymc.prideplus.utils.render.Translate;

public class PotionData {
    public int maxTimer = 0;
    public float animationX = 0.0f;
    public final Translate translate;
    public final int level;

    public PotionData(Translate translate, int level) {
        this.translate = translate;
        this.level = level;
    }

    public float getAnimationX() {
        return this.animationX;
    }

    public int getMaxTimer() {
        return this.maxTimer;
    }
}

