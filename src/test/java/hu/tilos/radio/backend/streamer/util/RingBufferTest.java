package hu.tilos.radio.backend.streamer.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RingBufferTest {

    @Test
    public void test(){
        RingBuffer b = new RingBuffer(4);
        b.add(0);
        b.add(1);
        b.add(2);
        b.add(3);
        assertEquals(0, b.get(0));
        assertEquals(1, b.get(1));

        b.add(4);
        assertEquals(1, b.get(0));
        assertEquals(2, b.get(1));

        b.add(5);
        assertEquals(2, b.get(0));
        assertEquals(3, b.get(1));

        b.add(6);
        assertEquals(3, b.get(0));
        assertEquals(4, b.get(1));

        b.add(7);
        assertEquals(4, b.get(0));
        assertEquals(5, b.get(1));
    }

}