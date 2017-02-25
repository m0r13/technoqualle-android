package de.yellow_ray.bluetoothtest.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Package {

    public char type;
    public byte[] data;
    public PackageInputStream stream;

    Package(char type, byte[] data) {
        this.type = type;
        this.data = data;
        this.stream = new PackageInputStream(new ByteArrayInputStream(data));
    }

    public static class Builder extends PackageOutputStream {

        private char mType;

        Builder(char type) {
            super(new ByteArrayOutputStream());
            mType = type;
        }

        public Package createPackage() {
            return new Package(mType, ((ByteArrayOutputStream) mOutputStream).toByteArray());
        }
    }
}
