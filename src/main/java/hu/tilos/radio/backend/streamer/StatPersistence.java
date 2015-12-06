package hu.tilos.radio.backend.streamer;

import java.util.Date;


public interface StatPersistence {

    void startDownload(String token, Date startDate);

    void seek(String token, int position);

    void endDownload(String token, int writtenBytes);
}
