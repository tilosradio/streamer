package hu.tilos.radio.backend.controller;

import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.TagType;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.output.TagCloud;
import hu.tilos.radio.backend.data.output.TagCloudElement;
import hu.tilos.radio.backend.data.output.TaggedElementList;
import hu.tilos.radio.backend.data.output.TaggedEpisode;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Date;

@Path("api/v1/tag")
public class TagController {

    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Inject
    DozerBeanMapper mapper;

    @Inject
    private DB db;

    @GET
    @Path("/{tag}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public TaggedElementList get(@PathParam("tag") String tag) {
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

    @GET
    @Path("/")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public TagCloud list(@QueryParam("limit") Integer limit) {
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
