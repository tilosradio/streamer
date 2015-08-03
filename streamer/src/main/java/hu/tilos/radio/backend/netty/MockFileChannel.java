package hu.tilos.radio.backend.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class MockFileChannel extends FileChannel {

    long pos = 0;

    long size = 10;

    @Override
    public int read(ByteBuffer byteBuffer) throws IOException {
        if (pos > size) {
            return -1;
        }
        byteBuffer.put((byte) 60);
        return 1;
    }

    @Override
    public long read(ByteBuffer[] byteBuffers, int from, int length) throws IOException {
        if (from > size) {
            return -1;
        }
        byteBuffers[0].put((byte) 60);
        return 1;
    }

    @Override
    public int write(ByteBuffer byteBuffer) throws IOException {
        return 0;
    }

    @Override
    public long write(ByteBuffer[] byteBuffers, int i, int i1) throws IOException {
        return 0;
    }

    @Override
    public long position() throws IOException {
        return pos;
    }

    @Override
    public FileChannel position(long l) throws IOException {
        pos = l;
        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public FileChannel truncate(long l) throws IOException {
        return this;
    }

    @Override
    public void force(boolean b) throws IOException {

    }

    @Override
    public long transferTo(long from, long length, WritableByteChannel writableByteChannel) throws IOException {
        ByteBuffer b = ByteBuffer.allocate((int) (length - from));

        for (int i = 0; i < (length - from); i++) {
            b.put(i, (byte) 78);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }
        writableByteChannel.write(b);
        return length - from;
    }

    @Override
    public long transferFrom(ReadableByteChannel readableByteChannel, long l, long l1) throws IOException {
        return l1 - l;
    }

    @Override
    public int read(ByteBuffer byteBuffer, long l) throws IOException {
        return 0;
    }

    @Override
    public int write(ByteBuffer byteBuffer, long l) throws IOException {
        return 0;
    }

    @Override
    public MappedByteBuffer map(MapMode mapMode, long l, long l1) throws IOException {
        return null;
    }

    @Override
    public FileLock lock(long l, long l1, boolean b) throws IOException {
        return null;
    }

    @Override
    public FileLock tryLock(long l, long l1, boolean b) throws IOException {
        return null;
    }

    @Override
    protected void implCloseChannel() throws IOException {

    }
}
