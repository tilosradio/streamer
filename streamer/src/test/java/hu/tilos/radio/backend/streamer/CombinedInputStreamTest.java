package hu.tilos.radio.backend.streamer;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CombinedInputStreamTest {

    @Test
    public void testRead() throws Exception {
        CombinedInputStream is = new CombinedInputStream(getClass().getResourceAsStream("/t1.txt"), getClass().getResourceAsStream("/t2.txt"));
        Assert.assertEquals((int) 'a', is.read());
        Assert.assertEquals((int) 'b', is.read());
        Assert.assertEquals((int) 'c', is.read());
        Assert.assertEquals((int) 'd', is.read());
        Assert.assertEquals((int) 'e', is.read());
        Assert.assertEquals((int) 'f', is.read());
        Assert.assertEquals(-1, is.read());
        is.close();

    }

    @Test
    public void testReadByteArray() throws Exception {
        CombinedInputStream is = new CombinedInputStream(getClass().getResourceAsStream("/t1.txt"), getClass().getResourceAsStream("/t2.txt"));

        byte[] result = new byte[5];
        Assert.assertEquals(5, is.read(result, 0, 5));
        Assert.assertArrayEquals(new byte[]{97, 98, 99, 100, 101}, result);
        is.close();

    }

    @Test
    public void testReadByteArrayWithOffset() throws Exception {
        CombinedInputStream is = new CombinedInputStream(getClass().getResourceAsStream("/t1.txt"), getClass().getResourceAsStream("/t2.txt"));

        byte[] result = new byte[5];
        Assert.assertEquals(4, is.read(result, 1, 4));
        Assert.assertArrayEquals(new byte[]{0, 97, 98, 99, 100}, result);
        is.close();

    }

    @Test
    public void testReadWithSkip() throws Exception {
        CombinedInputStream is = new CombinedInputStream(getClass().getResourceAsStream("/t1.txt"), getClass().getResourceAsStream("/t2.txt"));
        is.skip(4);
        Assert.assertEquals((int) 'e', is.read());
        Assert.assertEquals((int) 'f', is.read());
        Assert.assertEquals(-1, is.read());
        is.close();

    }

    @Test
    public void testReadWithSkip2() throws Exception {
        CombinedInputStream is = new CombinedInputStream(getClass().getResourceAsStream("/t1.txt"), getClass().getResourceAsStream("/t2.txt"));
        is.skip(2);
        Assert.assertEquals((int) 'c', is.read());
        Assert.assertEquals((int) 'd', is.read());
        Assert.assertEquals((int) 'e', is.read());
        Assert.assertEquals((int) 'f', is.read());
        Assert.assertEquals(-1, is.read());
        is.close();

    }
}