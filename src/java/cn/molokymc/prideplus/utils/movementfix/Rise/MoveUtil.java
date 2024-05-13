package cn.molokymc.prideplus.utils.movementfix.Rise;


import cn.molokymc.prideplus.event.impl.player.EventMoveInput;
import cn.molokymc.prideplus.event.impl.player.MoveEvent;
import cn.molokymc.prideplus.event.impl.world.EventMove;
import cn.molokymc.prideplus.utils.misc.MobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;

import java.util.Arrays;

public class MoveUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final double WALK_SPEED = 0.221;
    public static void setSpeed(double speed) {
        mc.thePlayer.motionX = - Math.sin(getDirection()) * speed;
        mc.thePlayer.motionZ = Math.cos(getDirection()) * speed;
    }

    public static void strafeNoTargetStrafe(MoveEvent event, double speed) {
        float direction = (float) Math.toRadians(getPlayerDirection());

        if (isMoving()) {
            event.setX(mc.thePlayer.motionX = -Math.sin(direction) * speed);
            event.setZ(mc.thePlayer.motionZ = Math.cos(direction) * speed);
        } else {
            event.setX(mc.thePlayer.motionX = 0);
            event.setZ(mc.thePlayer.motionZ = 0);
        }
    }

    public static void jump(EventMove event) {
        double jumpY = (double) mc.thePlayer.getJumpUpwardsMotion();

        if(mc.thePlayer.isPotionActive(MobEffects.JUMP_BOOST)) {
            jumpY += (double)((float)(mc.thePlayer.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
        }

        event.setY(mc.thePlayer.motionY = jumpY);
    }
    public static int getSpeedAmplifier() {
        if(mc.thePlayer.isPotionActive(MobEffects.SPEED)) {
            return 1 + mc.thePlayer.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
        }

        return 0;
    }
    public static float[] incrementMoveDirection(float forward, float strafe) {
        if(forward != 0 || strafe != 0) {
            float value = forward != 0 ? Math.abs(forward) : Math.abs(strafe);

            if(forward > 0) {
                if(strafe > 0) {
                    strafe = 0;
                } else if(strafe == 0) {
                    strafe = -value;
                } else if(strafe < 0) {
                    forward = 0;
                }
            } else if(forward == 0) {
                if(strafe > 0) {
                    forward = value;
                } else {
                    forward = -value;
                }
            } else {
                if(strafe < 0) {
                    strafe = 0;
                } else if(strafe == 0) {
                    strafe = value;
                } else if(strafe > 0) {
                    forward = 0;
                }
            }
        }

        return new float[] {forward, strafe};
    }

    public static float getPlayerDirection() {
        float direction = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 45;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 45;
            }
        } else if (mc.thePlayer.moveForward < 0) {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 135;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 90;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 90;
            }
        }

        return direction;
    }

    public static float getPlayerDirection(float baseYaw) {
        float direction = baseYaw;

        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 45;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 45;
            }
        } else if (mc.thePlayer.moveForward < 0) {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 135;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 90;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 90;
            }
        }

        return direction;
    }
    public static double direction() {
        float rotationYaw = mc.thePlayer.movementYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }
    /**
     * Gets the players' movement yaw
     */
    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Stops the player from moving
     */
    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }

    /**
     * Gets the players predicted jump motion the specified amount of ticks ahead
     *
     * @return predicted jump motion
     */
    public static double predictedMotion(final double motion, final int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }

    /**
     * Fixes the players movement
     */
    public static void fixMovement(final EventMoveInput event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = Math.abs(angle - predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }
    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.thePlayer.lastTickPosX, (float) mc.thePlayer.lastTickPosZ),
                to = new Vector2f((float) mc.thePlayer.posX, (float) mc.thePlayer.posZ),
                diff = new Vector2f(to.x - from.x, to.y - from.y);

        double x = diff.x, z = diff.y;
        if (x != 0 && z != 0) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }



    //get Move

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static double getSpeed(double motionX, double motionZ) {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }
    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(MobEffects.SPEED))
            return mc.thePlayer.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1;
        else
            return 0;
    }

    //Strafe

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(EventMove eventMove) {
        strafe(eventMove, getSpeed());
    }

    public static void strafe(final double speed) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static void strafe(EventMove eventMove, final double speed) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        eventMove.setX(mc.thePlayer.motionX = -Math.sin(yaw) * speed);
        eventMove.setZ(mc.thePlayer.motionZ = Math.cos(yaw) * speed);
    }

    public static void strafe(final double speed, float yaw) {
        if (!isMoving()) {
            return;
        }

        yaw = (float) Math.toRadians(yaw);
        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }

    public static void setMotion(double speed) {
        setMotion(speed, mc.thePlayer.rotationYaw);
    }

    public static void setMotion(EventMove e, double speed, float yaw) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = e.x = 0;
            mc.thePlayer.motionZ = e.z = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = e.x = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = e.z = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static void setMotion(EventMove e, double speed) {
        setMotion(e, speed, mc.thePlayer.rotationYaw);
    }

    public static void setMotion(double speed, float yaw) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    //UHC tp
    public static void hypixelTeleport(final double[] startPos, final BlockPos endPos) {
        double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
        double distanceEntreLesPackets = 0.31 + MoveUtil.getSpeedEffect() / 20;
        double xtp, ytp, ztp = 0;
        if (dist > distanceEntreLesPackets) {

            double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;

            xtp = mc.thePlayer.posX;
            ytp = mc.thePlayer.posY;
            ztp = mc.thePlayer.posZ;
            double count = 0;
            for (int i = 1; i < nbPackets; i++) {
                double xdi = (endPos.getX() - mc.thePlayer.posX) / (nbPackets);
                xtp += xdi;

                double zdi = (endPos.getZ() - mc.thePlayer.posZ) / (nbPackets);
                ztp += zdi;

                double ydi = (endPos.getY() - mc.thePlayer.posY) / (nbPackets);
                ytp += ydi;
                count++;

                if (!mc.theWorld.getBlockState(new BlockPos(xtp, ytp - 1, ztp)).getBlock().isFullBlock()) {
                    if (count <= 2) {
                        ytp += 2E-8;
                    } else if (count >= 4) {
                        count = 0;
                    }
                }
                C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, false);
                mc.getNetHandler().getNetworkManager().sendPacket(Packet);
            }
            mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);
        } else {
            mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());
        }
    }


    //GET yaw
    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
    public static void useDiagonalSpeed() {
        KeyBinding[] gameSettings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft};

        final int[] down = {0};

        Arrays.stream(gameSettings).forEach(keyBinding -> {
            down[0] = down[0] + (keyBinding.isKeyDown() ? 1 : 0);
        });

        boolean active = down[0] == 1;

        if (!active) return;

        final double groundIncrease = (0.1299999676734952 - 0.12739998266255503) + 1E-7 - 1E-8;
        final double airIncrease = (0.025999999334873708 - 0.025479999685988748) - 1E-8;
        final double increase = mc.thePlayer.onGround ? groundIncrease : airIncrease;

        moveFlying(increase);
    }

    public static void moveFlying(double increase) {
        if (!MoveUtil.isMoving()) return;
        final double yaw = MoveUtil.direction();
        mc.thePlayer.motionX += -MathHelper.sin((float) yaw) * increase;
        mc.thePlayer.motionZ += MathHelper.cos((float) yaw) * increase;
    }
}
