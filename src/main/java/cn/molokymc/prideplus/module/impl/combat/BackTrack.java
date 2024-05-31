package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.game.TickEvent;
import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.event.impl.render.Render3DEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Vec3;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.time.TimeUtil;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;

public final class BackTrack extends Module
{
    private final ArrayList<Packet<?>> packets;
    private EntityLivingBase entity;
    private INetHandler packetListener;
    public final NumberSetting hitRange;
    public final NumberSetting timerDelay;
    public final BooleanSetting esp;
    public final BooleanSetting onlyWhenNeed;
    public final BooleanSetting targets;
    public final BooleanSetting player;
    public final BooleanSetting mob;
    public final BooleanSetting animal;
    public final BooleanSetting villager;
    public final BooleanSetting armorStand;
    public final BooleanSetting onlyKillAura;
    public final NumberSetting range;
    public final BooleanSetting packetsToDelay;
    public final BooleanSetting packetVelocity;
    public final BooleanSetting packetVelocityExplosion;
    public final BooleanSetting packetTimeUpdate;
    public final BooleanSetting packetKeepAlive;
    TimeUtil timeHelper;
    private boolean blockPackets;
    private WorldClient lastWorld;

    public BackTrack() {
        super("BackTrack","回溯", Category.COMBAT,"huisu");
        this.packets = new ArrayList<Packet<?>>();
        this.entity = null;
        this.packetListener = null;
        this.hitRange = new NumberSetting("MaxHitRange", 6.0, 3.0, 8.0, 3.0);
        this.timerDelay = new NumberSetting("Time", 4000.0, 0.0, 30000.0, 4000.0);
        this.esp = new BooleanSetting("Esp", true);
        this.onlyWhenNeed = new BooleanSetting("OnlyWhenNeed", true);
        this.targets = new BooleanSetting("Targets", true);
        this.player = new BooleanSetting("Player", true);
        this.mob = new BooleanSetting("Mob", true);
        this.animal = new BooleanSetting("Animal", true);
        this.villager = new BooleanSetting("Villager", true);
        this.armorStand = new BooleanSetting("ArmorStand", true);
        this.onlyKillAura = new BooleanSetting("OnlyKillAura", true);
        this.range = new NumberSetting("PreAimRange", 4.0, 0.0, 15.0, 0.1);
        this.packetsToDelay = new BooleanSetting("PacketsToDelay", true);
        this.packetVelocity = new BooleanSetting("Velocity", true);
        this.packetVelocityExplosion = new BooleanSetting("ExplosionVelocity", true);
        this.packetTimeUpdate = new BooleanSetting("TimeUpdate", true);
        this.packetKeepAlive = new BooleanSetting("KeepAlive", true);
        this.timeHelper = new TimeUtil();
    }

