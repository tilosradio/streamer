package hu.tilos.radio.backend;

import hu.radio.tilos.model.Bookmark;
import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.BookmarkData;
import hu.tilos.radio.backend.data.BookmarkSimple;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.types.MixData;
import hu.tilos.radio.backend.data.types.MixSimple;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class BookmarkControllerTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd' 'HHmm");

    @Inject
    BookmarkController controller;

    @Before
    public void resetDatabase(){
        TestUtil.initTestData();
    }

    @Test
    public void testList() {
        //given

        //when
        List<BookmarkSimple> responses = controller.list(null);

        //then
        Assert.assertEquals(2, responses.size());
    }


    @Test
    public void testCreate() throws ParseException {
        //given
        BookmarkData data = new BookmarkData();
        data.title = "Beszélgetés az A38 állóhajóról";
        data.content = "x";
        data.realFrom = SDF.parse("20140516 1200");
        data.realTo = SDF.parse("20140516 1200");

        //when
        EntityManager em = controller.getEntityManager();

        em.getTransaction().begin();
        CreateResponse createResponse = controller.create(data);
        em.getTransaction().commit();

        //then
        Assert.assertNotEquals(0, createResponse.getId());
        Bookmark bookmark = em.find(Bookmark.class, createResponse.getId());
        Assert.assertEquals("Beszélgetés az A38 állóhajóról", bookmark.getTitle());

    }

}