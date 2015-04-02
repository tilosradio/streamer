package hu.tilos.radio.backend.tag;

import com.mongodb.*;
import hu.radio.tilos.model.type.TagType;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;

public class TagService {

    private static final Logger LOG = LoggerFactory.getLogger(TagService.class);

    @Inject
    DozerBeanMapper mapper;

    @Inject
    private DB db;

    public TaggedElementList get(String tag) {
        TaggedElementList list = new TaggedElementList();
        DBCursor episodes = db.getCollection("episode").find(new BasicDBObject("tags.name", tag));
        for (DBObject episode : episodes) {
            TaggedEpisode taggedEpisode = new TaggedEpisode();
            taggedEpisode.setPlannedFrom((Date) episode.get("plannedFrom"));
            DBObject showObject = (DBObject) episode.get("show");
            DBObject textObject = (DBObject) episode.get("text");
            taggedEpisode.setShowName((String) showObject.get("name"));
            taggedEpisode.setShowAlias((String) showObject.get("alias"));
            taggedEpisode.setTitle((String) textObject.get("title"));
            list.add(taggedEpisode);
        }
        return list;
    }

    public TagCloud list(Integer limit) {
        if (limit == null) {
            limit = 10;
        }
        if (limit > 150) {
            limit = 150;
        }
        String map = "function() {\n" +
                "    if (!this.tags) {\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    for (index in this.tags) {\n" +
                "        emit(this.tags[index], 1);\n" +
                "    }\n" +
                "}\n";
        String reduce = "function(previous, current) {     var count = 0;      for (index in current) {         count += current[index];     }      return count; }";
        MapReduceCommand cmd = new MapReduceCommand(db.getCollection("episode"), map, reduce, "tags", MapReduceCommand.OutputType.REPLACE, null);
        MapReduceOutput tags = db.getCollection("episode").mapReduce(cmd);

        LOG.debug("Recalculated tags: " + tags.getOutputCount());

        TagCloud result = new TagCloud();

        for (DBObject tagResult : db.getCollection("tags").find().sort(new BasicDBObject("value", -1)).limit(limit)) {
            BasicDBObject id = (BasicDBObject) tagResult.get("_id");
            TagCloudElement element = new TagCloudElement();
            element.setName((String) id.get("name"));
            element.setType(TagType.values()[((int) id.get("type"))]);
            element.setCount(((Double) tagResult.get("value")).intValue());
            result.add(element);
        }
        return result;
    }

}
