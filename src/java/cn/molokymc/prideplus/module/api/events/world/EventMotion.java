package cn.molokymc.prideplus.module.api.events.world;


import cn.molokymc.prideplus.event.Event;
import net.minecraft.client.Minecraft;

public class EventMotion extends Event {
    public static float RENDERPREVYAW;
    public static float RENDERYAW;
    public static float RENDERPREVYAWHEAD;
    public static float RENDERYAWHEAD;
    public static float RENDERPREVYAWBODY;
    public static float RENDERYAWBODY;
    public static float RENDERPREVPITCH;
    public static float RENDERPITCH;
    public final boolean PRE;
    public float YAW;
    public float YAWHEAD;
    public float YAWBODY;
    private float prevYaw;
    private float prevPitch;
    public float PITCH;
    public double X;
    public double Y;
    public double Z;
    public boolean GROUND;

    public EventMotion(final float yaw, final float pitch, final float prevYaw, final float prevPitch, final double posX, final double posY, final double posZ, final boolean ground) {
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
        this.YAW = yaw;
        this.PITCH = pitch;
        this.GROUND = ground;
        this.X = posX;
        this.Y = posY;
        this.Z = posZ;
        this.PRE = true;
    }

    public EventMotion(final float yaw, final float pitch) {
        EventMotion.RENDERPREVYAW = EventMotion.RENDERYAW;
        EventMotion.RENDERYAW = yaw;
        EventMotion.RENDERPREVYAWHEAD = EventMotion.RENDERYAWHEAD;
        EventMotion.RENDERYAWHEAD = yaw;
        EventMotion.RENDERPREVYAWBODY = EventMotion.RENDERYAWBODY;
        EventMotion.RENDERYAWBODY = yaw;
        EventMotion.RENDERPREVPITCH = EventMotion.RENDERPITCH;
        EventMotion.RENDERPITCH = pitch;
        this.PRE = false;
    }

    public float getPrevYaw() {
        return this.prevYaw;
    }

    public float getPrevPitch() {
        return this.prevPitch;
    }

    public static float getRenderYaw() {
        return EventMotion.RENDERYAW;
    }

    public static float getRenderPitch() {
        return EventMotion.RENDERPITCH;
    }

    public static float getPrevRenderYaw() {
        return EventMotion.RENDERPREVYAW;
    }

    public static float getPrevRenderPitch() {
        return EventMotion.RENDERPREVPITCH;
    }

    public boolean isPre() {
        return this.PRE;
    }

    public boolean isPost() {
        return !this.PRE;
    }

    public double getX() {
        return this.X;
    }

    public void setX(final double posX) {
        this.X = posX;
    }

    public double getY() {
        return this.Y;
    }

    public void setY(final double posY) {
        this.Y = posY;
    }

    public double getZ() {
        return this.Z;
    }

    public void setZ(final double posZ) {
        this.Z = posZ;
    }

    public float getYaw() {
        return this.YAW;
    }

    public void setYaw(final float yaw) {
        this.YAW = yaw;
        Minecraft.getMinecraft().thePlayer.prevRenderYawOffset = EventMotion.RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = EventMotion.RENDERYAW;
        Minecraft.getMinecraft().thePlayer.prevRotationYawHead = EventMotion.RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = EventMotion.RENDERYAW;
    }

    public void setYawHead(final float yaw) {
        this.YAWHEAD = yaw;
        Minecraft.getMinecraft().thePlayer.prevRotationYawHead = EventMotion.RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = EventMotion.RENDERYAW;
    }

    public void setYawOffset(final float yaw) {
        this.YAWBODY = yaw;
        Minecraft.getMinecraft().thePlayer.prevRenderYawOffset = EventMotion.RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = EventMotion.RENDERYAW;
    }

    public float getPitch() {
        return this.PITCH;
    }

    public void setPitch(final float pitch) {
        this.PITCH = pitch;
    }

    public boolean isOnGround() {
        return this.GROUND;
    }

    public void setOnGround(final boolean ground) {
        this.GROUND = ground;
    }

    public int getType() {
        return 1;
    }
}
