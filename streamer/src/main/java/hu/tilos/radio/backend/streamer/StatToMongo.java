package hu.tilos.radio.backend.streamer;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.Date;

public class StatToMongo implements StatPersistence {

    private double SECOND_TO_BYTE = 38.28125 * 836;

    @Inject
    DB db;

    @Override
    public String startDownload(Date startDate) {
        BasicDBObject newRecord = new BasicDBObject("startDate", startDate).append("time", new Date()).append("realStartDate", startDate);
        WriteResult result = db.getCollection("stat_download").insert(newRecord);
        return "" + newRecord.getString("_id");
    }

    @Override
    public void seek(String token, int position) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(token));
        DBObject statRecord = db.getCollection("stat_download").findOne(query);
        statRecord.put("position", position);
        Date startDate = (Date) statRecord.get("startDate");
        statRecord.put("realStartDate", new Date(startDate.getTime() + Math.round((position / SECOND_TO_BYTE) * 1000)));
        db.getCollection("stat_download").update(query, statRecord);
    }

    @Override
    public void endDownload(String token, int writtenBytes) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(token));
        DBObject statRecord = db.getCollection("stat_download").findOne(query);
        Date startDate = (Date) statRecord.get("startDate");
        Date endDate = new Date(startDate.getTime() + Math.round((writtenBytes / SECOND_TO_BYTE) * 1000));
        statRecord.put("bytes", writtenBytes);
        statRecord.put("endDate", endDate);
        db.getCollection("stat_download").update(query, statRecord);

    }
}
