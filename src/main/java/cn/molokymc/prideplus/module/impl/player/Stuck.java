package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.EventHandler;
import cn.molokymc.prideplus.event.impl.network.PacketReceiveEvent;
import cn.molokymc.prideplus.event.impl.network.PacketSendEvent;
import cn.molokymc.prideplus.event.impl.player.EventMoveInput;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.event.impl.player.UpdateEvent;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.combat.KillAura;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.client.MathUtil;
import cn.molokymc.prideplus.utils.math.Rotation;
import cn.molokymc.prideplus.utils.misc.MathUtils;
import cn.molokymc.prideplus.utils.movementfix.FallDistanceComponent;
import cn.molokymc.prideplus.utils.player.ChatUtil;
import cn.molokymc.prideplus.utils.player.PlayerUtil;
import cn.molokymc.prideplus.utils.player.ProjectileUtils;
import cn.molokymc.prideplus.utils.server.PacketUtils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.awt.*;
import java.util.Random;

public class Stuck extends Module {
    public BooleanSetting debugValue = new BooleanSetting("Debug", false);
    public NumberSetting fallDistance = new NumberSetting("FallDistance", 3, 3, 10,1);
    private CalculateThread calculateThread;
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;
    private boolean onGround;
    private Rotation rotation;
    private boolean stuck;
    private boolean throwing;
    private double x;
    private double y;
    private double z;

    public Stuck(){
        super("Stuck", "卡空", Category.PLAYER, "Auto Pearl");
        addSettings(fallDistance, debugValue);
    }

    public void onMoveInput(EventMoveInput event) {
        if (this.calculating) {
            event.setCancelled(true);
        }
    }


