package cn.molokymc.prideplus.ui.altmanager;

import org.lwjgl.input.Mouse;

public final class SlidingCalculation {
    private double current;
    private double added;
    private double minus;

    public SlidingCalculation() {
    }

    public SlidingCalculation(double added, double minus) {
        this.added = added;
        this.minus = minus;
    }

    public SlidingCalculation(double current, double added, double minus) {
        this.current = current;
        this.added = added;
        this.minus = minus;
    }

    public void calculation() {
        int wheel;
        if (Mouse.hasWheel() && (wheel = Mouse.getDWheel()) != 0) {
            this.current = wheel < 0 ? this.current + this.added : this.current - this.minus;
        }
    }

    public double getCurrent() {
        return this.current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getAdded() {
        return this.added;
    }

    public void setAdded(double added) {
        this.added = added;
    }

    public double getMinus() {
        return this.minus;
    }

    public void setMinus(double minus) {
        this.minus = minus;
    }
}

