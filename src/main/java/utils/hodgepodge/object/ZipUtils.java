package utils.hodgepodge.object;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {
    public static List<ZipEntry> getZipEntriesFromZipInputStream(ZipInputStream zipInputStream, List<ZipEntry> list) throws IOException {
        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            list.add(entry);
        }

        return list;
    }

    public static List<ZipEntry> getZipEntriesFromZipFile(ZipFile zipFile,List<ZipEntry> list) {
        ZipEntry entry;
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            list.add(entry);
        }

        return list;
    }

    public static void addZipEntryToZipOutputStream(ZipEntry zipEntry,byte[] data,ZipOutputStream zos) throws IOException {
        zos.putNextEntry(zipEntry);
        zos.write(data);
        zos.closeEntry();
    }
}
