package utils.hodgepodge.io;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class IOUtils {
    private static final byte[] NORMAL_BUFFER = new byte[4096];
    private static final String lineSeparator = System.lineSeparator();

    private IOUtils() {}

    public static byte[] toByteArray(InputStream inputStream,byte[] buffer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int cache;

        while ((cache = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer,0,cache);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static void close(Closeable closeable) throws IOException {
        closeable.close();
    }
    
    public static void close(Closeable... closeables) throws IOException {
        for (Closeable closeable : closeables) close(closeable);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            close(closeable);
        } catch (IOException ignored) {}
    }

    public static void closeQuietly(Closeable... closeable) {
        try {
            close(closeable);
        } catch (IOException ignored) {}
    }

    public static void flush(Flushable flushable) throws IOException {
        flushable.flush();
    }

    public static void flush(Flushable... flushables) throws IOException {
        for (Flushable flushable : flushables) flush(flushable);
    }

    public static void flushQuietly(Flushable flushable) {
        try {
            flush(flushable);
        } catch (IOException ignored) {}
    }

    public static void flushQuietly(Flushable... flushable) {
        try {
            flush(flushable);
        } catch (IOException ignored) {}
    }

    public static InputStream getFileInputStreamByURL(File file) throws IOException {
        return file.toURI().toURL().openStream();
    }

    public static FileInputStream getFileInputStream(File file) throws IOException {
        return new FileInputStream(file);
    }

    public static URL getResource(String name) {
        return IOUtils.class.getResource("/" + name);
    }

    public static InputStream getResourceAsStream(String name) {
        return IOUtils.class.getResourceAsStream("/" + name);
    }

    public static String inputStreamToString(InputStream inputStream,String code) throws IOException {
        return inputStreamToString(inputStream,Charset.forName(code));
    }

    public static String inputStreamToString(InputStream inputStream,Charset code) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,code));
        StringBuilder builder = new StringBuilder();
        String cache;

        while ((cache = reader.readLine()) != null) {
            builder.append(cache).append(lineSeparator);
        }

        inputStream.close();
        reader.close();

        return builder.toString();
    }

    public static List<String> inputStreamStringLines(InputStream inputStream,String code) throws IOException {
        return inputStreamToStringLines(inputStream,Charset.forName(code));
    }

    public static List<String> inputStreamToStringLines(InputStream inputStream, Charset code) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,code));
        ArrayList<String> stringList = new ArrayList<>();
        String cache;

        while ((cache = reader.readLine()) != null) {
            stringList.add(cache);
        }

        inputStream.close();
        reader.close();

        return stringList;
    }

    /**
     * @return InputStream bytes
     */
    public static int getInputStreamSize(InputStream inputStream,byte[] buffer) throws IOException {
        int size = 0,cache;

        while ((cache = inputStream.read(buffer)) != -1) {
            size += cache;
        }

        return size;
    }

    public static boolean inputStreamEquals(InputStream input1, InputStream input2,byte[] buffer) throws IOException {
        if (input1 == input2) return true;
        if (input1 == null || input2 == null) return false;

        byte[] byteArray1 = toByteArray(input1,buffer);
        byte[] byteArray2 = toByteArray(input2,buffer);

        if (byteArray1.length != byteArray2.length) return false;

        for (int i = 0; i < byteArray1.length; i++) {
            if (byteArray1[i] != byteArray2[i]) {
                return false;
            }
        }

        return true;
    }

    public static byte[] normalBuffer() {
        return NORMAL_BUFFER;
    }

    public static byte[] newBuffer() {
        return newBuffer(4096);
    }

    public static byte[] newBuffer(int size) {
        return new byte[size];
    }
}