    public void onEnable() {
        this.blockPackets = false;
        if (BackTrack.mc.theWorld != null && BackTrack.mc.thePlayer != null) {
            for (final Entity entity : BackTrack.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                    entityLivingBase.realPosX = entityLivingBase.serverPosX;
                    entityLivingBase.realPosZ = entityLivingBase.serverPosZ;
                    entityLivingBase.realPosY = entityLivingBase.serverPosY;
                }
            }
        }
        super.onEnable();
    }

    @Override
    public void onTickEvent(final TickEvent event) {
        if (((KillAura) Client.INSTANCE.getModuleCollection().getModule((Class)KillAura.class)).getState()) {
            this.entity = KillAura.target;
        }
        else {
            final Object[] listOfTargets = BackTrack.mc.theWorld.loadedEntityList.stream().filter(this::canAttacked).sorted(Comparator.comparingDouble(entity -> BackTrack.mc.thePlayer.getDistanceToEntity(entity))).toArray();
            if (listOfTargets.length > 0) {
                this.entity = (EntityLivingBase)listOfTargets[0];
            }
            if (this.onlyKillAura.getValue()) {
                this.entity = null;
            }
        }
        if (this.entity != null && BackTrack.mc.thePlayer != null && this.packetListener != null && BackTrack.mc.theWorld != null) {
            final double d0 = this.entity.realPosX / 32.0;
            final double d2 = this.entity.realPosY / 32.0;
            final double d3 = this.entity.realPosZ / 32.0;
            final double d4 = this.entity.serverPosX / 32.0;
            final double d5 = this.entity.serverPosY / 32.0;
            final double d6 = this.entity.serverPosZ / 32.0;
            final float f = this.entity.width / 2.0f;
            final AxisAlignedBB entityServerPos = new AxisAlignedBB(d4 - f, d5, d6 - f, d4 + f, d5 + this.entity.height, d6 + f);
            final Vec3 positionEyes = (Vec3) BackTrack.mc.thePlayer.getPositionEyes(BackTrack.mc.timer.renderPartialTicks);
            final double currentX = MathHelper.clamp_double(positionEyes.xCoord, entityServerPos.minX, entityServerPos.maxX);
            final double currentY = MathHelper.clamp_double(positionEyes.yCoord, entityServerPos.minY, entityServerPos.maxY);
            final double currentZ = MathHelper.clamp_double(positionEyes.zCoord, entityServerPos.minZ, entityServerPos.maxZ);
            final AxisAlignedBB entityPosMe = new AxisAlignedBB(d0 - f, d2, d3 - f, d0 + f, d2 + this.entity.height, d3 + f);
            final double realX = MathHelper.clamp_double(positionEyes.xCoord, entityPosMe.minX, entityPosMe.maxX);
            final double realY = MathHelper.clamp_double(positionEyes.yCoord, entityPosMe.minY, entityPosMe.maxY);
            final double realZ = MathHelper.clamp_double(positionEyes.zCoord, entityPosMe.minZ, entityPosMe.maxZ);
            double distance = this.hitRange.getValue();
            if (!BackTrack.mc.thePlayer.canEntityBeSeen((Entity)this.entity)) {
                distance = Math.min(distance, 3.0);
            }
            final double collision = this.entity.getCollisionBorderSize();
            final double width = BackTrack.mc.thePlayer.width / 2.0f;
            final double mePosXForPlayer = BackTrack.mc.thePlayer.getLastServerPosition().xCoord + (BackTrack.mc.thePlayer.getSeverPosition().xCoord - BackTrack.mc.thePlayer.getLastServerPosition().xCoord) / MathHelper.clamp_int(BackTrack.mc.thePlayer.rotIncrement, 1, 3);
            final double mePosYForPlayer = BackTrack.mc.thePlayer.getLastServerPosition().yCoord + (BackTrack.mc.thePlayer.getSeverPosition().yCoord - BackTrack.mc.thePlayer.getLastServerPosition().yCoord) / MathHelper.clamp_int(BackTrack.mc.thePlayer.rotIncrement, 1, 3);
            final double mePosZForPlayer = BackTrack.mc.thePlayer.getLastServerPosition().zCoord + (BackTrack.mc.thePlayer.getSeverPosition().zCoord - BackTrack.mc.thePlayer.getLastServerPosition().zCoord) / MathHelper.clamp_int(BackTrack.mc.thePlayer.rotIncrement, 1, 3);
            AxisAlignedBB mePosForPlayerBox = new AxisAlignedBB(mePosXForPlayer - width, mePosYForPlayer, mePosZForPlayer - width, mePosXForPlayer + width, mePosYForPlayer + BackTrack.mc.thePlayer.height, mePosZForPlayer + width);
            mePosForPlayerBox = mePosForPlayerBox.expand(collision, collision, collision);
            final Vec3 entityPosEyes = new Vec3(d4, d5 + this.entity.getEyeHeight(), d6);
            final double bestX = MathHelper.clamp_double(entityPosEyes.xCoord, mePosForPlayerBox.minX, mePosForPlayerBox.maxX);
            final double bestY = MathHelper.clamp_double(entityPosEyes.yCoord, mePosForPlayerBox.minY, mePosForPlayerBox.maxY);
            final double bestZ = MathHelper.clamp_double(entityPosEyes.zCoord, mePosForPlayerBox.minZ, mePosForPlayerBox.maxZ);
            boolean b = entityPosEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 3.0 || (BackTrack.mc.thePlayer.hurtTime < 8 && BackTrack.mc.thePlayer.hurtTime > 3);
            if (!this.onlyWhenNeed.getValue()) {
                b = true;
            }
            if (b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) && BackTrack.mc.thePlayer.getSeverPosition().distanceTo(new Vec3(d0, d2, d3)) < distance && !this.timeHelper.hasPassed(this.timerDelay.getValue().longValue())) {
                this.blockPackets = true;
            }
            else {
                this.blockPackets = false;
                this.resetPackets(this.packetListener);
                this.timeHelper.reset();
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getNetHandler() != null) {
            this.packetListener = event.getNetHandler();
        }
        if (event.getDirection() != EnumPacketDirection.CLIENTBOUND) {
            return;
        }
        final Packet<?> p = (Packet<?>)event.getPacket();
        if (p instanceof S08PacketPlayerPosLook) {
            this.resetPackets(event.getNetHandler());
        }
        if (p instanceof S14PacketEntity) {
            final S14PacketEntity packet = (S14PacketEntity)p;
            final Entity entity1 = BackTrack.mc.theWorld.getEntityByID(packet.entityId);
            if (entity1 instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase3;
                final EntityLivingBase entityLivingBase5;
                final EntityLivingBase entityLivingBase2 = entityLivingBase5 = (entityLivingBase3 = (EntityLivingBase)entity1);
                entityLivingBase5.realPosX += packet.posX;
                final EntityLivingBase entityLivingBase6 = entityLivingBase3;
                entityLivingBase6.realPosY += packet.posY;
                final EntityLivingBase entityLivingBase7 = entityLivingBase3;
                entityLivingBase7.realPosZ += packet.posZ;
            }
        }
        if (p instanceof S18PacketEntityTeleport) {
            final S18PacketEntityTeleport packet2 = (S18PacketEntityTeleport)p;
            final Entity entity1 = BackTrack.mc.theWorld.getEntityByID(packet2.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase4 = (EntityLivingBase)entity1;
                entityLivingBase4.realPosX = packet2.getX();
                entityLivingBase4.realPosY = packet2.getY();
                entityLivingBase4.realPosZ = packet2.getZ();
            }
        }
        if (this.entity == null) {
            this.resetPackets(event.getNetHandler());
            return;
        }
        if (BackTrack.mc.theWorld != null && BackTrack.mc.thePlayer != null) {
            if (this.lastWorld != BackTrack.mc.theWorld) {
                this.resetPackets(event.getNetHandler());
                this.lastWorld = BackTrack.mc.theWorld;
                return;
            }
            this.addPackets(p, event);
        }
        this.lastWorld = BackTrack.mc.theWorld;
    }

    private boolean canAttacked(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (entity.isInvisible()) {
                return false;
            }
            if (((EntityLivingBase)entity).deathTime > 1) {
                return false;
            }
            if (entity instanceof EntityArmorStand && !this.armorStand.getValue() && this.targets.getValue()) {
                return false;
            }
            if (entity instanceof EntityAnimal && !this.animal.getValue() && this.targets.getValue()) {
                return false;
            }
            if (entity instanceof EntityMob && !this.mob.getValue() && this.targets.getValue()) {
                return false;
            }
            if (entity instanceof EntityPlayer && !this.player.getValue() && this.targets.getValue()) {
                return false;
            }
            if (entity instanceof EntityVillager && !this.villager.getValue() && this.targets.getValue()) {
                return false;
            }
            if (entity.ticksExisted < 50) {
                return false;
            }
            //if (entity instanceof EntityPlayer && ((Teams)Client.INSTANCE.getModuleCollection().getModule((Class) Teams.class)).getState() && Teams.isSameTeam(entity)) {
            //    return false;
            //}
            //if (entity instanceof EntityPlayer && (entity.getName().equals("��aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES") || entity.getName().equals("商店"))) {
            //    return false;
            //}
            if (entity.isDead) {
                return false;
            }
            //if (entity instanceof EntityPlayer && Antibot.isServerBot(entity)) {
            //    return false;
            //}
        }
        return entity instanceof EntityLivingBase && entity != BackTrack.mc.thePlayer && BackTrack.mc.thePlayer.getDistanceToEntity(entity) < this.range.getValue();
    }

    @Override
    public void onRender3DEvent(final Render3DEvent event) {
        if (this.esp.getValue()) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
            if (this.entity != null && this.blockPackets) {
                this.render(this.entity);
            }
            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
        }
    }

    private void render(final EntityLivingBase entity) {
        final float red = 0.0f;
        final float green = 1.1333333f;
        final float blue = 0.0f;
        float lineWidth = 3.0f;
        final float alpha = 0.03137255f;
        if (BackTrack.mc.thePlayer.getDistanceToEntity((Entity)entity) > 1.0f) {
            double d0 = 1.0f - BackTrack.mc.thePlayer.getDistanceToEntity((Entity)entity) / 20.0f;
            if (d0 < 0.3) {
                d0 = 0.3;
            }
            lineWidth *= (float)d0;
        }
        RenderUtil.drawEntityServerESP((Entity)entity, 0.0f, 1.1333333f, 0.0f, 0.03137255f, 1.0f, lineWidth);
    }

    private void resetPackets(final INetHandler netHandler) {
        if (this.packets.size() > 0) {
            while (this.packets.size() != 0) {
                final Packet packet = this.packets.get(0);
                try {
                    if (packet != null) {
                            packet.processPacket(netHandler);
                        }
                }
                catch (ThreadQuickExitException ex) {}
                this.packets.remove(this.packets.get(0));
            }
        }
    }

    private void addPackets(final Packet<?> packet, final PacketReceiveEvent eventReadPacket) {
        synchronized (this.packets) {
            if (this.delayPackets(packet)) {
                this.packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    private boolean delayPackets(final Packet<?> packet) {
        if (BackTrack.mc.currentScreen != null) {
            return false;
        }
        if (packet instanceof S03PacketTimeUpdate && this.packetsToDelay.getValue()) {
            return this.packetTimeUpdate.getValue();
        }
        if (packet instanceof S00PacketKeepAlive && this.packetsToDelay.getValue()) {
            return this.packetKeepAlive.getValue();
        }
        if (packet instanceof S12PacketEntityVelocity && this.packetsToDelay.getValue()) {
            return this.packetVelocity.getValue();
        }
        if (packet instanceof S27PacketExplosion && this.packetsToDelay.getValue()) {
            return this.packetVelocityExplosion.getValue();
        }
        if (packet instanceof S19PacketEntityStatus && this.packetsToDelay.getValue()) {
            final S19PacketEntityStatus entityStatus = (S19PacketEntityStatus)packet;
            return entityStatus.getOpCode() != 2 || !(BackTrack.mc.theWorld.getEntityByID(entityStatus.getEntityId()) instanceof EntityLivingBase);
        }
        return !(packet instanceof S06PacketUpdateHealth) && !(packet instanceof S29PacketSoundEffect) && !(packet instanceof S3EPacketTeams) && !(packet instanceof S0CPacketSpawnPlayer);
    }
}
