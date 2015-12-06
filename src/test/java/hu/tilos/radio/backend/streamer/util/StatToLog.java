package hu.tilos.radio.backend.streamer.util;

import hu.tilos.radio.backend.streamer.StatPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by eszti on 2/22/15.
 */
public class StatToLog implements StatPersistence {
    private static final Logger LOG = LoggerFactory.getLogger(StatToLog.class);

    @Override
    public void startDownload(String token, Date startDate) {
        LOG.debug("Start downloading from " + startDate);
    }

    @Override
    public void seek(String token, int position) {
        LOG.debug("Seek to " + position);

    }

    @Override
    public void endDownload(String token, int writtenBytes) {
        LOG.debug("Download has been finished after " + writtenBytes + " bytes");

    }
}
