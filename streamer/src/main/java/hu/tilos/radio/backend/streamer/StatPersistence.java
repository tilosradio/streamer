package hu.tilos.radio.backend.streamer;

import java.util.Date;


public interface StatPersistence {

    String startDownload(Date startDate);

    void seek(String token, int position);

    void endDownload(String token, int writtenBytes);
}
