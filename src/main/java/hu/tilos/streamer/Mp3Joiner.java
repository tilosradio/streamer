package hu.tilos.streamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Mp3Joiner {

  private static final int BUFFER_SIZE = 500;

  private static final Logger LOG = LoggerFactory.getLogger(Mp3Joiner.class);

  private Map<String, OffsetDouble> cache = new ConcurrentHashMap<>();

  public OffsetDouble findJoinPositions(File firstFile, File secondFile) {
    String cacheKey = firstFile.getName() + "_" + secondFile.getName();
    if (cache.containsKey(cacheKey)) {
      return cache.get(cacheKey);
    }
    try (InputStream is = new FileInputStream(secondFile)) {
      RingBufferWithPosition last = findFirstFrame(is);
      if (last == null) {
        return null;
      }
      RingBuffer b = new RingBuffer(BUFFER_SIZE);
      //400000: maximim 12.4 second overlapping could be detected
      int start = (int) Files.size(Paths.get(secondFile.getAbsolutePath())) - 400000;
      if (start < 0) {
        return null;
      }
      try (InputStream prev = new FileInputStream(firstFile)) {
        int position = start;
        int ch;
        prev.skip(start);
        while ((ch = prev.read()) != -1) {
          b.add(ch);
          if (isFrameStart(b)) {
            if (b.equals(last.buffer)) {
              OffsetDouble result = new OffsetDouble(position - b.getSize() + 1, last.position);
              cache.put(cacheKey, result);
              return result;
            }
          }
          position++;
        }
      }
    } catch (Exception e) {
      LOG.error("Error on joining  files " + firstFile + " and " + secondFile, e);
    }
    return null;
  }


  /**
   * Find the next frame for a random position
   */
  public static int findNextFrame(File file, int startOffset) {
    try {
      try (FileInputStream fis = new FileInputStream(file)) {
        fis.skip(startOffset);

        RingBuffer b = new RingBuffer(BUFFER_SIZE);
        int i = 0, last = 0;
        while (i < Integer.MAX_VALUE) {
          int ch = fis.read();
          b.add(ch);
          if (i > 2000) {
            break;
          }
          if (i > 3) {
            if (isFrameStart(b)) {
              return startOffset + i - b.getSize() + 1;
            }
          }
          i++;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return startOffset;
    }
    return startOffset;

  }

  public RingBufferWithPosition findFirstFrame(InputStream is) throws IOException {
    RingBuffer b = new RingBuffer(BUFFER_SIZE);
    int i = 0, last = 0;
    while (i < Integer.MAX_VALUE) {
      int ch = is.read();
      b.add(ch);
      if (i > 2000) {
        break;
      }
      if (i > 3) {
        if (isFrameStart(b)) {
          return new RingBufferWithPosition(b, i - b.getSize() + 1);
        }
      }
      i++;
    }
    return null;
  }

  private static boolean isFrameStart(RingBuffer b) {
    //11111111   frame sync (8)
    //111 11 01 ?  frame sync (3) + (mpeg version 1) + (Layer III) + protection bit
    //1101 00 ? ?  (256) + (44100) + padding bit + private bit
    //00 stereo
    //FrameSize = 144 * BitRate / SampleRate when the padding bit is cleared and
    // FrameSize = (144 * BitRate / SampleRate) + 1 when the padding bit is set.
    return b.get(0) == 255 &&
        (b.get(1) == 0xfa || b.get(1) == 0xfb) &&
        (((b.get(2) & 0xFD) == 0xD0) || ((b.get(2) & 0xFD) == 0xB0)) &&
        (b.get(3) & 0xCB) == 0x40;
  }

  public void detectJoins(File root, ResourceCollection collection) {

    List<Mp3File> mp3Files = collection.getCollection();
    for (int i = 0; i < mp3Files.size() - 1; i++) {
      Mp3Joiner.OffsetDouble joinPositions = findJoinPositions(new File(root, mp3Files.get(i).getName()), new File(root, mp3Files.get(i + 1).getName()));
      if (joinPositions != null) {
        mp3Files.get(i).setEndOffset(joinPositions.firstEndOffset);
        mp3Files.get(i + 1).setStartOffset(joinPositions.secondStartOffset);
      }
    }
  }

  public void adjustFirstFrame(File root, ResourceCollection collection) {
    List<Mp3File> mp3Files = collection.getCollection();
    if (mp3Files.size() > 0) {
      int position = findNextFrame(new File(root, mp3Files.get(0).getName()), mp3Files.get(0).getStartOffset());
      mp3Files.get(0).setStartOffset(position);
    }
  }

  private File getLocalFile(Mp3File mp3File) {
    return null;
  }

  public static class OffsetDouble {

    public int firstEndOffset;

    public int secondStartOffset;

    public OffsetDouble(int firstEndOffset, int secondStartOffset) {
      this.firstEndOffset = firstEndOffset;
      this.secondStartOffset = secondStartOffset;
    }

    @Override
    public String toString() {
      return "OffsetDouble{" +
          "firstEndOffset=" + firstEndOffset +
          ", secondStartOffset=" + secondStartOffset +
          '}';
    }
  }

  public class RingBufferWithPosition {

    public RingBuffer buffer;

    public int position;

    private RingBufferWithPosition(RingBuffer buffer, int position) {
      this.buffer = buffer;
      this.position = position;
    }

    @Override
    public String toString() {
      return "RingBufferWithPosition{" +
          "buffer=" + buffer +
          ", position=" + position +
          '}';
    }
  }
}
