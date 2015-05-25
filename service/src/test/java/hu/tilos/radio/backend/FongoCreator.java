package hu.tilos.radio.backend;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.typesafe.config.ConfigFactory;

import java.net.UnknownHostException;

public class FongoCreator {

    private FongoRule fongoRule;

    private boolean embedded = false;

    public void init() {
        if (embedded) {
            fongoRule = new FongoRule();
        } else {
            try {
                String host = ConfigFactory.load().getString("mongo.host");
                fongoRule = new FongoRule("unit", true, new MongoClient(host));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public FongoRule createRule() {
        return fongoRule;
    }

    public DB createDB() {
        if (fongoRule == null) {
            init();
        }
        if (embedded) {
            return fongoRule.getFongo().getDB("test");
        } else {
            return fongoRule.getDB("unit");
        }
    }
}
