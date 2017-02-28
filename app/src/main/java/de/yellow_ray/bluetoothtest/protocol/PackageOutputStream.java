package de.yellow_ray.bluetoothtest.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.*;

public class PackageOutputStream {

    protected OutputStream mOutputStream;
    protected DataOutputStream mDataStream;

    public PackageOutputStream(OutputStream output) {
        mOutputStream = output;
        mDataStream = new DataOutputStream(mOutputStream);
    }

    public PackageOutputStream writeByte(char value) throws IOException {
        mDataStream.writeByte((byte) value & 0xff);
        return this;
    }

    public PackageOutputStream writeShort(int value) throws IOException {
        mDataStream.writeShort(value & 0xffff);
        return this;
    }

    public PackageOutputStream writeInt(long value) throws IOException {
        mDataStream.writeInt((int) value & 0xffffffff);
        return this;
    }

    public PackageOutputStream writeFloat(float value) throws IOException {
        mDataStream.writeFloat(value);
        return this;
    }

    public PackageOutputStream writeString(final String string) throws IOException {
        assert string.length() <= 255;
        writeByte((char) string.length());
        mDataStream.writeBytes(string);
        return this;
    }

    public PackageOutputStream writePackage(final Package pkg) throws IOException {
        assert pkg.data.length <= 255;
        writeByte(pkg.type);
        writeByte((char) pkg.data.length);
        mDataStream.write(pkg.data);
        return this;
    }
}
