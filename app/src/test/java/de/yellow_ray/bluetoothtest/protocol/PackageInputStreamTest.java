package de.yellow_ray.bluetoothtest.protocol;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

public class PackageInputStreamTest {

    private ByteArrayInputStream mBuffer;
    private PackageInputStream mStream;

    @Before
    public void setUp() throws Exception {
        setBuffer(new byte[0]);
    }

    private void setBuffer(byte[] buffer) {
        mBuffer = new ByteArrayInputStream(buffer);
        mStream = new PackageInputStream(mBuffer);
    }

    @Test
    public void readByte() throws Exception {
        byte buffer[] = {'H', 'e', 'l', 'l', 'o', (byte) 0xff};
        setBuffer(buffer);

        assertEquals('H', mStream.readByte());
        assertEquals('e', mStream.readByte());
        assertEquals('l', mStream.readByte());
        assertEquals('l', mStream.readByte());
        assertEquals('o', mStream.readByte());
        assertEquals(255, mStream.readByte());
    }

    @Test
    public void readShort() throws Exception {
        byte buffer[] = {
                (byte) 0, (byte) 42,
                (byte) 0, (byte) 0,
                (byte) 0xb6, (byte) 0xf3,
                (byte) 0xff, (byte) 0xff
        };
        setBuffer(buffer);

        assertEquals(42, mStream.readShort());
        assertEquals(0, mStream.readShort());
        assertEquals(46835, mStream.readShort());
        assertEquals(65535, mStream.readShort());
    }

    @Test
    public void readInt() throws Exception {
        byte buffer[] = {
                0, 0, 0, 0,
                (byte) 0xde, (byte) 0xad,(byte) 0xbe,(byte) 0xef,
                (byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff
        };
        setBuffer(buffer);

        assertEquals(0, mStream.readInt());
        assertEquals(3735928559l, mStream.readInt());
        assertEquals(4294967295l, mStream.readInt());
    }

    @Test
    public void readString() throws Exception {
        byte buffer[] = {
                (byte) 12,
                'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'
        };
        setBuffer(buffer);

        assertEquals("Hello World!", mStream.readString());
    }

    @Test
    public void readPackage() throws Exception {
        byte buffer[] = {
                (byte) 42,
                (byte) 3,
                (byte) 0xda, (byte) 0x50, (byte) 0xde
        };
        setBuffer(buffer);

        byte data[] = {(byte) 0xda, (byte) 0x50, (byte) 0xde};
        Package pkg = mStream.readPackage();
        assertEquals(42, pkg.type);
        assertArrayEquals(data, pkg.data);
    }
}