package de.yellow_ray.bluetoothtest.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;

public class PackageInputStream {

    protected DataInputStream mDataStream;

    public PackageInputStream(InputStream input) {
        mDataStream = new DataInputStream(input);
    }

    public char readByte() throws IOException {
        return (char) (mDataStream.readByte() & 0xff);
    }

    public int readShort() throws IOException {
        return (mDataStream.readShort() & 0xffff);
    }

    public long readInt() throws IOException {
        return (long) (mDataStream.readInt() & 0xffffffffl);
    }

    public float readFloat() throws IOException {
        return mDataStream.readFloat();
    }

    public String readString() throws IOException {
        char len = readByte();
        byte buffer[] = new byte[len];
        mDataStream.readFully(buffer);
        return new String(buffer);
    }

    public Package readPackage() throws IOException {
        if (mDataStream.available() == 0) {
            return null;
        }
        char type = readByte();
        char len = readByte();
        byte buffer[] = new byte[len];
        mDataStream.readFully(buffer);
        return new Package(type, buffer);
    }

}
