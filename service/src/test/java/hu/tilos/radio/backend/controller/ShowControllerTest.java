package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.ShowStatus;
import hu.radio.tilos.model.type.ShowType;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.MongoProducer;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.input.ShowToSave;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowDetailed;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.UserDetailed;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class})
@ActivatedAlternatives(FongoCreator.class)
public class ShowControllerTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    @Inject
    ShowController controller;

    @Inject
    Session session;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testGet() throws Exception {
        //given
        String authorId = loadTo(fongoRule, "author", "author-author2.json");
        String showId = loadTo(fongoRule, "show", "show-3utas2.json", authorId);
        loadTo(fongoRule, "mix", "mix-1.json", showId);

        //when
        ShowDetailed show = controller.get("3utas");

        //then
        Assert.assertEquals("3utas", show.getAlias());
        Assert.assertEquals("3. utas", show.getName());

        Assert.assertEquals(1, show.getContributors().size());

        Assert.assertEquals("AUTHOR2", show.getContributors().get(0).getNick());
        Assert.assertEquals(authorId, show.getContributors().get(0).getAuthor().getId());

        Assert.assertEquals(1, show.getSchedulings().size());

        Assert.assertEquals(1, show.getStats().mixCount);

    }

    @Test
    public void testGetWithId() throws Exception {
        //given
        loadTo(fongoRule, "show", "show-3utas.json");

        //when
        ShowDetailed show = controller.get("1");

        //then
        Assert.assertEquals("3utas", show.getAlias());
        Assert.assertEquals("3. utas", show.getName());

        Assert.assertEquals(1, show.getSchedulings().size());

    }

    @Test
    public void testListEpisodes() throws ParseException {
        //given
        Date start = SDF.parse("201404010000");
        Date end = SDF.parse("201406010000");
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "episode", "episode-episode1.json", showId);
        loadTo(fongoRule, "episode", "episode-episode2.json", showId);

        //when
        List<EpisodeData> episodeDatas = controller.listEpisodes(showId, start.getTime(), end.getTime());

        //then
        Assert.assertEquals(9, episodeDatas.size());
    }

    @Test
    public void list() throws Exception {
        //given
        controller.setDb(fongoRule.getDB());
        loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "show", "show-vendeglo.json");


        //when
        List<ShowSimple> showSimples = controller.list(null);

        //then
        assertThat(showSimples.size(), equalTo(2));

    }

    @Test
    @InRequestScope
    public void update() throws Exception {
        //given
        controller.setDb(fongoRule.getDB());
        loadTo(fongoRule, "show", "show-3utas.json");
        UserDetailed detailed = new UserDetailed();
        detailed.setRole(Role.ADMIN);
        session.setCurrentUser(detailed);

        ShowToSave showToSave = new ShowToSave();
        showToSave.setType(ShowType.MUSIC);
        showToSave.setStatus(ShowStatus.ACTIVE);
        showToSave.setName("test");

        //when
        controller.update("3utas", showToSave);

        //then
        DBObject one = fongoRule.getDB().getCollection("show").findOne();
        Assert.assertThat((String) one.get("name"), equalTo("test"));
        Assert.assertEquals(0, one.get("type"));
        Assert.assertEquals(1, one.get("status"));
    }
//    @Test
//    public void checkPermission() throws Exception {
//        //given
//        EntityManager em = controller.getEntityManager();
//        Show show = em.find(Show.class, 1);
//        User user = em.find(User.class, 2);
//
//        //when
//        try {
//            controller.checkPermission(show, user);
//        } catch (IllegalArgumentException ex) {
//            fail();
//        }
//
//        //then
//
//
//    }

//    @Test
//    public void checkPermissionFailed() throws Exception {
//        //given
//
//        Show show = em.find(Show.class, 1);
//        User user = em.find(User.class, 1);
//
//        //when
//        try {
//            controller.checkPermission(show, user);
//            fail();
//        } catch (IllegalArgumentException ex) {
//
//        }
//
//        //then
//
//
//    }


    @Test
    public void listAll() throws Exception {
        //given
        loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "show", "show-vendeglo.json");

        //when
        List<ShowSimple> showSimples = controller.list("all");

        //then
        assertThat(showSimples.size(), equalTo(2));

    }
}