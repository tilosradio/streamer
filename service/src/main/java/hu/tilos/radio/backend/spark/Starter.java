package hu.tilos.radio.backend.spark;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.MongoProducer;
import hu.tilos.radio.backend.auth.AuthService;
import hu.tilos.radio.backend.auth.LoginData;
import hu.tilos.radio.backend.auth.PasswordReset;
import hu.tilos.radio.backend.auth.RegisterData;
import hu.tilos.radio.backend.author.AuthorService;
import hu.tilos.radio.backend.author.AuthorToSave;
import hu.tilos.radio.backend.contribution.ContributionService;
import hu.tilos.radio.backend.contribution.ContributionToSave;
import hu.tilos.radio.backend.episode.EpisodeService;
import hu.tilos.radio.backend.episode.EpisodeToSave;
import hu.tilos.radio.backend.feed.FeedService;
import hu.tilos.radio.backend.m3u.M3uService;
import hu.tilos.radio.backend.mix.MixData;
import hu.tilos.radio.backend.mix.MixService;
import hu.tilos.radio.backend.search.SearchService;
import hu.tilos.radio.backend.show.ShowService;
import hu.tilos.radio.backend.show.ShowToSave;
import hu.tilos.radio.backend.tag.TagService;
import hu.tilos.radio.backend.text.TextService;
import hu.tilos.radio.backend.text.TextToSave;
import hu.tilos.radio.backend.user.UserService;
import org.dozer.DozerBeanMapper;
import spark.Request;
import spark.Route;

import javax.validation.Validator;

import static spark.Spark.*;

public class Starter {

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
    M3uService m3uService;

    @Inject
    SearchService searchService;

    @Inject
    FeedService feedService;

    private Gson gson = new Gson();

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

        port(8080);

        before((request, response) -> {
            System.out.println(request.uri());
        });


        get("/api/v1/author", (req, res) -> authorService.list(), new JsonTransformer());
        get("/api/v1/author/:alias", (req, res) -> authorService.get(req.params("alias"), null), new JsonTransformer());
        post("/api/v1/author", authorized(Role.ADMIN, (req, res, session) -> authorService.create(gson.fromJson(req.body(), AuthorToSave.class))), new JsonTransformer());
        put("/api/v1/author/:alias",
                authorized("/author/{alias}", (req, res, session) ->
                        authorService.update(req.params("alias"), gson.fromJson(req.body(), AuthorToSave.class))), new JsonTransformer());

        get("/api/v1/show", (req, res) ->
                showService.list(req.queryParams("status")), new JsonTransformer());
        get("/api/v1/show/:alias", (req, res) ->
                showService.get(req.params("alias")), new JsonTransformer());
        get("/api/v1/show/:alias/episodes", (req, res) ->
                showService.listEpisodes(req.params("alias"),
                        Long.valueOf(req.queryParams("start")),
                        Long.valueOf(req.queryParams("end"))
                ), new JsonTransformer());
        post("/api/v1/show",
                authorized(Role.ADMIN, (req, res, session) ->
                        showService.create(gson.fromJson(req.body(), ShowToSave.class))), new JsonTransformer());
        put("/api/v1/show/:alias",
                authorized("/show/{alias}", (req, res, session) ->
                        showService.update(req.params("alias"), gson.fromJson(req.body(), ShowToSave.class))), new JsonTransformer());

        get("/api/v1/mix", (req, res) ->
                mixService.list(req.queryParams("show"), req.queryParams("category")), new JsonTransformer());
        get("/api/v1/mix/:id", (req, res) ->
                mixService.get(req.params("id")), new JsonTransformer());
        post("/api/v1/mix",
                authorized(Role.ADMIN, (req, res, session) ->
                        mixService.create(gson.fromJson(req.body(), MixData.class))), new JsonTransformer());
        put("/api/v1/mix/:alias",
                authorized(Role.ADMIN, (req, res, session) ->
                        mixService.update(req.params("alias"), gson.fromJson(req.body(), MixData.class))), new JsonTransformer());
        delete("/api/v1/mix/:alias",
                authorized(Role.ADMIN, (req, res, session) ->
                        mixService.delete(req.params("alias"))), new JsonTransformer());

