package cn.molokymc.prideplus.module.impl.movement;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.Vec3;
import cn.molokymc.prideplus.utils.player.MovementUtils;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;



import java.util.concurrent.ThreadLocalRandom;


public final class Speed extends Module {
        private final ModeSetting mode = new ModeSetting("Mode", "Watchdog",
                "Watchdog", "Strafe", "Vanilla", "BHop");
    private final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", "GroundStrafe", "Strafe", "GroundStrafe", "Glide Strafe", "Low-Jump");
    private final NumberSetting groundSpeed = new NumberSetting("Ground Speed", 2, 5, 1, 0.1);
    private final NumberSetting timer = new NumberSetting("Timer", 1, 5, 1, 0.1);
    private final NumberSetting vanillaSpeed = new NumberSetting("Speed", 1, 10, 1, 0.1);

    private final TimerUtil timerUtil = new TimerUtil();
    private final float r = ThreadLocalRandom.current().nextFloat();
    private double speed, lastDist;
    private float speedChangingDirection;
    private int stage;
    private boolean strafe, wasOnGround;
    private boolean setTimer = true;
    private double moveSpeed;
    private int inAirTicks;
    int onGroundticks = 0;
    int t;

    public Speed() {
        super("Speed","加速", Category.MOVEMENT, "Makes you go faster");
        watchdogMode.addParent(mode, modeSetting -> modeSetting.is("Watchdog"));
        groundSpeed.addParent(watchdogMode, modeSetting -> modeSetting.is("Ground") && mode.is("Watchdog"));
        vanillaSpeed.addParent(mode, modeSetting -> modeSetting.is("Vanilla") || modeSetting.is("BHop"));
        this.addSettings(mode, vanillaSpeed, watchdogMode, groundSpeed, timer);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if(mode.equals("Watchdog")){
            this.setSuffix(watchdogMode.getMode());
        }else{
            this.setSuffix(mode.getMode());
        }
        if (setTimer) {
            mc.timer.timerSpeed = timer.getValue().floatValue();
        }

        double distX = e.getX() - mc.thePlayer.prevPosX, distZ = e.getZ() - mc.thePlayer.prevPosZ;
        lastDist = Math.hypot(distX, distZ);

        switch (mode.getMode()) {
            case "Vanilla":
                if (MovementUtils.isMoving()) {
                    MovementUtils.setSpeed(vanillaSpeed.getValue() / 4);
                }
                break;
            case "BHop":
                if (MovementUtils.isMoving()) {
                    MovementUtils.setSpeed(vanillaSpeed.getValue() / 4);
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                }
                break;
            case "Strafe":
                if (e.isPre() && MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    } else {
                        MovementUtils.setSpeed(MovementUtils.getSpeed());
                    }
                }
                break;
            case "Watchdog":
                mc.gameSettings.keyBindJump.pressed = false;
                if (watchdogMode.is("Strafe")) {
                        if (e.isPre() && MovementUtils.isMoving()) {
                            if (Math.abs(mc.thePlayer.movementInput.moveStrafe) < 0.1F) {
                                mc.thePlayer.jumpMovementFactor = 0.022499F;
                            } else {
                                mc.thePlayer.jumpMovementFactor = 0.0234F;
                            }
                            mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump);
                            if (MovementUtils.getSpeed() < 0.21F && !mc.thePlayer.onGround) {
                                MovementUtils.Strafe(0.22F);
                            }
                            if (mc.thePlayer.onGround) {
                                mc.gameSettings.keyBindJump.pressed = false;
                                mc.thePlayer.motionY = 0.0008;
                                mc.thePlayer.jump();
                                if (mc.thePlayer.isAirBorne) {
                                    MovementUtils.strafe(0.45);
                                    if (MovementUtils.getSpeed() < (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.55F : 0.5F))
                                        MovementUtils.Strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.5349F : 0.4849F);
                                }
                            }
                        }
                } else if (watchdogMode.is("Low-Jump") && mc.thePlayer.onGround) {
                    if (MovementUtils.isMoving()) {
                        mc.thePlayer.motionY = 0.008;
                        MovementUtils.strafe(0.21f);
                    } else {
                            mc.thePlayer.jump();
                    }
                }  else if(watchdogMode.is("Glide Strafe")) {
                        if(MovementUtils.isMoving()) {
                            if((mc.thePlayer.offGroundTicks == 10 || mc.thePlayer.offGroundTicks == 11 ) && MovementUtils.isOnGround(0.769)) {
                                mc.thePlayer.motionY = 0;
                                MovementUtils.strafe(0.15);
                            }

                            if(mc.thePlayer.onGround) {
                                if(mc.gameSettings.keyBindForward.pressed) MovementUtils.strafe(0.28);
                                else MovementUtils.strafe(0.45);
                                mc.thePlayer.jump();
                            }
                        }
                } else if (watchdogMode.is("GroundStrafe")) {
                    if (mc.thePlayer.isCollidedVertically && MovementUtils.isMoving()) {
                        BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        if (mc.thePlayer.onGround && MovementUtils.isMoving() && !(mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockStairs)) {
                            mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.41999998688698F);
                            MovementUtils.setSpeed((float) Math.max(MovementUtils.getBaseMoveSpeed(), 0.475f + 0.04F * MovementUtils.getSpeedEffect()));
                        }
                    }
                    if (!MovementUtils.isMoving()) {
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                    }
                    break;
            }
        }
    }
    public boolean shouldPreventJumping() {
        return Client.INSTANCE.isEnabled(Speed.class) && MovementUtils.isMoving() && !(mode.is("Watchdog") && watchdogMode.is("Ground"));
    }

    public void onDisable() {
        this.onGroundticks = 0;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }

    boolean fallDistance() { //TAKEN FROM BLINK NOFALL
        int fallDist = -1;
        int minFall = 1;
        int maxFall = 1;
        Vec3 pos = (Vec3) mc.thePlayer.getPositionVector();
        int y = (int) Math.floor(pos.getY());
        if (pos.getY() % 1 == 0) y--;
        for (int i = y; i > -1; i--) {
            if (!isAirorSigh((int) Math.floor(pos.getX()), i, (int) Math.floor(pos.getZ()))) {
                fallDist = y - i;
                break;
            }
        }
        if (fallDist < minFall && fallDist != -1) return false;
        if (fallDist > maxFall) return false;
        return true;
    }

    private boolean isAirorSigh(int x, int y, int z) {
        Vec3 pos = (Vec3) mc.thePlayer.getPositionVector();
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.air || mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.standing_sign || mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.wall_sign;
    }
}
