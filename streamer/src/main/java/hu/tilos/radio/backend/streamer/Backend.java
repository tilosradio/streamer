package hu.tilos.radio.backend.streamer;

import hu.tilos.radio.backend.Mp3File;
import hu.tilos.radio.backend.ResourceCollection;

import java.io.File;
import java.io.OutputStream;

public interface Backend {

    public void stream(ResourceCollection collection, int startOffset, int endPosition, OutputStream out) throws Exception;

    public int getSize(ResourceCollection collection);

    File getLocalFile(Mp3File mp3File);
}
