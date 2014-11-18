package hu.tilos.radio.backend;

import hu.radio.tilos.model.Mix;
import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;
import hu.tilos.radio.backend.controller.MixController;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.types.MixData;
import hu.tilos.radio.backend.data.types.MixSimple;
import hu.tilos.radio.backend.data.types.ShowSimple;
import org.hamcrest.CustomMatcher;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class MixControllerTest {

    private static EntityManagerFactory factory;

    @Inject
    MixController controller;

    @BeforeClass
    public static void setUp() throws Exception {
        factory = TestUtil.initPersistence();
    }

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void testGet() {

        //given
        controller.setEntityManager(factory.createEntityManager());

        //when
        MixData r = controller.get(1);

        //then
        Assert.assertEquals("good mix", r.getTitle());
        Assert.assertNotNull(r.getShow());
        Assert.assertEquals(MixType.MUSIC.SPEECH, r.getType());
        Assert.assertEquals(MixCategory.SHOW, r.getCategory());
        Assert.assertEquals("3. utas", r.getShow().getName());
    }


    @Test
    public void testList() {

        //given
        controller.setEntityManager(factory.createEntityManager());

        //when
        List<MixSimple> responses = controller.list(null, null);

        //then
        Assert.assertEquals(3, responses.size());
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
        controller.setEntityManager(factory.createEntityManager());

        //when
        List<MixSimple> responses = controller.list("3utas", null);

        //then
        Assert.assertEquals(2, responses.size());
    }


    @Test
    public void testCreate() {

        //given
        EntityManager em = factory.createEntityManager();

        controller.setEntityManager(em);

        MixData r = new MixData();
        r.setAuthor("lajos");
        r.setTitle("new mix");
        r.setFile("lajos.mp3");
        r.setType(MixType.SPEECH);
        r.setCategory(MixCategory.DJ);

        ShowSimple showSimple = new ShowSimple();
        showSimple.setId(1);
        r.setShow(showSimple);

        //when
        em.getTransaction().begin();
        CreateResponse response = controller.create(r);
        em.getTransaction().commit();

        //then
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotEquals(0, response.getId());

        Mix mix = em.find(Mix.class, response.getId());
        Assert.assertEquals("lajos", mix.getAuthor());
        Assert.assertEquals(1, mix.getShow().getId());
        em.close();
    }


    @Test
    public void testUpdate() {

        //given
        EntityManager em = factory.createEntityManager();
        controller.setEntityManager(em);
        MixData req = controller.get(1);

        req.setTitle("this Is the title");
        req.setDate("2014-10-23");
        Assert.assertEquals(MixType.SPEECH, req.getType());
        req.setType(MixType.MUSIC);
        //req.setShow(new EntitySelector(2));

        //when
        em.getTransaction().begin();
        CreateResponse response = controller.update(1, req);
        em.getTransaction().commit();

        //then
        Assert.assertTrue(response.isSuccess());

        Mix mix = em.find(Mix.class, 1);
        Assert.assertEquals("this Is the title", mix.getTitle());
        Assert.assertEquals(9, mix.getDate().getMonth());
        Assert.assertEquals(23, mix.getDate().getDate());
        Assert.assertEquals(MixType.MUSIC, mix.getType());
        em.close();
    }


}