package cn.molokymc.prideplus.utils.movementfix.Rise;

import cn.molokymc.prideplus.utils.math.Rotation;
import net.minecraft.util.Vec3;

public class VecRotation {
    Vec3 vec;
    Rotation rotation;

    public VecRotation(Vec3 vec, Rotation rotation) {
        this.vec = vec;
        this.rotation = rotation;
    }
    public Rotation getRotation()
    {
        return this.rotation;
    }
}
