package dev.stable.event.impl.world;

import dev.stable.event.Event;
import net.minecraft.client.Minecraft;

public class EventPreUpdate
extends Event {
    private float yaw;
    private float pitch;
    private double x, y, z;
    private boolean ground;

    public EventPreUpdate(double x, double y, double z, float yaw, float pitch,  boolean ground) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.y = y;
        this.ground = ground;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
    	//Helper.mc.thePlayer.rotationPitchHead = pitch;
        Minecraft.getMinecraft().thePlayer.rotationPitch = pitch;
        this.pitch = pitch;
    }

    public double getY() {
        return this.y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getX() {
        return x;
    }
    public double getZ() {
        return z;
    }
    public void setZ(double z) {
        this.z = z;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isOnground() {
        return this.ground;
    }

    public void setOnground(boolean ground) {
        this.ground = ground;
    }

}

