package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.viamcp.ViaMCP;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.commands.impl.FriendCommand;
import cn.molokymc.prideplus.event.impl.player.*;
import cn.molokymc.prideplus.event.impl.render.Render3DEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.exploit.Teams;
import cn.molokymc.prideplus.module.impl.movement.Scaffold;
import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.module.settings.impl.*;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.math.MathUtil;
import cn.molokymc.prideplus.utils.math.Vector2f;
import cn.molokymc.prideplus.utils.misc.MathUtils;
import cn.molokymc.prideplus.utils.movementfix.MovementFix;
import cn.molokymc.prideplus.utils.movementfix.Rise.RotationComponent;
import cn.molokymc.prideplus.utils.player.InventoryUtils;
import cn.molokymc.prideplus.utils.player.RotationUtils;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import lombok.SneakyThrows;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;


import java.awt.*;
import java.util.List;
import java.util.*;


public final class KillAura extends Module {
    public static boolean attacking;
    public static boolean blocking;
    public static boolean wasBlocking;
    public static float yaw = 0.0F;
    private int cps;
    public static EntityLivingBase target;

    public static final List<EntityLivingBase> targets = new ArrayList();
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil switchTimer = new TimerUtil();
    private final MultipleBoolSetting targetsSetting = new MultipleBoolSetting("Targets",
             new BooleanSetting("Players", true),
             new BooleanSetting("Animals", false),
             new BooleanSetting("Mobs", false),
             new BooleanSetting("Invisibles", false),
             new BooleanSetting("Villagers", false));
    private final ModeSetting mode = new ModeSetting("Mode", "Single", "Single", "Switch", "Multi");
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 50.0, 500.0, 1.0, 1.0);
    private final NumberSetting maxTargetAmount = new NumberSetting("Max Target Amount", 3.0, 50.0, 2.0, 1.0);
    private final NumberSetting minCPS = new NumberSetting("Min CPS", 10.0, 20.0, 1.0, 1.0);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 10.0, 20.0, 1.0, 1.0);
    public final NumberSetting reach = new NumberSetting("Reach", 4.0, 6.0, 3.0, 0.1);
    private final NumberSetting maxrotationspeed = new NumberSetting("Max Rotation Speed", 100.0, 100.0, 1.0, 1.0);
    private final NumberSetting minrotationspeed = new NumberSetting("Min Rotation Speed", 100.0, 100.0, 1.0, 1.0);
    private final BooleanSetting strafefix = new BooleanSetting("Strafe Fix", true);
    private final BooleanSetting autoblock = new BooleanSetting("Autoblock", false);
    private final ModeSetting autoblockMode = new ModeSetting("Autoblock Mode", "Watchdog","Fake", "Watchdog", "Grim");
    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation speed", 5, 10, 5, 1);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "HvH","HvH", "Vanilla", "Custom");
    private final ModeSetting sortMode = new ModeSetting("Sort Mode", "Range","Range", "Hurt Time", "Health", "Armor");
    private final BooleanSetting whileUsingBreaker = new BooleanSetting("While using breaker", false);
    private final MultipleBoolSetting addons = new MultipleBoolSetting("Addons",
             new BooleanSetting("Keep Sprint", true),
             new BooleanSetting("Through Walls", true),
             new BooleanSetting("Allow Scaffold", false),
             new BooleanSetting("Movement Fix", false),
             new BooleanSetting("Ray Cast", false));
    private final MultipleBoolSetting auraESP = new MultipleBoolSetting("Target ESP",
             new BooleanSetting("Circle", true),
             new BooleanSetting("Icarus", false),
             new BooleanSetting("Tracer", false),
             new BooleanSetting("Box", true),
             new BooleanSetting("Custom Color", false));
    private final ColorSetting customColor;
    private EntityLivingBase auraESPTarget;
    private final Animation auraESPAnim;

    public KillAura() {
        super("KillAura","杀戮光环", Category.COMBAT, "Automatically attacks players");
        this.customColor = new ColorSetting("Custom Color", Color.WHITE);
        this.auraESPAnim = new DecelerateAnimation(350, 1.0);
        this.autoblockMode.addParent(this.autoblock, (a) -> this.autoblock.isEnabled());
        this.rotationMode.addParent(this.rotations, (r) -> this.rotations.isEnabled());
        this.switchDelay.addParent(this.mode, (m) -> this.mode.is("Switch"));
        this.maxTargetAmount.addParent(this.mode, (m) -> this.mode.is("Multi"));
        this.customColor.addParent(this.auraESP, (r) -> this.auraESP.isEnabled("Custom Color"));
        this.maxrotationspeed.addParent(this.rotationMode, (rotationMode) -> rotationMode.is("Custom"));
        this.minrotationspeed.addParent(this.rotationMode, (rotationMode) -> rotationMode.is("Custom"));
        this.rotationSpeed.addParent(this.rotationMode, (rotationMode) -> rotationMode.is("HvH"));
        this.addSettings(this.targetsSetting, this.mode, this.strafefix, this.maxTargetAmount,
                this.switchDelay, this.minCPS, this.maxCPS,
                this.reach, this.autoblock, this.autoblockMode,
                this.rotations, this.rotationSpeed, this.maxrotationspeed,
                this.minrotationspeed, this.rotationMode,
                this.sortMode, this.addons, this.auraESP, this.customColor);
    }
    public void onEnable() {
        target = null;
        targets.clear();
        super.onEnable();
    }
    private void attack() {
        if (KillAura.target != null) {
            this.attackEntity(KillAura.target);
            if (KillAura.mc.thePlayer.fallDistance > 0.0f && !KillAura.mc.thePlayer.onGround && !KillAura.mc.thePlayer.isOnLadder() && !KillAura.mc.thePlayer.isInWater() && !KillAura.mc.thePlayer.isPotionActive(Potion.blindness) && KillAura.mc.thePlayer.ridingEntity == null) {
                KillAura.mc.thePlayer.onCriticalHit(KillAura.target);
            }
            if (EnchantmentHelper.getModifierForCreature(KillAura.mc.thePlayer.getHeldItem(), KillAura.target.getCreatureAttribute()) > 0.0f) {
                KillAura.mc.thePlayer.onEnchantmentCritical(KillAura.target);
            }
        }
    }
    private void attackEntity(final Entity target) {
        if (ViaMCP.getInstance().getVersion() <= 47) {
            mc.thePlayer.swingItem();
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        } else {
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            mc.thePlayer.swingItem();
        }
        //AttackOrder.sendFixedAttack((EntityPlayer) KillAura.mc.thePlayer, target);
        this.attackTimer.reset();
    }
    public void onDisable() {
        target = null;
        targets.clear();
        blocking = false;
        attacking = false;
        if (wasBlocking) {
            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }

        wasBlocking = false;
        super.onDisable();
    }
    @SneakyThrows
    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());
        if (minCPS.getValue() > maxCPS.getValue()) {
            minCPS.setValue(minCPS.getValue() - 1);
        }
        // Gets all entities in specified range, sorts them using your specified sort mode, and adds them to target list\
        sortTargets();
        if (target == null) {
            yaw = mc.thePlayer.rotationYaw;
        }
        if (event.isPost()) {
            if (blocking) {
                switch (autoblockMode.getMode()) {
                    case "Watchdog":
                        if (event.isPre()) {
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem.write(Type.VAR_INT, 1);
                            useItem.sendToServer(Protocol1_8TO1_9.class, true);
                            wasBlocking = true;
                        }
                        break;
                    case "Grim":
                        if (event.isPre()) {
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());

                            PacketWrapper use_0 = PacketWrapper.create(29, null,
                                    Via.getManager().getConnectionManager().getConnections().iterator().next());
                            use_0.write(Type.VAR_INT, 0);
                            PacketUtil.sendToServer(use_0, Protocol1_8TO1_9.class, true, true);

                            PacketWrapper use_1 = PacketWrapper.create(29, null,
                                    Via.getManager().getConnectionManager().getConnections().iterator().next());
                            use_1.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(use_1, Protocol1_8TO1_9.class, true, true);
                            mc.gameSettings.keyBindUseItem.pressed = true;
                            wasBlocking = true;
                        }
                        break;
                    case "Fake":
                        break;
                }
            } else if (wasBlocking && autoblockMode.is("Grim") && KillAura.target == null) {
                mc.gameSettings.keyBindUseItem.pressed = false;
                wasBlocking = false;
            }
        }
        attacking = !targets.isEmpty() && (addons.getSetting("Allow Scaffold").isEnabled() || !Pride.INSTANCE.isEnabled(Scaffold.class));
        blocking = autoblock.isEnabled() && attacking && InventoryUtils.isHoldingSword();
        if (attacking) {
            KillAura.target = targets.get(0);

            if (rotations.isEnabled()) {
                float[] rotations = new float[]{0, 0};
                final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
                final double maxRotationSpeed = this.rotationSpeed.getValue().doubleValue();
                final float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);
                switch (rotationMode.getMode()) {
                    case "HvH":
                        rotations = RotationUtils.getHVHRotation(target, this.reach.getValue() + 0.1);
                        break;
                    case "Vanilla":
                        rotations = RotationUtils.getRotationsNeeded(KillAura.target);
                        break;
                    case "Smooth":
                        rotations = RotationUtils.getSmoothRotations(KillAura.target);
                        break;
                }

                RotationComponent.setRotations(new Vector2f(rotations[0], rotations[1]), rotationSpeed, addons.getSetting("Movement Fix").getValue() ? MovementFix.NORMAL : MovementFix.OFF);
            }
            if (addons.getSetting("Ray Cast").isEnabled() && !RotationUtils.isMouseOver(event.getYaw(), event.getPitch(), KillAura.target, reach.getValue().floatValue()))
                return;
            final int maxValue = (int) ((minCPS.getMaxValue() - maxCPS.getValue()) * 20);
            final int minValue = (int) ((minCPS.getMaxValue() - minCPS.getValue()) * 20);
            cps = MathUtils.getRandomInRange(minValue, maxValue);
            if (mode.is("Multi")) {
                for (EntityLivingBase entityLivingBase : targets) {
                    AttackEvent attackEvent = new AttackEvent(entityLivingBase);
                    Pride.INSTANCE.getEventProtocol().handleEvent(attackEvent);

                    if (!attackEvent.isCancelled()) {
                        //AttackOrder.sendFixedAttack(mc.thePlayer, entityLivingBase);
                        if (ViaMCP.getInstance().getVersion() <= 47) {
                            mc.thePlayer.swingItem();
                            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        } else {
                            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                            mc.thePlayer.swingItem();
                        }
                    }
                }
            } else {
                AttackEvent attackEvent = new AttackEvent(KillAura.target);
                Pride.INSTANCE.getEventProtocol().handleEvent(attackEvent);

                attack();
            }
        } else {
            attackTimer.reset();
            KillAura.target = null;
        }
    }

    private void sortTargets() {
        targets.clear();
        Iterator var1 = mc.theWorld.getLoadedEntityList().iterator();

        while(var1.hasNext()) {
            Entity entity = (Entity)var1.next();
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                if ((double)mc.thePlayer.getDistanceToEntity(entity) <= this.reach.getValue() && this.isValid(entity) && mc.thePlayer != entityLivingBase && !FriendCommand.isFriend(entityLivingBase.getName())) {
                    targets.add(entityLivingBase);
                }
            }
        }

        switch (this.sortMode.getMode()) {
            case "Range":
                List var10000 = targets;
                EntityPlayerSP var10001 = mc.thePlayer;
                var10001.getClass();
                var10000.sort(Comparator.comparingDouble(var10001::getDistanceToEntity));
                break;
            case "Hurt Time":
                targets.sort(Comparator.comparingInt(EntityLivingBase::getHurtTime));
                break;
            case "Health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "Armor":
                targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
        }

    }

    public boolean isValid(Entity entity) {
        if (entity instanceof EntityPlayer && !this.targetsSetting.getSetting("Players").isEnabled()) {
            return false;
        } else if (entity instanceof EntityVillager && !this.targetsSetting.getSetting("Villagers").isEnabled()) {
            return false;
        } else if (entity instanceof EntityAnimal && !this.targetsSetting.getSetting("Animals").isEnabled()) {
            return false;
        } else if (entity instanceof EntityMob && !this.targetsSetting.getSetting("Mobs").isEnabled()) {
            return false;
        } else if (entity.isInvisible() && !this.targetsSetting.getSetting("Invisibles").isEnabled()) {
            return false;
        } else if (!mc.thePlayer.canEntityBeSeen(entity) && !this.addons.getSetting("Through Walls").isEnabled()) {
            return false;
        } else {
            return !Pride.INSTANCE.isEnabled(Teams.class) || !Teams.isOnSameTeam(entity);
        }
    }

    public double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0F;
        }

        float forward = 1.0F;
        if (moveForward < 0.0) {
            forward = -0.5F;
        } else if (moveForward > 0.0) {
            forward = 0.5F;
        }

        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0F * forward;
        }

        if (moveStrafing < 0.0) {
            rotationYaw += 90.0F * forward;
        }

        return Math.toRadians((double)rotationYaw);
    }

    public void onKeepSprintEvent(KeepSprintEvent event) {
        if (this.addons.getSetting("Keep Sprint").isEnabled()) {
            event.cancel();
        }

    }

    public void onRender3DEvent(Render3DEvent event) {
        this.auraESPAnim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);
        if (target != null) {
            this.auraESPTarget = target;
        }

        if (this.auraESPAnim.finished(Direction.BACKWARDS)) {
            this.auraESPTarget = null;
        }

        Color color = (Color)HUDMod.getClientColors().getFirst();
        if (this.auraESP.isEnabled("Custom Color")) {
            color = this.customColor.getColor();
        }

        if (this.auraESPTarget != null) {
            if (this.auraESP.getSetting("Box").isEnabled()) {
                RenderUtil.renderBoundingBox(this.auraESPTarget, color, this.auraESPAnim.getOutput().floatValue());
            }
            if (this.auraESP.getSetting("Icarus").isEnabled()) {
                RenderUtil.drawIcarusESP(this.auraESPTarget, color, this.auraESPAnim.getOutput().floatValue());
            }
            if (this.auraESP.getSetting("Circle").isEnabled()) {
                RenderUtil.drawCircle(this.auraESPTarget, event.getTicks(), 0.75, color.getRGB(), this.auraESPAnim.getOutput().floatValue());
            }

            if (this.auraESP.getSetting("Tracer").isEnabled()) {
                RenderUtil.drawTracerLine(this.auraESPTarget, 4.0F, Color.BLACK, this.auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(this.auraESPTarget, 2.5F, color, this.auraESPAnim.getOutput().floatValue());
            }
        }

    }
}
