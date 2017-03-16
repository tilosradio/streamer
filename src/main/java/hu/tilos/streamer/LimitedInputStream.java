package hu.tilos.streamer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream the bytes from an other stream from/to a specified offset.
 */
public class LimitedInputStream extends InputStream {

  private final int from;

  private final int to;

  private InputStream origin;

  int pos = 0;

  public LimitedInputStream(InputStream origin, int from, int to) {
    this.origin = origin;
    this.from = from;
    this.to = to;
    try {
      pos = from;
      origin.skip(from);
    } catch (IOException e) {
      throw new RuntimeException("Can't seek to the from position " + from, e);
    }
  }

  @Override
  public int read() throws IOException {
    if (pos >= to) {
      return -1;
    }
    pos++;
    return origin.read();
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (to <= pos) {
      return -1;
    }
    int read = origin.read(b, off, Math.min(len, to - pos));
    pos += read;
    return read;
  }

  @Override
  public int available() throws IOException {
    return Math.min(pos + origin.available(), to);
  }

  @Override
  public void close() throws IOException {
    origin.close();
  }
}
