package cn.molokymc.prideplus.event.impl.render;

import cn.molokymc.prideplus.event.Event;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
public class EventRender2D extends Event {
    private float partialTicks;
    private final ScaledResolution resolution;
    private float width, height;

    public EventRender2D(ScaledResolution resolution, float partialTicks) {
        this.resolution = resolution;
        this.partialTicks = partialTicks;
        this.width = resolution.getScaledWidth();
        this.height = resolution.getScaledHeight();
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public void setHeight(float height) {
        this.height = height;
    }


    public void setWidth(float width) {
        this.width = width;
    }
}

