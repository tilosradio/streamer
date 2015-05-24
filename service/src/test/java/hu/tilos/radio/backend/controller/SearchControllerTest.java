package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.search.SearchResponse;
import hu.tilos.radio.backend.search.SearchResponseElement;
import hu.tilos.radio.backend.search.SearchService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class SearchControllerTest {

    @Inject
    SearchService controller;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void test() throws IOException, ParseException {
        //given
        loadTo(fongoRule,"show","show-3utas.json");
        loadTo(fongoRule,"page","page-page1.json");

        //when
        SearchResponse respo = controller.search("tamogatas");

        //then
        Assert.assertEquals(1, respo.getElements().size());
        SearchResponseElement searchResponseElement = respo.getElements().get(0);
        Assert.assertEquals("asd", searchResponseElement.getAlias());
    }

}