package cn.molokymc.prideplus.utils.movementfix.Rise;


import cn.molokymc.prideplus.event.ListenerAdapter;
import cn.molokymc.prideplus.event.impl.player.*;
import cn.molokymc.prideplus.utils.math.Vector2f;
import cn.molokymc.prideplus.utils.movementfix.MovementFix;

import static cn.molokymc.prideplus.utils.Utils.mc;

public final class RotationComponent extends ListenerAdapter {
    private static boolean active, smoothed;
    public static Vector2f rotations;
    public static Vector2f lastRotations;
    public static Vector2f targetRotations;
    public static Vector2f lastServerRotations;
    private static double rotationSpeed;
    private static MovementFix correctMovement;


    /*
     * This method must be called on Pre Update Event to work correctly
     */
    public static void setRotations(final Vector2f rotations, final double rotationSpeed, final MovementFix correctMovement) {
        RotationComponent.targetRotations = rotations;
        RotationComponent.rotationSpeed = rotationSpeed * 18;
        RotationComponent.correctMovement = correctMovement;
        active = true;

        smooth();
    }

    @Override
    public void onUpdateEvent(UpdateEvent e){
        if (!RotationComponent.active || RotationComponent.rotations == null || RotationComponent.lastRotations == null || RotationComponent.targetRotations == null || RotationComponent.lastServerRotations == null) {
            RotationComponent.rotations = (RotationComponent.lastRotations = (RotationComponent.targetRotations = (RotationComponent.lastServerRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch))));
        }
        if (RotationComponent.active) {
            smooth();
        }
        if (RotationComponent.correctMovement == MovementFix.BACKWARDS_SPRINT && RotationComponent.active && Math.abs(RotationComponent.rotations.x - Math.toDegrees(MoveUtil.direction())) > 45.0) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
        }
    }

    @Override
    public void onMoveInput(EventMoveInput event) {
        if (active && correctMovement == MovementFix.NORMAL && rotations != null) {
            /*
             * Calculating movement fix
             */
            final float yaw = rotations.x;
            MoveUtil.fixMovement(event, yaw);
        }
    }

    @Override
    public void onLook(EventLook event) {
        if (active && rotations != null) {
            event.setRotation(rotations);
        }
    }

    @Override
    public void onPlayerMoveUpdateEvent(PlayerMoveUpdateEvent a) {
        if (RotationComponent.active && (RotationComponent.correctMovement == MovementFix.NORMAL || RotationComponent.correctMovement == MovementFix.TRADITIONAL) && RotationComponent.rotations != null) {
            a.setYaw(RotationComponent.rotations.x);
        }
    }

    @Override
    public void onJumpFixEvent(JumpFixEvent event) {
        if (RotationComponent.active && (RotationComponent.correctMovement == MovementFix.NORMAL || RotationComponent.correctMovement == MovementFix.TRADITIONAL || RotationComponent.correctMovement == MovementFix.BACKWARDS_SPRINT) && RotationComponent.rotations != null) {
            event.setYaw(RotationComponent.rotations.x);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPre()) {
            if (active && rotations != null) {
                final float yaw = rotations.x;
                final float pitch = rotations.y;

                event.setYaw(yaw);
                event.setPitch(pitch);

                // mc.thePlayer.rotationYaw = yaw;
                // mc.thePlayer.rotationPitch = pitch;

                mc.thePlayer.renderYawOffset = yaw;
                mc.thePlayer.rotationYawHead = yaw;
                mc.thePlayer.renderPitchHead = pitch;

                lastServerRotations = new Vector2f(yaw, pitch);

                if (Math.abs((rotations.x - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((rotations.y - mc.thePlayer.rotationPitch)) < 1) {
                    active = false;

                    this.correctDisabledRotations();
                }

                lastRotations = rotations;
            } else {
                lastRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            }

            targetRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            smoothed = false;
        }
    }

    private static void correctDisabledRotations() {
        if (mc.thePlayer == null) {
            return;
        }

        final Vector2f rotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        final Vector2f fixedRotations = RotationRise.resetRotation(RotationRise.applySensitivityPatchA(rotations, lastRotations));

        mc.thePlayer.rotationYaw = fixedRotations.x;
        mc.thePlayer.rotationPitch = fixedRotations.y;
    }

    public static void smooth() {
        if (!smoothed) {
            final float lastYaw = lastRotations.x;
            final float lastPitch = lastRotations.y;
            final float targetYaw = targetRotations.x;
            final float targetPitch = targetRotations.y;

            rotations = RotationRise.smooth(new Vector2f(lastYaw, lastPitch), new Vector2f(targetYaw, targetPitch),
                    rotationSpeed + Math.random());

            if (correctMovement == MovementFix.NORMAL || correctMovement == MovementFix.TRADITIONAL) {
                mc.thePlayer.movementYaw = rotations.x;
            }

            mc.thePlayer.velocityYaw = rotations.x;
        }

        smoothed = true;

        /*
         * Updating MouseOver
         */
        mc.entityRenderer.getMouseOver(1);
    }

    public static void stopRotation() {
        active = false;

        correctDisabledRotations();
    }
}
