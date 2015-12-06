package hu.tilos.radio.backend.streamer.util;

/**
 * Ringbuffer to keep a limited number of the bytes in the memory during sequential read.
 */
public class RingBuffer {
    private int idx;
    private int[] data;
    private int size;

    public RingBuffer(int size) {
        this.size = size;
        data = new int[size];
    }

    public void add(int value) {
        data[idx++] = value;
        if (idx == size) {
            idx = 0;
        }
    }

    public int get(int r) {
        int realIndex = idx - size + r;
        if (realIndex < 0) {
            realIndex += size;
        }
        return data[realIndex];
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RingBuffer)) {
            return false;
        }
        RingBuffer b = (RingBuffer) o;
        if (this.getSize() != b.getSize()) {
            return false;
        }
        for (int i = 0; i < getSize(); i++) {
            if (b.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return size;
    }
}
