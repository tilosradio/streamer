package hu.tilos.radio.backend.spark;

import com.google.gson.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.ShowType;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.MongoProducer;
import hu.tilos.radio.backend.Smoketest;
import hu.tilos.radio.backend.auth.AuthService;
import hu.tilos.radio.backend.auth.LoginData;
import hu.tilos.radio.backend.auth.PasswordReset;
import hu.tilos.radio.backend.auth.RegisterData;
import hu.tilos.radio.backend.author.AuthorService;
import hu.tilos.radio.backend.author.AuthorToSave;
import hu.tilos.radio.backend.bookmark.BookmarkService;
import hu.tilos.radio.backend.bookmark.BookmarkToSave;
import hu.tilos.radio.backend.comment.CommentService;
import hu.tilos.radio.backend.comment.CommentToSave;
import hu.tilos.radio.backend.comment.CommentType;
import hu.tilos.radio.backend.contribution.ContributionService;
import hu.tilos.radio.backend.contribution.ContributionToSave;
import hu.tilos.radio.backend.controller.internal.OauthService;
import hu.tilos.radio.backend.data.error.AccessDeniedException;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.error.ValidationException;
import hu.tilos.radio.backend.data.response.ErrorResponse;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.episode.EpisodeToSave;
import hu.tilos.radio.backend.feed.FeedService;
import hu.tilos.radio.backend.m3u.M3uService;
import hu.tilos.radio.backend.mix.MixData;
import hu.tilos.radio.backend.mix.MixService;
import hu.tilos.radio.backend.search.SearchService;
import hu.tilos.radio.backend.show.MailToShow;
import hu.tilos.radio.backend.show.ShowService;
import hu.tilos.radio.backend.show.ShowToSave;
import hu.tilos.radio.backend.stat.StatController;
import hu.tilos.radio.backend.status.StatusService;
import hu.tilos.radio.backend.tag.TagService;
import hu.tilos.radio.backend.text.TextService;
import hu.tilos.radio.backend.text.TextToSave;
import hu.tilos.radio.backend.user.UserService;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Route;

import javax.validation.Validator;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import static spark.Spark.*;

public class Starter {

    private static final Logger LOG = LoggerFactory.getLogger(Starter.class);

    @Inject
    AuthorService authorService;

    @Inject
    TextService textService;

    @Inject
    ShowService showService;

    @Inject
    TagService tagService;

    @Inject
    EpisodeService episodeService;

    @Inject
    MixService mixService;

    @Inject
    AuthService authService;

    @Inject
    UserService userService;

    @Inject
    ContributionService contributionService;

    @Inject
    StatController statController;

    @Inject
    M3uService m3uService;

    @Inject
    SearchService searchService;

    @Inject
    FeedService feedService;

    @Inject
    StatusService statusService;

    @Inject
    CommentService commentService;

    @Inject
    OauthService oauthService;

    @Inject
    BookmarkService bookmarkService;

    @Inject
    Smoketest smoketestService;

    private Gson gson;

    static Injector injector;