        get("/api/v1/text/:type", (req, res) ->
                textService.list(req.params("type"), intParam(req, "limit")), new JsonTransformer());
        get("/api/v1/text/:type/:id", (req, res) ->
                textService.get(req.params("id"), req.params("type")), new JsonTransformer());
        post("/api/v1/text/:type",
                authorized(Role.ADMIN, (req, res, session) ->
                        textService.create(req.params("type"), gson.fromJson(req.body(), TextToSave.class))), new JsonTransformer());
        put("/api/v1/text/:type/:id",
                authorized(Role.ADMIN, (req, res, session) ->
                        textService.update(req.params("type"), req.params("id"), gson.fromJson(req.body(), TextToSave.class))), new JsonTransformer());

        get("/api/v1/tag/:tag", (req, res) ->
                tagService.get(req.params("tag")), new JsonTransformer());
        get("/api/v1/tag", (req, res) ->
                tagService.list(intParam(req, "limit")), new JsonTransformer());


        get("/api/v1/episode", (req, res) ->
                episodeService.listEpisodes(longParam(req, "start"), longParam(req, "end")), new JsonTransformer());
        get("/api/v1/episode/next", (req, res) ->
                episodeService.next(), new JsonTransformer());
        get("/api/v1/episode/last", (req, res) ->
                episodeService.last(), new JsonTransformer());
        get("/api/v1/episode/:id", (req, res) ->
                episodeService.get(req.params("id")), new JsonTransformer());
        get("/api/v1/episode//:show/:year/:month/:day", (req, res) ->
                episodeService.getByDate(req.params("show"), intParam(req, "year"), intParam(req, "month"), intParam(req, "day")), new JsonTransformer());
        post("/api/v1/episode",
                authorized(Role.ADMIN, (req, res, session) ->
                        episodeService.create(gson.fromJson(req.body(), EpisodeToSave.class))), new JsonTransformer());
        put("/api/v1/episode/:id",
                authorized(Role.ADMIN, (req, res, session) ->
                        episodeService.update(req.params("id"), gson.fromJson(req.body(), EpisodeToSave.class))), new JsonTransformer());

        post("/api/v1/auth/password_reset",
                (req, res) -> authService.passwordReset(gson.fromJson(req.body(), PasswordReset.class)), new JsonTransformer());
        post("/api/v1/auth/login",
                (req, res) -> authService.login(gson.fromJson(req.body(), LoginData.class)), new JsonTransformer());
        post("/api/v1/auth/register",
                (req, res) -> authService.register(gson.fromJson(req.body(), RegisterData.class)), new JsonTransformer());

        get("/api/v1/user/me", authorized(Role.USER, (req, res, session) ->
                userService.me(session)), new JsonTransformer());

        post("/api/int/contribution", authorized(Role.ADMIN, (req, res, session) ->
                contributionService.save(gson.fromJson(req.body(), ContributionToSave.class))), new JsonTransformer());
        delete("/api/int/contribution", authorized(Role.ADMIN, (req, res, session) ->
                contributionService.delete(req.params("author"), req.params("show"))), new JsonTransformer());

        get("/api/v1/m3u/lastweek", (req, res) -> {
            res.header("Content-Type", "audio/x-mpegurl; charset=iso-8859-2");
            return m3uService.lastWeek(req.queryParams("stream"));
        });

        get("/api/v1/search/query", (req, res) -> searchService.search(req.queryParams("q")));

        get("/feed/weekly", (req, res) -> {
            res.header("application", "atom+xml");
            return feedService.weeklyFeed();
        }, new FeedTransformer());
        get("/feed/weekly/:type", (req, res) -> {
            res.header("application", "atom+xml");
            return feedService.weeklyFeed(req.params("type"));
        }, new FeedTransformer());
        get("/feed/show/itunes/:alias", (req, res) -> {
            res.header("application", "atom+xml");
            return feedService.feed(req.params("alias"), null);
        }, new FeedTransformer());
        get("/feed/show/:alias/:year", (req, res) -> {
            res.header("application", "atom+xml");
            return feedService.feed(req.params("alias"), req.params("year"));
        }, new FeedTransformer());


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
