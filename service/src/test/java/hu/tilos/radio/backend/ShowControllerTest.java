package hu.tilos.radio.backend;

import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowDetailed;
import hu.tilos.radio.backend.data.types.ShowSimple;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class ShowControllerTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    @Inject
    ShowController controller;


    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void testGet() throws Exception {
        //given

        //when
        ShowDetailed show = controller.get("3utas");

        //then
        Assert.assertEquals("3utas", show.getAlias());
        Assert.assertEquals("3. utas", show.getName());

        Assert.assertEquals(2, show.getMixes().size());
        Assert.assertEquals("http://archive.tilos.hu/sounds/asd.mp3", show.getMixes().get(0).getLink());

        Assert.assertEquals(2, show.getContributors().size());

        Assert.assertEquals("AUTHOR1", show.getContributors().get(0).getAuthor().getName());
        Assert.assertEquals("http://tilos.hu/upload/avatar/asd.jpg", show.getContributors().get(0).getAuthor().getAvatar());

        Assert.assertEquals(1, show.getSchedulings().size());
        Assert.assertEquals(2, show.getStats().mixCount);

        //       Assert.assertEquals("minden szombat 8:00-10:00",show.getSchedulings().get(0).getText());
    }

    @Test
    public void testGetWithId() throws Exception {
        //given

        //when
        ShowDetailed show = controller.get("1");

        //then
        Assert.assertEquals("3utas", show.getAlias());
        Assert.assertEquals("3. utas", show.getName());

        Assert.assertEquals(2, show.getMixes().size());
        Assert.assertEquals("http://archive.tilos.hu/sounds/asd.mp3", show.getMixes().get(0).getLink());

        Assert.assertEquals(2, show.getContributors().size());

        Assert.assertEquals("AUTHOR1", show.getContributors().get(0).getAuthor().getName());
        Assert.assertEquals("http://tilos.hu/upload/avatar/asd.jpg", show.getContributors().get(0).getAuthor().getAvatar());

        Assert.assertEquals(1, show.getSchedulings().size());
        Assert.assertEquals(2, show.getStats().mixCount);

        //       Assert.assertEquals("minden szombat 8:00-10:00",show.getSchedulings().get(0).getText());
    }

    @Test
    public void testListEpisodes() throws ParseException {
        //given
        Date start = SDF.parse("201404010000");
        Date end = SDF.parse("201406010000");

        //when
        List<EpisodeData> episodeDatas = controller.listEpisodes("1", start.getTime(), end.getTime());

        //then
        Assert.assertEquals(9, episodeDatas.size());
    }

    @Test
    public void list() throws Exception {
        //given


        //when
        List<ShowSimple> showSimples = controller.list(null);

        //then
        assertThat(showSimples.size(), equalTo(3));

    }

    @Test
    public void listAll() throws Exception {
        //given


        //when
        List<ShowSimple> showSimples = controller.list("all");

        //then
        assertThat(showSimples.size(), equalTo(4));

    }
}