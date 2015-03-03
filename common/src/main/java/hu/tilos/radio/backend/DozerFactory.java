package hu.tilos.radio.backend;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import hu.tilos.radio.backend.converters.*;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@ApplicationScoped
public class DozerFactory {

    @Inject
    ContentCleaner cleaner;

    @Inject
    DB db;

    private DozerBeanMapper mapper;

    public static BasicDBObject createDbObject() {
        return new BasicDBObject();
    }

    @Produces
    DozerBeanMapper mapperFactory() {
        return mapper;
    }

    @PostConstruct
    public void init() {
        ArrayList<String> mappingFiles = new ArrayList<String>();
        mappingFiles.add("dozer.xml");
        mapper = new DozerBeanMapper(mappingFiles);
        Map<String, CustomConverter> converters = new HashMap<>();
        converters.put("uploadUrl", new PrefixingConverter("https://tilos.hu/upload/"));
        converters.put("contentCleaner", cleaner);
        converters.put("showReference", new ReferenceEncoder(db, "show", new String[]{"alias", "name"}));
        converters.put("authorReference", new ReferenceEncoder(db, "author", new String[]{"alias", "name"}));
        converters.put("childEncoder", new MongoObjectEncoder(mapper));
        converters.put("childListEncoder", new MongoListEncoder(mapper));
        converters.put("resolvedReferenceDecoder", new ResolvedReferenceDecoder(db, mapper));
        converters.put("soundLink", new PrefixingConverter("http://archive.tilos.hu/sounds/", "http"));
        mapper.setCustomConvertersWithId(converters);
    }
}
