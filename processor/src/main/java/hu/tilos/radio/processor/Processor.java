package hu.tilos.radio.processor;

import com.google.inject.*;
import com.mongodb.DB;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.MongoProducer;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import hu.tilos.radio.backend.episode.Merger;
import hu.tilos.radio.backend.episode.PersistentEpisodeProvider;
import hu.tilos.radio.backend.episode.ScheduledEpisodeProvider;
import org.dozer.DozerBeanMapper;

import java.util.Date;
import java.util.List;

public class Processor {
    public static void main(String[] args) {
        new Processor().run();
    }

    private DozerFactory dozerFactory;

    private void run() {
        final MongoProducer mongo = new MongoProducer();
        mongo.setDbName("tilos");
        mongo.init();

        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(PersistentEpisodeProvider.class);
                binder.bind(ScheduledEpisodeProvider.class);
                binder.bind(Merger.class);
                binder.bind(DozerFactory.class).asEagerSingleton();
                binder.bind(DB.class).toProvider(new Provider<DB>() {
                    @Override
                    public DB get() {
                        return mongo.getDb();
                    }
                });
                binder.bind(DozerBeanMapper.class).toProvider(new Provider<DozerBeanMapper>() {
                    @Override
                    public DozerBeanMapper get() {
                        return dozerFactory.mapperFactory();
                    }
                });
            }
        });
        dozerFactory = injector.getInstance(DozerFactory.class);
        dozerFactory.init();
        EpisodeUtil episodeUtil = injector.getInstance(EpisodeUtil.class);
        List<EpisodeData> episodeList = episodeUtil.getEpisodeData(null, new Date(new Date().getTime() - 30l * 24 * 60 * 60 * 1000), new Date());
        for (EpisodeData episodeData : episodeList){
            System.out.println("wget --no-check-certificate " + episodeData.getM3uUrl().replace("m3u","mp3"));
        }
    }
}
