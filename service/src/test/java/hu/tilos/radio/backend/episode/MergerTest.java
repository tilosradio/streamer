package hu.tilos.radio.backend.episode;


import hu.tilos.radio.backend.episode.util.Merger;
import hu.tilos.radio.backend.text.TextData;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MergerTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Test
    public void testMerge() {
        //given
        Merger m = new Merger();

        List<EpisodeData> a = new ArrayList<>();

        EpisodeData d = new EpisodeData();
        d.setPersistent(true);
        d.setPlannedFrom(date(100000));
        a.add(d);

        d = new EpisodeData();
        d.setPersistent(true);
        d.setPlannedFrom(date(200000));
        a.add(d);


        List<EpisodeData> b = new ArrayList<>();
        d = new EpisodeData();
        d.setPersistent(false);
        d.setPlannedFrom(date(100000));
        b.add(d);

        d = new EpisodeData();
        d.setPersistent(false);
        d.setPlannedFrom(date(300000));
        b.add(d);


        //when
        List<EpisodeData> result = m.merge(a, b);

        //then
        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.get(0).isPersistent());
        Assert.assertTrue(result.get(1).isPersistent());
        Assert.assertFalse(result.get(2).isPersistent());

        Assert.assertEquals(date(100000), result.get(0).getPlannedFrom());
        Assert.assertEquals(date(200000), result.get(1).getPlannedFrom());

    }

    @Test
    public void adjustTimes() throws Exception {
        //given
        Merger m = new Merger();

        List<EpisodeData> a = new ArrayList<>();

        EpisodeData d = new EpisodeData();
        d.setPersistent(true);
        d.setPlannedFrom(SDF.parse("2014-10-12 10:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 12:00"));
        d.setText(new TextData("t1"));
        a.add(d);


        d = new EpisodeData();
        d.setPersistent(true);
        d.setExtra(true);
        d.setPlannedFrom(SDF.parse("2014-10-12 11:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 12:30"));
        d.setText(new TextData("t2"));
        a.add(d);

        d = new EpisodeData();
        d.setPersistent(false);
        d.setPlannedFrom(SDF.parse("2014-10-12 12:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 14:00"));
        d.setText(new TextData("t2"));
        a.add(d);


        //when
        m.adjustTimes(a);

        //then
        Assert.assertEquals(3, a.size());

        Assert.assertEquals(SDF.parse("2014-10-12 10:00"), a.get(0).getPlannedFrom());
        Assert.assertEquals(SDF.parse("2014-10-12 11:00"), a.get(0).getPlannedTo());
        Assert.assertEquals(SDF.parse("2014-10-12 11:00"), a.get(1).getPlannedFrom());
        Assert.assertEquals(SDF.parse("2014-10-12 12:30"), a.get(1).getPlannedTo());
        Assert.assertEquals(SDF.parse("2014-10-12 12:30"), a.get(2).getPlannedFrom());
        Assert.assertEquals(SDF.parse("2014-10-12 14:00"), a.get(2).getPlannedTo());

        Assert.assertFalse(a.get(0).isExtra());
        Assert.assertTrue(a.get(1).isExtra());
        Assert.assertFalse(a.get(2).isExtra());

    }

    @Test
    public void adjustTimesWithRemove() throws Exception {
        //given
        Merger m = new Merger();

        List<EpisodeData> a = new ArrayList<>();

        EpisodeData d = new EpisodeData();
        d.setPersistent(true);
        d.setPlannedFrom(SDF.parse("2014-10-12 10:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 11:00"));
        d.setText(new TextData("t1"));
        a.add(d);


        d = new EpisodeData();
        d.setPersistent(true);
        d.setExtra(true);
        d.setPlannedFrom(SDF.parse("2014-10-12 11:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 12:30"));
        d.setText(new TextData("t2"));
        a.add(d);

        d = new EpisodeData();
        d.setPersistent(false);
        d.setPlannedFrom(SDF.parse("2014-10-12 11:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 12:00"));
        d.setText(new TextData("t2"));
        a.add(d);

        d = new EpisodeData();
        d.setPersistent(false);
        d.setPlannedFrom(SDF.parse("2014-10-12 12:00"));
        d.setPlannedTo(SDF.parse("2014-10-12 13:00"));
        d.setText(new TextData("t2"));
        a.add(d);


        //when
        m.adjustTimes(a);

        //then
        Assert.assertEquals(3, a.size());

        Assert.assertEquals(SDF.parse("2014-10-12 10:00"), a.get(0).getPlannedFrom());
        Assert.assertEquals(SDF.parse("2014-10-12 11:00"), a.get(0).getPlannedTo());
        Assert.assertEquals(SDF.parse("2014-10-12 11:00"), a.get(1).getPlannedFrom());
        Assert.assertEquals(SDF.parse("2014-10-12 12:30"), a.get(1).getPlannedTo());
        Assert.assertEquals(SDF.parse("2014-10-12 12:30"), a.get(2).getPlannedFrom());
        Assert.assertEquals(SDF.parse("2014-10-12 13:00"), a.get(2).getPlannedTo());

        Assert.assertFalse(a.get(0).isExtra());
        Assert.assertTrue(a.get(1).isExtra());
        Assert.assertFalse(a.get(2).isExtra());

    }


    private Date date(int i) {
        Date date = new Date();
        date.setTime(i);
        return date;
    }

}