package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Episode;
import hu.radio.tilos.model.Tag;
import hu.radio.tilos.model.type.TagType;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.TextData;
import org.junit.Assert;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import javax.inject.Inject;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class EpisodeControllerTest {

    @Inject
    EpisodeController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void testGet() throws Exception {
        //given

        //when
        EpisodeData episode = controller.get(2);

        //then
        Assert.assertNotNull(episode.getText());
        Assert.assertEquals("Jo musor", episode.getText().getTitle());
        Assert.assertEquals("http://tilos.hu/mp3/tilos-20140411-080000-100000.m3u", episode.getM3uUrl());
    }


    @Test
    public void testGetByDate() throws Exception {
        //given

        //when
        EpisodeData episode = controller.getByDate("3utas", 2014, 04, 11);

        //then
        Assert.assertNotNull(episode.getText());
        Assert.assertEquals("Jo musor", episode.getText().getTitle());
    }

    @Test
    public void testCreateEpisode() throws Exception {
        //given
        EpisodeData episode = new EpisodeData();
        episode.setPlannedFrom(TestUtil.YYYYMMDDHHMM.parse("201405011200"));
        episode.setPlannedTo(TestUtil.YYYYMMDDHHMM.parse("201405011300"));

        ShowSimple simple = new ShowSimple();
        simple.setId(1);
        episode.setShow(simple);

        TextData td = new TextData();
        td.setTitle("Title");
        td.setContent("ahoj #teg ahoj");
        episode.setText(td);

        //when
        controller.getEntityManager().getTransaction().begin();
        CreateResponse createResponse = controller.create(episode);
        controller.getEntityManager().getTransaction().commit();

        //then
        Episode episodeEntity = controller.getEntityManager().find(Episode.class, createResponse.getId());
        Assert.assertNotNull(episodeEntity.getText());
        Assert.assertEquals("Title", episodeEntity.getText().getTitle());
        Assert.assertEquals(1, episodeEntity.getShow().getId());

        Tag tag = controller.getEntityManager().createQuery("SELECT t FROM Tag t WHERE t.name = :name", Tag.class).setParameter("name", "teg").getSingleResult();
        Assert.assertEquals(1, tag.getTaggedTexts().size());
        Assert.assertEquals("Title", tag.getTaggedTexts().get(0).getTitle());
    }

    @Test
    public void testUpdateEpisode() throws Exception {
        //given
        EpisodeData episode = controller.get(2);
        episode.setPlannedFrom(TestUtil.YYYYMMDDHHMM.parse("201405011200"));
        episode.setPlannedTo(TestUtil.YYYYMMDDHHMM.parse("201405011300"));

        episode.getText().setContent("ez jobb #kukac de a harom nincs @szemely is van");

        //when
        controller.getEntityManager().getTransaction().begin();
        UpdateResponse createResponse = controller.update(2, episode);
        controller.getEntityManager().getTransaction().commit();

        //then
        Episode episodeEntity = controller.getEntityManager().find(Episode.class, createResponse.getId());
        Assert.assertNotNull(episodeEntity.getText());
        Assert.assertEquals("ez jobb #kukac de a harom nincs @szemely is van", episodeEntity.getText().getContent());

        Tag tag = controller.getEntityManager().createNamedQuery("tag.byName",Tag.class).setParameter("name", "kukac").getSingleResult();
        Assert.assertEquals(2, tag.getTaggedTexts().size());

        tag = controller.getEntityManager().createNamedQuery("tag.byName",Tag.class).setParameter("name", "harom").getSingleResult();
        Assert.assertEquals(0, tag.getTaggedTexts().size());

        tag = controller.getEntityManager().createNamedQuery("tag.byName",Tag.class).setParameter("name", "szemely").getSingleResult();
        Assert.assertEquals(1, tag.getTaggedTexts().size());
        Assert.assertEquals(TagType.PERSON, tag.getType());

    }
}