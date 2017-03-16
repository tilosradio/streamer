package hu.tilos.streamer;

import java.io.File;
import java.io.OutputStream;

public interface Backend {

  int stream(ResourceCollection collection, int startOffset, int endPosition, OutputStream out) throws Exception;

  int getSize(ResourceCollection collection);

  File getLocalFile(Mp3File mp3File);
}
