package cn.molokymc.prideplus.utils;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@Getter
public class EnumFacingUtils {
    public EnumFacing enumFacing;
    private final Vec3 offset;

    public EnumFacingUtils(final EnumFacing enumFacing, final Vec3 offset) {
        this.enumFacing = enumFacing;
        this.offset = offset;
    }
}
