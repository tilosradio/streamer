package hu.tilos.radio.backend.tag;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.GuiceRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


public class TagServiceTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    TagService service;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void get() {
        //given
        loadTo(fongoRule, "episode", "episode-episode4.json");

        //when
        TaggedElementList tagged = service.get("bela");

        //then
        Assert.assertEquals(1, tagged.getTagged().size());
        TaggedEpisode taggedEpisode = tagged.getTagged().get(0);
        Assert.assertEquals("3. utas", taggedEpisode.getShowName());
    }

    @Test
    public void list() {
        //given
        loadTo(fongoRule, "episode", "episode-episode4.json");

        //when
        TagCloud list = service.list(null);

        //then
        Assert.assertEquals(2, list.getTags().size());

    }
}