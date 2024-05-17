package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.game.LegitTickEvent;
import cn.molokymc.prideplus.event.impl.game.LegitUpdateEvent;
import cn.molokymc.prideplus.event.impl.network.PacketEvent;
import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.UpdateEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.exploit.Disabler;
import cn.molokymc.prideplus.module.settings.Setting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.entity.RayCastUtil;
import cn.molokymc.prideplus.utils.movementfix.Rise.RotationComponent;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;


public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Hypixel", "GrimAC","Strict", "Hypixel");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, -100, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, -100, 1);
    private final NumberSetting airhorizontal = new NumberSetting("Air Horizontal", 0, 100, -100, 1);
    private final NumberSetting airvertical = new NumberSetting("Air Vertical", 0, 100, -100, 1);

    public Velocity() {
        super("AntiVelocity","无击退", Category.COMBAT, "Reduces your knockback");
        Setting.addParent(mode, m -> m.is("Strict"), horizontal, vertical, airhorizontal, airvertical);
        this.addSettings(mode, horizontal, vertical, airhorizontal, airvertical);
    }

    private boolean velocityInput = false, falling = false, attacking, C0B, lastSprinting;
    private double[] motionNoXZ;

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        Packet<?> packet = e.getPacket();
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = ((S12PacketEntityVelocity) e.getPacket());
            switch (mode.getMode()) {
                case "GrimAC":
                    if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        this.velocityInput = true;
                        KillAura killAura = Client.INSTANCE.getModuleCollection().getModule(KillAura.class);
                        if (killAura.state && KillAura.target != null) {
                            if (mc.thePlayer.getDistanceToEntity(KillAura.target) <= 3) {
                                this.attacking = true;
                                this.motionNoXZ = this.getMotionNoXZ(s12);
                                PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                            }
                        }
                    }
                    break;
                case "Strict": {
                    if (Utils.mc.thePlayer.isOnGround()) {
                        if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= horizontal.getValue().intValue() / 100;
                        s12.motionZ *= horizontal.getValue().intValue() / 100;
                        s12.motionX *= vertical.getValue().intValue() / 100;
                    } else {
                        if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionX *= (int) airvertical.getValue().intValue() / 100;
                    }
                }
                break;
                case "Hypixel":
                    if (packet instanceof S12PacketEntityVelocity) {
                        if (s12.getEntityID() == this.mc.thePlayer.getEntityId()) {
                            e.cancel();
                            if (this.mc.thePlayer.onGround || s12.motionY / 8000.0D < 0.2D || s12.motionY / 8000.0D > 0.41995D)
                                this.mc.thePlayer.motionY = s12.motionY / 8000.0D;
                        }
                        break;
                    }
                    if (e.getPacket() instanceof S27PacketExplosion) {
                        S27PacketExplosion s27 = ((S27PacketExplosion) e.getPacket());
                        switch (mode.getMode()) {
                            case "Strict": {
                                if (Utils.mc.thePlayer.onGround) {
                                    if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                                        e.cancel();
                                        return;
                                    }
                                    s27.motionX *= horizontal.getValue().intValue() / 100;
                                    s27.motionZ *= horizontal.getValue().intValue() / 100;
                                    s27.motionX *= vertical.getValue().intValue() / 100;
                                } else {
                                    if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                                        e.cancel();
                                        return;
                                    }
                                    s27.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                                    s27.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                                    s27.motionX *= (int) airvertical.getValue().intValue() / 100;
                                }
                            }
                            break;
                        }
                    }
            }
        }
    }

    @Override
    public void onLegitUpdateEvent(LegitUpdateEvent e) {
        if (e.isPost()) {
            if (C0B) {
                C0B = false;
                if (!mc.thePlayer.isSprinting()) {
                    PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
        }
        if (e.isPre()) {
            if (mc.thePlayer.fallDistance > 3) {
                falling = true;
            }
            if (falling) {
                if (!mc.thePlayer.velocityChanged && mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0) {
                    falling = false;
                }
            }
        }
    }

    @Override
    public void onLegitTickEvent(LegitTickEvent e) {
        KillAura killAura = (KillAura) Client.INSTANCE.getModuleCollection().get(KillAura.class);
        if (mode.is("GrimAC")) {
            if (this.velocityInput) {
                if (this.attacking) {
                    if (killAura.state && KillAura.target != null) {
                        if (mc.thePlayer.getDistanceToEntity(KillAura.target) <= killAura.reach.getValue()) {
                            if (RayCastUtil.rayCast(RotationComponent.rotations, 3.5).entityHit == KillAura.target) {
                                for (int i = 0; i < 13; i++) {
                                    PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(KillAura.target, C02PacketUseEntity.Action.ATTACK));
                                    PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
                                }
                                mc.thePlayer.motionX = this.motionNoXZ[0];
                                mc.thePlayer.motionZ = this.motionNoXZ[1];
                                this.attacking = false;
                                C0B = true;
                            }

                        } else if (mc.thePlayer.getDistanceToEntity(KillAura.target) <= 3.0D) {
                            for (int i = 0; i < 13; i++) {
                                PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(KillAura.target, C02PacketUseEntity.Action.ATTACK));
                                PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
                            }
                            mc.thePlayer.motionX = this.motionNoXZ[0];
                            mc.thePlayer.motionZ = this.motionNoXZ[1];
                            this.attacking = false;
                            C0B = true;
                        }
                    }
                } else if (mc.thePlayer.hurtTime == 6 && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.movementInput.jump = true;
                }
                if (mc.thePlayer.hurtTime == 0) {
                    this.velocityInput = false;
                }
            }
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (mode.is("Grim") && Client.INSTANCE.getModuleCollection().get(Disabler.class).isEnabled() && Disabler.disablers.isEnabled("Grim")) {
            if (packet instanceof C0BPacketEntityAction) {
                if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                    if (this.lastSprinting) {
                        event.setCancelled(true);
                    }
                    this.lastSprinting = true;
                } else if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    if (!this.lastSprinting) {
                        event.setCancelled(true);
                    }
                    this.lastSprinting = false;
                }
            }
        }
    }

    private double[] getMotionNoXZ(final S12PacketEntityVelocity packetEntityVelocity) {
        final double strength = MathHelper.sqrt_double((packetEntityVelocity.getMotionX() / 8000f) * (packetEntityVelocity.getMotionX() / 8000f) + (packetEntityVelocity.getMotionZ() / 8000f) * (packetEntityVelocity.getMotionZ() / 8000f));
        double motionNoX, motionNoZ;

        if (strength >= 3) {
            motionNoX = (packetEntityVelocity.getMotionX() / 8000f) * 0.08;
            motionNoZ = (packetEntityVelocity.getMotionZ() / 8000f) * 0.08;
            mc.thePlayer.motionY = packetEntityVelocity.getMotionY() / 8000f;
        } else if (strength >= 1) {
            if (mc.thePlayer.onGround) {
                motionNoX = (packetEntityVelocity.getMotionX() / 8000f) * 0.045;
                motionNoZ = (packetEntityVelocity.getMotionZ() / 8000f) * 0.045;
                mc.thePlayer.motionY = packetEntityVelocity.getMotionY() / 21000f;
            }
            else {
                motionNoX = (packetEntityVelocity.getMotionX() / 8000f) * 0.035;
                motionNoZ = (packetEntityVelocity.getMotionZ() / 8000f) * 0.035;
                mc.thePlayer.motionY = packetEntityVelocity.getMotionY() / 38000f;
            }
        } else {
            if (mc.thePlayer.onGround) {
                motionNoX = (packetEntityVelocity.getMotionX() / 8000f) * 0.005;
                motionNoZ = (packetEntityVelocity.getMotionZ() / 8000f) * 0.005;
            }
            else {
                motionNoX = (packetEntityVelocity.getMotionX() / 8000f) * 0.008;
                motionNoZ = (packetEntityVelocity.getMotionZ() / 8000f) * 0.008;
            }
            mc.thePlayer.motionY = packetEntityVelocity.getMotionY() / 55000f;

        }
        return new double[]{motionNoX, motionNoZ};
    }
}
