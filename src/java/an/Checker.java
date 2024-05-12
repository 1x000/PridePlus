package an;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Locale;

public class Checker {
    public static boolean crash = false;

    public static void check() {
        String javaVersion = System.getProperty("java.version");
        if (javaVersion != null) {
            int ver = Integer.parseInt(javaVersion.substring(javaVersion.length() - 3));
            // 小于302
            if (ver < 202) {
                crash = true;
            }
        }

        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (!crash) {
            return;
        }
        // 跳转                                                                           5
        try {
            JOptionPane.showMessageDialog(null, "你的Java版本过低，请切换至8u202以上版本", "Java", JOptionPane.WARNING_MESSAGE);
            if (osName.contains("windows")) {
                Desktop.getDesktop().browse(new URI("https://www.azul.com/downloads/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jdk#zulu"));
            } else if (osName.contains("mac")) {
                if (osName.contains("arm")) {
                    Desktop.getDesktop().browse(new URI("https://www.azul.com/downloads/?version=java-8-lts&os=macos&architecture=arm-64-bit&package=jdk#zulu"));
                } else {
                    Desktop.getDesktop().browse(new URI("https://www.azul.com/downloads/?version=java-8-lts&os=macos&architecture=x86-64-bit&package=jdk#zulu"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        crash();
    }

    private static void crash() {
        throw new RuntimeException("Your Java version is too old, please update to version 8u302 or higher.");
    }
}
