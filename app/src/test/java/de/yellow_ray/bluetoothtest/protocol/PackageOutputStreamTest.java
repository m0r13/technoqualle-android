package de.yellow_ray.bluetoothtest.protocol;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class PackageOutputStreamTest {

    private ByteArrayOutputStream mBuffer;
    private PackageOutputStream mStream;

    @Before
    public void setUp() throws Exception {
        mBuffer = new ByteArrayOutputStream();
        mStream = new PackageOutputStream(mBuffer);
    }

    @Test
    public void writeByte() throws Exception {
        mStream.writeByte('H');
        mStream.writeByte('e');
        mStream.writeByte('l');
        mStream.writeByte('l');
        mStream.writeByte('o');
        mStream.writeByte((char) 255);

        byte expected[] = {'H', 'e', 'l', 'l', 'o', (byte) 0xff};
        assertArrayEquals(expected, mBuffer.toByteArray());
    }

    @Test
    public void writeShort() throws Exception {
        mStream.writeShort(42);
        mStream.writeShort(0);
        mStream.writeShort(46835);
        mStream.writeShort(65535);

        byte expected[] = {
                (byte) 0, (byte) 42,
                (byte) 0, (byte) 0,
                (byte) 0xb6, (byte) 0xf3,
                (byte) 0xff, (byte) 0xff
        };
        assertArrayEquals(expected, mBuffer.toByteArray());
    }

    @Test
    public void writeInt() throws Exception {
        mStream.writeInt(0l);
        mStream.writeInt(3735928559l);
        mStream.writeInt(4294967295l);

        byte expected[] = {
                0, 0, 0, 0,
                (byte) 0xde, (byte) 0xad,(byte) 0xbe,(byte) 0xef,
                (byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff
        };
        assertArrayEquals(expected, mBuffer.toByteArray());
    }

    @Test
    public void writeString() throws Exception {
        String string = "Hello World!";
        mStream.writeString(string);

        byte expected[] = {
                (byte) string.length(),
                'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'
        };
        assertArrayEquals(expected, mBuffer.toByteArray());
    }

    @Test
    public void writePackage() throws Exception {
        byte data[] = {(byte) 0xda, (byte) 0x50, (byte) 0xde};
        Package pkg = new Package((char) 42, data);
        mStream.writePackage(pkg);

        byte expected[] = {
                (byte) 42,
                (byte) 3,
                (byte) 0xda, (byte) 0x50, (byte) 0xde
        };
        assertArrayEquals(expected, mBuffer.toByteArray());
    }
}