    public void onRender2DEvent(final Render2DEvent event) {
        if(mc.thePlayer != null  && debugValue.getValue()){
            mc.fontRendererObj.drawString("assessment: " + new ProjectileUtils.EnderPearlPredictor(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.motionY - 0.01, mc.thePlayer.motionY + 0.02).assessRotation(new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)), 20, 20, Color.WHITE.getRGB());
            mc.fontRendererObj.drawString("(" + mc.thePlayer.rotationYaw + ", " + mc.thePlayer.rotationPitch + ")", 20, 30, Color.WHITE.getRGB());

        }
    }


    public void onEnable(){
        if(mc.theWorld != null){
            this.x = mc.thePlayer.posX;
            this.y = mc.thePlayer.posY;
            this.z = mc.thePlayer.posZ;
            this.onGround = mc.thePlayer.onGround;
        }
        stuck = false;
        super.onEnable();
    }

    public void onPacketSendEvent(PacketSendEvent event) {
        if (stuck && KillAura.target == null) {
            if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                final Rotation current = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
                final float gcd = f * f * f * 1.2f;
                current.yaw -= current.yaw % gcd;
                current.pitch -= current.pitch % gcd;
                if (this.rotation.equals(current)) {
                    return;
                }
                this.rotation = current;
                event.cancel();
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.yaw, current.pitch, this.onGround));
                PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
            if (!throwing && event.getPacket() instanceof C03PacketPlayer && KillAura.target == null) {
                event.cancel();
            }
        }
    }


    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && stuck && KillAura.target == null) {
            stuck = false;
        }
    }


    public void onUpdateEvent(UpdateEvent event) {
        if (stuck) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionY = 0.0;
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.setPosition(this.x, this.y, this.z);
        }
    }


    public void onMotionEvent(MotionEvent event) {
        if (mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
        }
        if (event.isPre() && this.calculating && (this.calculateThread == null || this.calculateThread.completed) && KillAura.target == null) {
            this.calculating = false;
            if (this.calculateThread != null) {
                throwPearl(this.calculateThread.solution);
            }
        }
        final boolean overVoid = !mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30.0, true);
        if (!this.attempted && !mc.thePlayer.onGround && overVoid && FallDistanceComponent.distance > fallDistance.getValue() && KillAura.target == null) {
            FallDistanceComponent.distance = 0.0f;
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack != null && stack.getItem() instanceof ItemEnderPearl && slot >= 36) {
                    this.bestPearlSlot = slot;
                    if (this.debugValue.getValue()) {
                        ChatUtil.print(false, "Found Pearl:" + (this.bestPearlSlot - 36));
                    }
                    if (this.bestPearlSlot - 36 != -37) {
                        mc.thePlayer.inventory.currentItem = this.bestPearlSlot - 36;
                    }
                }
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            ChatUtil.print(false, String.valueOf(this.bestPearlSlot));
            if (mc.thePlayer.getHeldItem() != null) {
                if (!(mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                    return;
                }
            }
            this.calculating = true;
            (this.calculateThread = new CalculateThread(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 0.0, 0.0)).start();
            stuck = true;
            if (mc.thePlayer == null) {
                return;
            }
            this.x = mc.thePlayer.posX;
            this.y = mc.thePlayer.posY;
            this.z = mc.thePlayer.posZ;
            //this.stuck = false;
            this.onGround = mc.thePlayer.onGround;
            this.rotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float gcd = f * f * f * 1.2f;
            final Rotation rotation = this.rotation;
            rotation.pitch -= this.rotation.pitch % gcd;
            final Rotation rotation2 = this.rotation;
            rotation2.yaw -= this.rotation.yaw % gcd;
        }
    }


    public void throwPearl(final Rotation current) {
        if (!stuck) {
            return;
        }
        mc.thePlayer.rotationYaw = current.yaw;
        mc.thePlayer.rotationPitch = current.pitch;
        final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        current.yaw -= current.yaw % gcd;
        current.pitch -= current.pitch % gcd;
        throwing = true;
        if (!rotation.equals(current)) {
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.yaw, current.pitch, onGround));
        }
        rotation = current;
        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        throwing = false;
    }

    private static class CalculateThread extends Thread {
        private final ProjectileUtils.EnderPearlPredictor predictor;
        public boolean stop;
        private int iteration;
        private boolean completed;
        private double temperature;
        private double energy;
        private double solutionE;
        private Rotation solution;

        private CalculateThread(final double predictX, final double predictY, final double predictZ, final double minMotionY, final double maxMotionY) {
            this.predictor = new ProjectileUtils.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
            this.completed = false;
        }

        @Override
        public void run() {
            final TimerUtil timer = new TimerUtil();
            timer.reset();
            this.solution = new Rotation(MathUtil.getRandom(-180, 180), MathUtil.getRandom(-90, 90));
            Rotation current = this.solution;
            this.energy = this.predictor.assessRotation(this.solution);
            this.solutionE = this.energy;

            Random random = new Random();
            while (this.temperature >= 1.0E-4 && !this.stop) {
                float deltaRotationX = (float) ((float) random.nextGaussian() * this.temperature * 18.0f);
                float deltaRotationY = (float) ((float) random.nextGaussian() * this.temperature * 9.0f);

                Rotation rotation = new Rotation(current.yaw + deltaRotationX, current.pitch + deltaRotationY);
                rotation.pitch = Math.min(90.0f, Math.max(-90.0f, rotation.pitch));

                double assessment = this.predictor.assessRotation(rotation);
                double deltaE = assessment - this.energy;

                if (deltaE >= 0.0 || random.nextDouble() < Math.exp(-deltaE / (this.temperature * 100.0))) {
                    this.energy = assessment;
                    current = rotation;

                    if (assessment > this.solutionE) {
                        this.solutionE = assessment;
                        this.solution = rotation;
                        ChatUtil.print(false, "Find a better solution: (" + rotation.yaw + ", " + rotation.pitch + "), value: " + this.solutionE);
                    }
                }

                this.temperature *= 0.997;
                this.iteration++;
            }

            ChatUtil.print(false, "Simulated annealing completed within " + this.iteration + " iterations");
            ChatUtil.print(false, "Time used: " + timer.hasTimePassed(0) + " solution energy: " + this.solutionE);
            this.completed = true;
        }

    }
}

