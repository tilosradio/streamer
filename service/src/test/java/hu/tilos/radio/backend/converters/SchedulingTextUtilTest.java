package hu.tilos.radio.backend.converters;

import hu.tilos.radio.backend.data.types.SchedulingSimple;
import org.junit.Assert;
import org.junit.Test;

public class SchedulingTextUtilTest {

    @Test
    public void testCreate() throws Exception {
        //given
        SchedulingSimple s = new SchedulingSimple();
        s.setWeekDay(4);
        s.setWeekType(2);
        s.setMinFrom(30);
        s.setHourFrom(13);
        s.setDuration(120);
        SchedulingTextUtil util = new SchedulingTextUtil();

        //when
        String response = util.create(s);

        //then
        Assert.assertEquals("minden második péntek 13:30-15:30", response);

    }
}