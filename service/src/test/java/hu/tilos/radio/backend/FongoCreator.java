package hu.tilos.radio.backend;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class FongoCreator {

    private FongoRule fongoRule;

    private boolean embedded = false;

    public void init() {
        if (embedded) {
            fongoRule = new FongoRule();
        } else {
            try {
                fongoRule = new FongoRule("unit", true, new MongoClient("localhost"));
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
            return fongoRule.getDB();
        } else {
            return fongoRule.getDB("unit");
        }
    }
}
