package hu.tilos.radio.backend.streamer;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.Date;

public class StatToMongo {

    @Inject
    DB db;

    public String startDownload(Date startDate) {
        BasicDBObject newRecord = new BasicDBObject("startDate", startDate);
        WriteResult result = db.getCollection("stat_download").insert(newRecord);
        return "" + newRecord.getString("_id");
    }

    public void seek(String token, int position) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(token));
        DBObject statRecord = db.getCollection("stat_download").findOne(query);
        statRecord.put("position", position);
        db.getCollection("stat_download").update(query, statRecord);
    }

    public void endDownload(String token, int writtenBytes) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(token));
        DBObject statRecord = db.getCollection("stat_download").findOne(query);
        statRecord.put("bytes", writtenBytes);
        db.getCollection("stat_download").update(query, statRecord);

    }
}
