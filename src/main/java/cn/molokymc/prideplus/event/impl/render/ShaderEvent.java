package cn.molokymc.prideplus.event.impl.render;

import cn.molokymc.prideplus.event.Event;
import cn.molokymc.prideplus.module.settings.impl.MultipleBoolSetting;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class ShaderEvent extends Event {

    private final boolean bloom;

    private final MultipleBoolSetting bloomOptions;

    public ShaderEvent(boolean bloom, MultipleBoolSetting bloomOptions){
        this.bloom = bloom;
        this.bloomOptions = bloomOptions;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isBloom() {
        return bloom;
    }

    public MultipleBoolSetting getBloomOptions() { return bloomOptions; }

}
