package utils.hodgepodge.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class NativeLoader {
    public static void loadAndWriteToCacheDirectory(InputStream nativeStream,byte[] buffer) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"));
        loadAndWrite(file,nativeStream,buffer);
        file.deleteOnExit();
    }

    public static void loadAndWrite(File writeToWhere,InputStream nativeStream,byte[] buffer) throws IOException {
        FileUtils.writeInputStreamToFile(writeToWhere,nativeStream,buffer);
        System.load(writeToWhere.getAbsolutePath());
    }
}
