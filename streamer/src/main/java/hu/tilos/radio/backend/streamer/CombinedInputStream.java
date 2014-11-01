package hu.tilos.radio.backend.streamer;

import java.io.IOException;
import java.io.InputStream;

public class CombinedInputStream extends InputStream {

    private InputStream[] streams;

    private int idx = 0;

    public CombinedInputStream(InputStream... streams) {
        this.streams = streams;
    }

    @Override
    public int read() throws IOException {
        int res = streams[idx].read();
        if (res == -1 && idx < streams.length - 1) {
            idx++;
            res = streams[idx].read();
        }
        return res;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int toRead = len;
        int offset = off;
        while (toRead > 0) {
            int read = streams[idx].read(b, offset, toRead);
            if (read == -1) {
                if (idx == streams.length - 1) {
                    if (toRead < len) {
                        return len - toRead;
                    } else {
                        return -1;
                    }
                } else {
                    idx++;
                }
            } else {
                offset += read;
                toRead -= read;
            }
        }
        return len - toRead;
    }


    @Override
    public void close() throws IOException {
        for (InputStream stream : streams) {
            stream.close();
        }
    }
}
