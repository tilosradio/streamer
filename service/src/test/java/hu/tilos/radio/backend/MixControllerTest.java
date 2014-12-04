package hu.tilos.radio.backend;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;
import hu.tilos.radio.backend.controller.MixController;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.MixData;
import hu.tilos.radio.backend.data.types.MixSimple;
import hu.tilos.radio.backend.data.types.ShowSimple;
import org.hamcrest.CustomMatcher;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static hu.tilos.radio.backend.MongoUtil.aliasOrId;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class MixControllerTest {

    @Inject
    MixController controller;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testGet() {
        //given
        loadTo(fongoRule, "mix", "mix-1.json");

        //when
        MixData r = controller.get("1");

        //then
        Assert.assertEquals("good mix", r.getTitle());
        Assert.assertNotNull(r.getShow());
        Assert.assertEquals(MixType.MUSIC.SPEECH, r.getType());
        Assert.assertEquals(MixCategory.SHOW, r.getCategory());
        Assert.assertEquals("3. utas", r.getShow().getName());
        Assert.assertNotNull(r.getShow().getId());
        Assert.assertEquals(r.getLink(),"http://archive.tilos.hu/sounds/asd.mp3");
    }


    @Test
    public void testList() {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "mix", "mix-1.json", showId);
        loadTo(fongoRule, "mix", "mix-2.json");

        //when
        List<MixSimple> responses = controller.list(null, null);

        //then
        Assert.assertEquals(2, responses.size());
        assertThat(responses, hasItem(new CustomMatcher<MixSimple>("Mix with show") {

            @Override
            public boolean matches(Object item) {
                try {
                    MixSimple mix = (MixSimple) item;
                    assertThat(item, notNullValue());
                    assertThat(mix.getShow(), notNullValue());
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        }));

    }

    @Test
    public void testListWithShowId() {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "mix", "mix-1.json", showId);

        //when
        List<MixSimple> responses = controller.list("3utas", null);

        //then
        Assert.assertEquals(1, responses.size());
    }


    @Test
    public void testCreate() {
        //given
        MixData r = new MixData();
        r.setAuthor("lajos");
        r.setTitle("new mix");
        r.setFile("lajos.mp3");
        r.setType(MixType.SPEECH);
        r.setCategory(MixCategory.DJ);


        //when
        CreateResponse response = controller.create(r);

        //then
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotEquals(0, response.getId());

        DBObject mix = fongoRule.getDB().getCollection("mix").findOne(aliasOrId(response.getId()));
        Assert.assertEquals("lajos", mix.get("author"));

    }


    @Test
    public void testUpdate() {
        //given
        String mix1Id = loadTo(fongoRule, "mix", "mix-1.json");
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        MixData req = controller.get(mix1Id);

        req.setTitle("this Is the title");
        req.setDate("2014-10-23");
        Assert.assertEquals(MixType.SPEECH, req.getType());
        req.setType(MixType.MUSIC);

        ShowSimple show = new ShowSimple();
        show.setId(showId);
        req.setShow(show);

        //when
        UpdateResponse response = controller.update(mix1Id, req);

        //then
        Assert.assertTrue(response.isSuccess());

        DBObject mix = fongoRule.getDB().getCollection("mix").findOne(aliasOrId(mix1Id));
        Assert.assertEquals("this Is the title", mix.get("title"));
        Assert.assertEquals(MixType.MUSIC.ordinal(), mix.get("type"));
        Assert.assertEquals(showId, ((DBRef) ((DBObject) mix.get("show")).get("ref")).getId());
    }


}