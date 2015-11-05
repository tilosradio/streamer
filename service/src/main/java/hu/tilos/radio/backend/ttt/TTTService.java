package hu.tilos.radio.backend.ttt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.util.NaturalDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TTTService {

    @Configuration(name = "couchdb.url")
    private String couchDBhost;

    Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new NaturalDeserializer()).create();

    public TTTService() {
    }

    public Object[] getAllBusiness() {
        URL oracle = null;
        try {
            oracle = getUrl();
            try (BufferedReader in =
                         createBufferedReader(oracle)) {
                Class<ArrayList> arrayListClass = ArrayList.class;

                Map<String, Object> result = (Map<String, Object>) gson.fromJson(in, Object.class);
                Object[] rows = (Object[]) result.get("rows");
                return Arrays.stream(rows).map(o -> ((Map) o).get("doc")).map(o -> {
                    Map<String, Object> element = (Map<String, Object>) o;
                    element.put("id", element.get("_id"));
                    element.remove("_id");
                    element.remove("_rev");
                    return element;
                }).toArray();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }


    }

    protected URL getUrl() throws MalformedURLException {
        return new URL(couchDBhost + "/ttt-business/_all_docs?include_docs=true");
    }

    protected BufferedReader createBufferedReader(URL oracle) throws IOException {
        return new BufferedReader(new InputStreamReader(oracle.openStream()));
    }
}
