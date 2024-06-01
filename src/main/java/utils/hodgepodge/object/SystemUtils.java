package utils.hodgepodge.object;

public class SystemUtils {
    public static SystemEnum getSystem() {
        final String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("windows"))
            return SystemEnum.WINDOWS;
        if (osName.contains("linux"))
            return SystemEnum.LINUX;
        if (osName.contains("mac"))
            return SystemEnum.MACOS;

        return SystemEnum.UNKNOWN;
    }

    public enum SystemEnum {
        WINDOWS,
        LINUX,
        MACOS,
        UNKNOWN
    }
}
