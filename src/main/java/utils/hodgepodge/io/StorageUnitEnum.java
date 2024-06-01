package utils.hodgepodge.io;

public enum StorageUnitEnum {
    BYTE("B") {
        @Override
        public double toByte(double n) {
            return n;
        }
        @Override
        public double toKilobyte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toKilobyte(n) / 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toMegabyte(n) / 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toGigabyte(n) / 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toTerabyte(n) / 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toPetabyte(n) / 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toExabyte(n) / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    KILOBYTE("KB") {
        @Override
        public double toByte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return n;
        }
        @Override
        public double toMegabyte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toMegabyte(n) / 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toGigabyte(n) / 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toTerabyte(n) / 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toPetabyte(n) / 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toExabyte(n) / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    MEGABYTE("MB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return n;
        }
        @Override
        public double toGigabyte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toGigabyte(n) / 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toTerabyte(n) / 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toPetabyte(n) / 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toExabyte(n) / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    GIGABYTE("GB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return n;
        }
        @Override
        public double toTerabyte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toTerabyte(n) / 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toPetabyte(n) / 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toExabyte(n) / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    TERABYTE("TB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toGigabyte(n) * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return n;
        }
        @Override
        public double toPetabyte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toPetabyte(n) / 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toExabyte(n) / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    PETABYTE("PB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toGigabyte(n) * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toTerabyte(n) * 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return n;
        }
        @Override
        public double toExabyte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toExabyte(n) / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    EXABYTE("EB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toGigabyte(n) * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toTerabyte(n) * 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toPetabyte(n) * 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return n;
        }
        @Override
        public double toZettaByte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return toZettaByte(n) / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    ZETTA_BYTE("ZB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toGigabyte(n) * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toTerabyte(n) * 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toPetabyte(n) * 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toExabyte(n) * 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return n;
        }
        @Override
        public double toYottaByte(double n) {
            return n / 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return toYottaByte(n) / 1024.0;
        }
    },
    YOTTA_BYTE("YB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toGigabyte(n) * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toTerabyte(n) * 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toPetabyte(n) * 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toExabyte(n) * 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toZettaByte(n) * 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return n;
        }
        @Override
        public double toBrontoByte(double n) {
            return n / 1024.0;
        }
    },
    BRONTO_BYTE("BB") {
        @Override
        public double toByte(double n) {
            return toKilobyte(n) * 1024.0;
        }
        @Override
        public double toKilobyte(double n) {
            return toMegabyte(n) * 1024.0;
        }
        @Override
        public double toMegabyte(double n) {
            return toGigabyte(n) * 1024.0;
        }
        @Override
        public double toGigabyte(double n) {
            return toTerabyte(n) * 1024.0;
        }
        @Override
        public double toTerabyte(double n) {
            return toPetabyte(n) * 1024.0;
        }
        @Override
        public double toPetabyte(double n) {
            return toExabyte(n) * 1024.0;
        }
        @Override
        public double toExabyte(double n) {
            return toZettaByte(n) * 1024.0;
        }
        @Override
        public double toZettaByte(double n) {
            return toYottaByte(n) * 1024.0;
        }
        @Override
        public double toYottaByte(double n) {
            return n * 1024.0;
        }
        @Override
        public double toBrontoByte(double n) {
            return n;
        }
    };

    private final String abbreviation;

    StorageUnitEnum(String name) {
        this.abbreviation = name;
    }

    public abstract double toByte(double n);
    public abstract double toKilobyte(double n);
    public abstract double toMegabyte(double n);
    public abstract double toGigabyte(double n);
    public abstract double toTerabyte(double n);
    public abstract double toPetabyte(double n);
    public abstract double toExabyte(double n);
    public abstract double toZettaByte(double n);
    public abstract double toYottaByte(double n);
    public abstract double toBrontoByte(double n);

    public String getAbbreviation() {
        return abbreviation;
    }
}
