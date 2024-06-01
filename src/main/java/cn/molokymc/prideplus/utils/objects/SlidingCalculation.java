package cn.molokymc.prideplus.utils.objects;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;

@Setter
@Getter
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
        if (Mouse.hasWheel()) {
            final int wheel = Mouse.getDWheel();

            if (wheel != 0) {
                if (wheel < 0) {
                    current += added;
                } else {
                    current -= minus;
                }
            }
        }
    }

}
