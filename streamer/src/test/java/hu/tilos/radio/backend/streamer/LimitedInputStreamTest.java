package hu.tilos.radio.backend.streamer;

import org.junit.Assert;
import org.junit.Test;


public class LimitedInputStreamTest {

    @Test
    public void testRead() throws Exception {
        LimitedInputStream stream = new LimitedInputStream(getClass().getResourceAsStream("/test.txt"), 1, 5);
        Assert.assertEquals((int) '2', stream.read());
        Assert.assertEquals((int) '3', stream.read());
        Assert.assertEquals((int) '4', stream.read());
        Assert.assertEquals((int) '5', stream.read());
        Assert.assertEquals(-1, stream.read());
        stream.close();
    }


    @Test
    public void testReadUnlimited() throws Exception {
        LimitedInputStream stream = new LimitedInputStream(getClass().getResourceAsStream("/t1.txt"), 1, Integer.MAX_VALUE);
        Assert.assertEquals((int) 'b', stream.read());
        Assert.assertEquals((int) 'c', stream.read());
        Assert.assertEquals(-1, stream.read());
        stream.close();
    }

    @Test
    public void testReadArray() throws Exception {
        //given
        LimitedInputStream stream = new LimitedInputStream(getClass().getResourceAsStream("/test.txt"), 1, 5);
        byte[] b = new byte[3];

        //when
        int r = stream.read(b, 0, 3);

        //then
        Assert.assertEquals(3, r);
        Assert.assertArrayEquals(new byte[]{50, 51, 52}, b);

        stream.close();
    }

    @Test
    public void testReadArrayUnlimited() throws Exception {
        //given
        LimitedInputStream stream = new LimitedInputStream(getClass().getResourceAsStream("/t1.txt"), 1, Integer.MAX_VALUE);
        byte[] b = new byte[5];

        //when
        int r = stream.read(b, 0, 3);

        //then
        Assert.assertEquals(2, r);
        Assert.assertArrayEquals(new byte[]{98, 99, 0, 0, 0}, b);

        stream.close();
    }
}