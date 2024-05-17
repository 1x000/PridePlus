package cn.molokymc.prideplus.utils.movementfix;

import cn.molokymc.prideplus.utils.RotationsUtil;
import lombok.Getter;


@Getter
public class FixedRotations {

    private float yaw, pitch;
    private float lastYaw, lastPitch;

    public FixedRotations(float startingYaw, float startingPitch) {
        lastYaw = yaw = startingYaw;
        lastPitch = pitch = startingPitch;
    }

    public void updateRotations(float requestedYaw, float requestedPitch) {
        lastYaw = yaw;
        lastPitch = pitch;

        float gcd = RotationsUtil.getGCD();

        float yawDiff = (requestedYaw - yaw);
        float pitchDiff = (requestedPitch - pitch);

        float fixedYawDiff = yawDiff - (yawDiff % gcd);
        float fixedPitchDiff = pitchDiff - (pitchDiff % gcd);

        yaw += fixedYawDiff;
        pitch += fixedPitchDiff;

        pitch = Math.max(-90, Math.min(90, pitch));
    }

}
