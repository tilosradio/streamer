package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.output.TagCloud;
import hu.tilos.radio.backend.data.output.TaggedElementList;
import hu.tilos.radio.backend.data.output.TaggedEpisode;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class TagControllerTest {

    @Inject
    TagController controller;

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
        TaggedElementList tagged = controller.get("bela");

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
        TagCloud list = controller.list(null);

        //then
        Assert.assertEquals(2, list.getTags().size());

    }
}