package utils.hodgepodge.object.math;

public enum CubeEnum {
    CUBIC_CENTIMETER {
        @Override
        public double toCubicMeter(double n) {
            return toCubicDecimeter(n) / 1000.0;
        }
        @Override
        public double toCubicDecimeter(double n) {
            return n / 1000.0;
        }
        @Override
        public double toCubicCentimeter(double n) {
            return n;
        }
    },
    CUBIC_DECIMETER {
        @Override
        public double toCubicMeter(double n) {
            return n / 1000.0;
        }
        @Override
        public double toCubicDecimeter(double n) {
            return n;
        }
        @Override
        public double toCubicCentimeter(double n) {
            return n * 1000.0;
        }
    },
    CUBIC_METER {
        @Override
        public double toCubicMeter(double n) {
            return n;
        }
        @Override
        public double toCubicDecimeter(double n) {
            return n * 1000.0;
        }
        @Override
        public double toCubicCentimeter(double n) {
            return toCubicDecimeter(n) * 1000.0;
        }
    };

    public abstract double toCubicMeter(double n);
    public abstract double toCubicDecimeter(double n);
    public abstract double toCubicCentimeter(double n);
}
