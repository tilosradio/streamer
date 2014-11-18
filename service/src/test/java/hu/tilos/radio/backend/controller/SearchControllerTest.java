package hu.tilos.radio.backend.controller;

import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.controller.SearchController;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.output.SearchResponse;
import hu.tilos.radio.backend.data.output.SearchResponseElement;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class SearchControllerTest {

    @Inject
    SearchController controller;

    @Before
    public void resetDatabase(){
        TestUtil.initTestData();
    }

    @Test
    public void test() throws IOException, ParseException {
        //given

        //when
        SearchResponse respo = controller.search("tamogatas");

        //then
        Assert.assertEquals(1, respo.getElements().size());
        SearchResponseElement searchResponseElement = respo.getElements().get(0);
        Assert.assertEquals("asd", searchResponseElement.getAlias());
    }

}