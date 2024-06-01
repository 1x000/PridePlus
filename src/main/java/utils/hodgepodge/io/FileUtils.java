package utils.hodgepodge.io;

import utils.hodgepodge.object.ObjectUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public final class FileUtils {
    private FileUtils() {}

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createNew(String fileName, String path) throws IOException {
        ObjectUtils.makeSureNotNull(fileName,path);
        new File(path).mkdirs();
        new File(fileName).createNewFile();
    }

    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        }

        for (File d : Objects.requireNonNull(file.listFiles())) {
            deleteFile(d);
        }

        return file.delete();
    }

    public static void moveFile(File movedFile,File moveTo,byte[] buffer) throws IOException {
        copyFile(movedFile,moveTo,buffer);
        deleteFile(movedFile);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyFile(File copiedFile, File copyTo, byte[] buffer) throws IOException {
        if (!copiedFile.exists()) {
            throw new FileNotFoundException("Copied File");
        }
        if (!copyTo.exists()) {
            if (copyTo.isDirectory()) {
                copyTo.mkdirs();
            } else if (copyTo.isFile()) {
                createNew(copyTo.getName(),copyTo.getPath());
            }
        }
        FileInputStream stream = IOUtils.getFileInputStream(copiedFile);
        copyInputStream(stream,copyTo,buffer);
        IOUtils.close(stream);
    }

    public static void copyInputStream(InputStream stream,File copyTo,byte[] buffer) throws IOException {
        writeInputStreamToFile(copyTo,stream,buffer);
    }

    public static String readFileAsString(File file,String charset) throws IOException {
        return readFileAsString(file,Charset.forName(charset));
    }

    public static String readFileAsString(File file,Charset charset) throws IOException {
        ObjectUtils.makeSureNotNull(file,charset);
        return IOUtils.inputStreamToString(IOUtils.getFileInputStream(file),charset);
    }

    public static List<String> readFileAsStringList(File file, Charset charset) throws IOException {
        ObjectUtils.makeSureNotNull(file,charset);
        return IOUtils.inputStreamToStringLines(IOUtils.getFileInputStream(file),charset);
    }

    public static byte[] readFileAsByteArray(File file,byte[] buffer) throws IOException {
        ObjectUtils.makeSureNotNull(file);
        return IOUtils.toByteArray(IOUtils.getFileInputStream(file),buffer);
    }

    public static void writeStringToFile(File file,String str,Charset code) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),code));
        writer.write(str);
        writer.close();
    }

    public static void writeByteArrayToFile(File file,byte[] byteArray) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        outputStream.write(byteArray);
        outputStream.close();
    }

    public static void writeInputStreamToFile(File file,InputStream inputStream,byte[] buffer) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        int cache;

        while ((cache = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer,0,cache);
        }

        inputStream.close();
        outputStream.close();
    }
}
