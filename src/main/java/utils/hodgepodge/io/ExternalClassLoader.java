package utils.hodgepodge.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public final class ExternalClassLoader {
    private static final CustomClassLoader Instance = new CustomClassLoader();

    public static void loadJar(File jarIn) throws IOException {
        Instance.addURLFile(jarIn.toURI().toURL());
    }

    public static void loadJar(URL jarURL) {
        Instance.addURLFile(jarURL);
    }

    public static Class<?> getClassFromName(String name) throws ClassNotFoundException {
        return Class.forName(name,true,Instance);
    }

    private final static class CustomClassLoader extends URLClassLoader {
        public CustomClassLoader() {
            super(new URL[]{},findParentClassLoader());
        }

        public void addURLFile(URL file) {
            addURL(file);
        }

        private static ClassLoader findParentClassLoader() {
            ClassLoader parent = CustomClassLoader.class.getClassLoader();
            if (parent == null) {
                parent = CustomClassLoader.class.getClassLoader();
            }
            if (parent == null) {
                parent = ClassLoader.getSystemClassLoader();
            }
            return parent;
        }
    }
}
