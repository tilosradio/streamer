package hu.tilos.radio.backend.feed;

import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.ShowType;
import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import net.anzix.jaxrs.atom.Feed;
import net.anzix.jaxrs.atom.Link;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

/**
 * Generate atom feed for the shows.
 */
@Path("/feed")
public class FeedController {


    @Inject
    private EpisodeUtil episodeUtil;

    @Inject
    private DB db;


    @Inject
    private FeedRenderer feedRenderer;

    @Inject
    @Configuration(name = "server.url")
    private String serverUrl;

    @Inject
    private DozerBeanMapper mapper;

    @GET
    @Path("/weekly")
    @Security(role = Role.GUEST)
    @Produces("application/atom+xml")
    public Response weeklyFeed() {
        return weeklyFeed(null);
    }


    @GET
    @Path("/weekly/{type}")
    @Security(role = Role.GUEST)
    @Produces("application/atom+xml")
    public Response weeklyFeed(@PathParam("type") String type) {
        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - (long) 604800000L);

        List<EpisodeData> episodes = filter(episodeUtil.getEpisodeData(null, weekAgo, now), type);

        Collections.sort(episodes, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData episodeData, EpisodeData episodeData2) {
                return episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom());
            }
        });

        Feed feed = feedRenderer.generateFeed(episodes, true);


        feed.setTitle("Tilos Rádió heti podcast");
        feed.setUpdated(new Date());

        Link feedLink = new Link();
        feedLink.setRel("self");
        feedLink.setType(new MediaType("application", "atom+xml"));
        feedLink.setHref(uri(serverUrl + "/feed/weekly"));

        return Response.ok().entity(feed).build();
    }

    private List<EpisodeData> filter(List<EpisodeData> episodeData, String type) {
        if (type == null) {
            return episodeData;
        } else {
            List<EpisodeData> result = new ArrayList<>();
            for (EpisodeData data : episodeData) {
                if ((type.equals("talk") && data.getShow().getType() == ShowType.SPEECH) || (type.equals("music") && data.getShow().getType() == ShowType.SPEECH)) {
                    result.add(data);
                }
            }
            return result;
        }
    }

    private URI uri(String s) {
        try {
            return new URI(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL can't be converted", e);
        }
    }


    @GET
    @Path("/show/itunes/{alias}")
    @Security(role = Role.GUEST)
    @Produces("application/atom+xml")
    public Response itunesFeed(@PathParam("alias") String alias) {
        return feed(alias, null);
    }

    @GET
    @Path("/show/{alias}{year:(/.*)?}")
    @Security(role = Role.GUEST)
    @Produces("application/atom+xml")
    public Response feed(@PathParam("alias") String alias, @PathParam("year") String year) {
        //{year: (/.*)?
        //,
        if (year == null) {
            year = "";
        } else if (year.startsWith("/")) {
            year = year.substring(1);
        }

        ShowSimple show = mapper.map(db.getCollection("show").findOne(aliasOrId(alias)), ShowSimple.class);

        Date end;
        Date start;
        if ("".equals(year)) {
            end = getNow();
            //six monthes
            start = new Date();
            start.setTime(end.getTime() - (long) 60 * 24 * 30 * 6 * 60 * 1000);
        } else {
            int yearInt = Integer.parseInt(year);
            start = new Date(yearInt - 1900, 0, 1);
            end = new Date(yearInt - 1900 + 1, 0, 1);
        }

        List<EpisodeData> episodeData = episodeUtil.getEpisodeData("" + show.getId(), start, end);
        Collections.sort(episodeData, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData episodeData, EpisodeData episodeData2) {
                return episodeData2.getPlannedFrom().compareTo(episodeData.getPlannedFrom());
            }
        });


        Feed feed = feedRenderer.generateFeed(episodeData);

        //generate header

        feed.setTitle(show.getName() + " [Tilos Rádió podcast]");
        feed.setUpdated(new Date());

        String yearPostfix = ("".equals(year) ? "" : "/" + year);

        Link feedLink = new Link();
        feedLink.setRel("self");
        feedLink.setType(new MediaType("application", "atom+xml"));
        feedLink.setHref(uri(serverUrl + "/feed/show/" + show.getAlias() + yearPostfix));

        feed.getLinks().add(feedLink);
        feed.setId(uri("http://tilos.hu/show/" + show.getAlias() + yearPostfix));

        return Response.ok().entity(feed).build();


    }

    protected Date getNow() {
        return new Date();
    }

    public EpisodeUtil getEpisodeUtil() {
        return episodeUtil;
    }

    public void setEpisodeUtil(EpisodeUtil episodeUtil) {
        this.episodeUtil = episodeUtil;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

}
