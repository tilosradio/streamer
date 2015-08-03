package hu.tilos.radio.backend.netty;

import io.netty.channel.FileRegion;
import io.netty.util.AbstractReferenceCounted;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class MockFile extends AbstractReferenceCounted implements FileRegion {

    int pos = 0;

    @Override
    protected void deallocate() {

    }

    @Override
    public long position() {
        return 0;
    }

    @Override
    public long transfered() {
        return pos;
    }

    @Override
    public long count() {
        return 10;
    }

    @Override
    public long transferTo(WritableByteChannel target, long position) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(1);
        b.put((byte) 60);
        target.write(b);
        return 1;
    }
}
