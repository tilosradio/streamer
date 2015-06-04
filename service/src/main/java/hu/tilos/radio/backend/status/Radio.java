package hu.tilos.radio.backend.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Radio {

    private Gson gson = new GsonBuilder().create();

    public List<String> getLiveSources() {
        try (InputStreamReader urlReader = new InputStreamReader(new URL("http://stream.tilos.hu/status-json.xsl").openStream())) {
            IcecastResult icecastResult = gson.fromJson(urlReader, IcecastResult.class);

            List<String> streams = icecastResult.getIcestats().getSource().stream()
                    .map(source -> source.getListenurl())
                    .map(source -> source.substring(source.lastIndexOf('/') + 1))
                    .filter(t -> !t.contains("digital"))
                    .filter(t -> !t.contains("tordas"))
                    .filter(t -> !t.contains("tilos_"))
                    .filter(t -> !t.equals("tilos"))
                    .collect(toList());
            return streams;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