    public static void main(String[] args) {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DB.class).toProvider(MongoProducer.class);
                bind(DozerBeanMapper.class).toProvider(DozerFactory.class).asEagerSingleton();
                bind(Validator.class).toProvider(hu.tilos.radio.backend.ValidatorProducer.class);
                bindListener(Matchers.any(), new GuiceConfigurationListener());

            }
        });
        injector.getInstance(Starter.class).run();
    }

    private void run() {
        LOG.info("Starting new deployment");

        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date src, Type type, JsonSerializationContext jsonSerializationContext) {
                        return src == null ? null : new JsonPrimitive(src.getTime());
                    }
                })
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                        return json == null ? null : new Date(json.getAsLong());
                    }
                }).registerTypeAdapter(ShowType.class, new JsonSerializer<ShowType>() {

                    @Override
                    public JsonElement serialize(ShowType showType, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(showType.name());
                    }
                })
                .create();


        port(8080);

        before((request, response) -> {
            LOG.info(request.uri());
        });
        before((request, response) -> response.type("application/json"));

        exception(NotFoundException.class, (e, request, response) -> {
            response.status(404);
            response.body(gson.toJson(new ErrorResponse(e.getMessage())));
        });

        exception(AccessDeniedException.class, (e, request, response) -> {
            response.status(403);
            response.body(gson.toJson(new ErrorResponse("Hibás user név vagy jelszó")));
        });

        exception(NullPointerException.class, (e, request, response) -> {
            LOG.error("Error", e);
            response.status(500);
            response.body(gson.toJson(new ErrorResponse("Alkalmazás hiba történt, kérlek írj a webmester@tilos.hu címre.")));
        });

        exception(IllegalArgumentException.class, (e, request, response) -> {
            LOG.error("Illegal argument", e);
            response.status(400);
            response.body(gson.toJson(new ErrorResponse(e.getMessage())));
        });

        exception(ValidationException.class, (e, request, response) -> {
            response.status(400);
            response.body(gson.toJson(new ErrorResponse(e.getMessage())));
        });


        JsonTransformer jsonResponse = new JsonTransformer(gson);

        get("/api/v1/author", (req, res) -> authorService.list(), jsonResponse);
        get("/api/v1/author/:alias", (req, res) -> authorService.get(req.params("alias"), null), jsonResponse);
        post("/api/v1/author", authorized(Role.ADMIN, (req, res, session) -> authorService.create(gson.fromJson(req.body(), AuthorToSave.class))), jsonResponse);
        put("/api/v1/author/:alias",
                authorized("/author/{alias}", (req, res, session) ->
                        authorService.update(req.params("alias"), gson.fromJson(req.body(), AuthorToSave.class))), jsonResponse);

        get("/api/v1/show", (req, res) ->
                showService.list(req.queryParams("status")), jsonResponse);
        get("/api/v1/show/:alias", (req, res) ->
                showService.get(req.params("alias")), jsonResponse);
        get("/api/v1/show/:alias/episodes", (req, res) ->
                showService.listEpisodes(req.params("alias"),
                        Long.valueOf(req.queryParams("start")),
                        Long.valueOf(req.queryParams("end"))
                ), jsonResponse);
        post("/api/v1/show",
                authorized(Role.ADMIN, (req, res, session) ->
                        showService.create(gson.fromJson(req.body(), ShowToSave.class))), jsonResponse);
        put("/api/v1/show/:alias",
                authorized("/show/{alias}", (req, res, session) ->
                        showService.update(req.params("alias"), gson.fromJson(req.body(), ShowToSave.class))), jsonResponse);

        post("/api/v1/show/:alias/contact", (req, res) ->
                showService.contact(req.params("alias"), gson.fromJson(req.body(), MailToShow.class)), jsonResponse);


        get("/api/v1/test/ping", (req, res) ->
                smoketestService.ping(), jsonResponse);

        get("/api/v1/mix", (req, res) ->
                mixService.list(req.queryParams("show"), req.queryParams("category")), jsonResponse);
        get("/api/v1/mix/:id", (req, res) ->
                mixService.get(req.params("id")), jsonResponse);
        post("/api/v1/mix",
                authorized(Role.ADMIN, (req, res, session) ->
                        mixService.create(gson.fromJson(req.body(), MixData.class))), jsonResponse);
        put("/api/v1/mix/:alias",
                authorized(Role.ADMIN, (req, res, session) ->
                        mixService.update(req.params("alias"), gson.fromJson(req.body(), MixData.class))), jsonResponse);
        delete("/api/v1/mix/:alias",
                authorized(Role.ADMIN, (req, res, session) ->
                        mixService.delete(req.params("alias"))), jsonResponse);

        get("/api/v1/text/:type", (req, res) ->
                textService.list(req.params("type"), intParam(req, "limit"), booleanParam(req, "full")), jsonResponse);

        get("/api/v1/text/:type/:id", (req, res) ->
                textService.get(req.params("id"), req.params("type")), jsonResponse);

        post("/api/v1/text/:type",
                authorized(Role.ADMIN, (req, res, session) ->
                        textService.create(req.params("type"), gson.fromJson(req.body(), TextToSave.class))), jsonResponse);
        put("/api/v1/text/:type/:id",
                authorized(Role.ADMIN, (req, res, session) ->
                        textService.update(req.params("type"), req.params("id"), gson.fromJson(req.body(), TextToSave.class))), jsonResponse);

        delete("/api/v1/text/:type/:id",
                authorized(Role.ADMIN, (req, res, session) ->
                        textService.delete(req.params("type"), req.params("id"))), jsonResponse);

        get("/api/v1/tag/:tag", (req, res) ->
                tagService.get(req.params("tag")), jsonResponse);
        get("/api/v1/tag", (req, res) ->
                tagService.list(intParam(req, "limit")), jsonResponse);

        get("/api/v1/comment", (req, res) -> commentService.listAll(req.queryParams("status")), jsonResponse);


        post("/api/v1/comment/:id/approve", authorized(Role.AUTHOR,
                (req, res, session) -> commentService.approve(req.params("id")))
                , jsonResponse);

        delete("/api/v1/comment/:id/approve", authorized(Role.AUTHOR,
                (req, res, session) -> commentService.approve(req.params("id")))
                , jsonResponse);

        get("/api/v1/:type/:id/comment", authorized(Role.GUEST,
                (req, res, session) -> commentService.list(CommentType.valueOf(req.params("type")), req.params("id"), session)), jsonResponse);

        post("/api/v1/:type/:id/comment", authorized(Role.USER,
                (req, res, session) -> commentService.create(
                        CommentType.valueOf(req.params("type")),
                        req.params("id"),
                        gson.fromJson(req.body(), CommentToSave.class),
                        session))
                , jsonResponse);


        post("/api/v1/episode/:id/bookmark", authorized(Role.ADMIN, (req, res, session) ->
                bookmarkService.create(session, req.params("id"), gson.fromJson(req.body(), BookmarkToSave.class))), jsonResponse);

        get("/api/v1/episode", (req, res) ->
                episodeService.listEpisodes(longParam(req, "start"), longParam(req, "end")), jsonResponse);
        get("/api/v1/episode/next", (req, res) ->
                episodeService.next(), jsonResponse);
        get("/api/v1/episode/last", (req, res) ->
                episodeService.last(), jsonResponse);
        get("/api/v1/episode/lastWeek", (req, res) ->
                episodeService.lastWeek(), jsonResponse);
        get("/api/v1/episode/now", (req, res) ->
                episodeService.now(), jsonResponse);
        get("/api/v1/episode/:id", (req, res) ->
                episodeService.get(req.params("id")), jsonResponse);
        get("/api/v1/episode/:show/:year/:month/:day", (req, res) ->
                        episodeService.getByDate(req.params("show"),
                                Integer.parseInt(req.params("year")),
                                Integer.parseInt(req.params("month")),
                                Integer.parseInt(req.params("day"))),
                jsonResponse);
        post("/api/v1/episode",
                authorized(Role.AUTHOR, (req, res, session) ->
                        episodeService.create(gson.fromJson(req.body(), EpisodeToSave.class))), jsonResponse);
        put("/api/v1/episode/:id",
                authorized(Role.AUTHOR, (req, res, session) ->
                        episodeService.update(req.params("id"), gson.fromJson(req.body(), EpisodeToSave.class))), jsonResponse);

        post("/api/v1/auth/password_reset",
                (req, res) -> authService.passwordReset(gson.fromJson(req.body(), PasswordReset.class)), jsonResponse);
        post("/api/v1/auth/login",
                (req, res) -> authService.login(gson.fromJson(req.body(), LoginData.class)), jsonResponse);
        post("/api/v1/auth/register",
                (req, res) -> authService.register(gson.fromJson(req.body(), RegisterData.class)), jsonResponse);

        get("/api/v1/user/me", authorized(Role.USER, (req, res, session) ->
                userService.me(session)), jsonResponse);

        get("/api/int/user", authorized(Role.ADMIN, (req, res, session) ->
                userService.list()), jsonResponse);

        post("/api/int/contribution", authorized(Role.ADMIN, (req, res, session) ->
                contributionService.save(gson.fromJson(req.body(), ContributionToSave.class))), jsonResponse);
        delete("/api/int/contribution", authorized(Role.ADMIN, (req, res, session) ->
                contributionService.delete(req.queryParams("author"), req.queryParams("show"))), jsonResponse);

        get("/api/v1/m3u/lastweek", (req, res) -> {
            return asM3u(res, m3uService.lastWeek(req.queryParams("stream"), req.queryParams("type")));
        });

        get("/api/v1/search/query", (req, res) -> searchService.search(req.queryParams("q")), jsonResponse);

        get("/api/v1/status/radio", (req, res) -> statusService.getLiveSources(), jsonResponse);

        get("/api/v1/status/radio.txt", (req, res) -> statusService.getLiveSources(), new ResponseTransformer() {
            @Override
            public String render(Object model) throws Exception {
                StringBuilder result = new StringBuilder();
                List<String> list = (List<String>) model;
                for (String line : list) {
                    result.append(line + "\n");
                }
                return result.toString();
            }
        });

        get("/api/v1/stat/summary", (req, res) -> statController.getSummary(), jsonResponse);

        get("/api/v1/stat/listener", (req, res) -> statController.getListenerSTat(longParam(req, "from"), longParam(req, "to")), jsonResponse);

        get("/feed/weekly", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.weeklyFeed();
        }, new FeedTransformer());
        get("/feed/weekly/:type", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.weeklyFeed(req.params("type"));
        }, new FeedTransformer());
        get("/feed/show/itunes/:alias", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.feed(req.params("alias"), null);
        }, new FeedTransformer());
        get("/feed/show/:alias", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.feed(req.params("alias"), null);
        }, new FeedTransformer());
        get("/feed/show/:alias/:year", (req, res) -> {
            res.type("application/atom+xml");
            return feedService.feed(req.params("alias"), req.params("year"));
        }, new FeedTransformer());


        post("/api/int/oauth/facebook", (req, res) -> oauthService.facebook(gson.fromJson(req.body(), OauthService.FacebookRequest.class)), jsonResponse);
    }

    private boolean booleanParam(Request req, String param) {
        String full = req.queryParams(param);
        if (full != null) {
            return Boolean.parseBoolean(full);
        } else {
            return false;
        }
    }

    private Object asM3u(Response res, String output) throws Exception {
        res.type("audio/x-mpegurl; charset=iso-8859-2");
        try (OutputStreamWriter writer = new OutputStreamWriter(res.raw().getOutputStream(), Charset.forName("ISO-8859-2"))) {
            writer.write(output);
        }
        return null;
    }

    private Integer intParam(Request req, String name) {
        if (req.queryParams(name) == null) {
            return null;
        } else {
            return Integer.parseInt(req.queryParams(name));
        }
    }

    private Long longParam(Request req, String name) {
        if (req.queryParams(name) == null) {
            return null;
        } else {
            return Long.parseLong(req.queryParams(name));
        }
    }


    private Route authorized(Role role, AuthorizedRoute authorizedRoute) {
        Authorized authorized = new Authorized(role, authorizedRoute);
        injector.injectMembers(authorized);
        return authorized;
    }

    private Route authorized(String permission, AuthorizedRoute authorizedRoute) {
        Authorized authorized = new Authorized(permission, authorizedRoute);
        injector.injectMembers(authorized);
        return authorized;
    }

}
