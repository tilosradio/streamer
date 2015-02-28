package hu.tilos.radio.backend.streamer;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatToMongo implements StatPersistence {

    private static final Logger LOG = LoggerFactory.getLogger(StatPersistence.class);

    private double SECOND_TO_BYTE = 38.28125 * 836;

    @Inject
    DB db;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void startDownload(final String token, final Date startDate) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicDBObject newRecord = new BasicDBObject("startDate", startDate)
                            .append("time", new Date())
                            .append("token", "token")
                            .append("realStartDate", startDate);
                    db.getCollection("stat_download").insert(newRecord);
                } catch (Exception ex) {
                    LOG.error("Can't save download audit to the log file", ex);
                }
            }
        });
    }

    @Override
    public void seek(final String token, final int position) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicDBObject query = new BasicDBObject("token", token);
                    DBObject statRecord = db.getCollection("stat_download").findOne(query);
                    statRecord.put("position", position);
                    Date startDate = (Date) statRecord.get("startDate");
                    statRecord.put("realStartDate", new Date(startDate.getTime() + Math.round((position / SECOND_TO_BYTE) * 1000)));
                    db.getCollection("stat_download").update(query, statRecord);
                } catch (Exception ex) {
                    LOG.error("Can't save download audit to the log file", ex);
                }
            }
        });
    }

    @Override
    public void endDownload(final String token, final int writtenBytes) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicDBObject query = new BasicDBObject("token", token);
                    DBObject statRecord = db.getCollection("stat_download").findOne(query);
                    Date startDate = (Date) statRecord.get("startDate");
                    Date endDate = new Date(startDate.getTime() + Math.round((writtenBytes / SECOND_TO_BYTE) * 1000));
                    statRecord.put("bytes", writtenBytes);
                    statRecord.put("endDate", endDate);
                    db.getCollection("stat_download").update(query, statRecord);
                } catch (Exception ex) {
                    LOG.error("Can't save download audit to the log file", ex);
                }
            }
        });

    }
}
