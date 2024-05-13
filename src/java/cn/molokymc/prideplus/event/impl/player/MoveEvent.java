//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.molokymc.prideplus.event.impl.player;

import cn.molokymc.prideplus.event.Event;
import cn.molokymc.prideplus.utils.player.MovementUtils;

public class MoveEvent extends Event {
    public double x;
    private double y;
    public double z;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setSpeed(double speed) {
        MovementUtils.setSpeed(this, speed);
    }
}