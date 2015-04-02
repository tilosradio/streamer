package hu.tilos.radio.backend;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import hu.tilos.radio.backend.converters.*;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


@ApplicationScoped
public class DozerFactory {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(DozerFactory.class);

    @Inject
    ContentCleaner cleaner;

    @Inject
    DB db;

    private DozerBeanMapper mapper;

    public static BasicDBObject createDbObject() {
        return new BasicDBObject();
    }

    @Produces
    public DozerBeanMapper mapperFactory() {
        return mapper;
    }

    @PostConstruct
    public void init() {

        List<String> mappingFiles = detectMappings();
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

    protected List<String> detectMappings() {
        List<String> result = new ArrayList<>();
        LOG.debug("searching for dozer.file");
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("dozer.file");
            if (resources.hasMoreElements()) {
                LOG.warn("No dozer.file on the classpath");
            }
            while (resources.hasMoreElements()) {
                InputStream is = resources.nextElement().openStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\n");
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.trim().length() > 0) {
                        result.add(line);
                        LOG.info("Adding dozer definition: " + line);
                    }
                }
                is.close();
            }
        } catch (Exception e) {
            LOG.error("Error during dozer file detenction", e);
        }
        return result;

    }
}
