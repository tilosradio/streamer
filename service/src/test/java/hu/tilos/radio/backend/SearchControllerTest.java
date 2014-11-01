package hu.tilos.radio.backend;

import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.SearchResponse;
import hu.tilos.radio.backend.data.SearchResponseElement;
import org.apache.lucene.queryparser.classic.ParseException;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.Persistence;
import java.io.IOException;

import static org.junit.Assert.*;

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