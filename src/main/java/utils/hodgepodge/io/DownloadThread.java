package utils.hodgepodge.io;

import utils.hodgepodge.function.CatchHandler;
import utils.hodgepodge.function.VoidFunction;

import java.io.*;
import java.net.URL;

public final class DownloadThread extends Thread implements Closeable {
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final BufferedOutputStream bos = new BufferedOutputStream(byteArrayOutputStream);
    private final CatchHandler catchHandler;
    private final byte[] buffer;
    private final InputStream inputStream;

    private VoidFunction endingHandler;
    private int weAreWhere;
    private boolean stop = false;

    public DownloadThread(URL url, byte[] buffer,CatchHandler catchHandler) throws IOException {
        this.catchHandler = catchHandler;
        this.buffer = buffer;
        this.inputStream = url.openStream();
    }

    @Override
    public void run() {
        try {
            int cache;
            while (true) {
                if (!stop) {
                    if ((cache = inputStream.read(buffer)) != -1) {
                        bos.write(buffer, 0, cache);
                        bos.flush();
                        weAreWhere += cache;
                    } else break;
                }
            }
        } catch (IOException e) {
            catchHandler.onCatchException(e);
        }

        if (endingHandler != null) endingHandler.handle();
    }

    public byte[] getDownloadedByteArray() {
        return byteArrayOutputStream.toByteArray();
    }

    public int getWeAreWhere() {
        return weAreWhere;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setEndingHandler(VoidFunction endingHandler) {
        this.endingHandler = endingHandler;
    }

    public void reset() {
        byteArrayOutputStream.reset();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        bos.close();
        byteArrayOutputStream.close();
    }
}
