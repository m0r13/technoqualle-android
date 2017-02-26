package de.yellow_ray.bluetoothtest.protocol;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Package implements Parcelable {

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

        @Override
        public Package.Builder writeByte(char value) throws IOException {
            super.writeByte(value);
            return this;
        }

        @Override
        public Package.Builder writeShort(int value) throws IOException {
            super.writeShort(value);
            return this;
        }

        @Override
        public Package.Builder writeInt(long value) throws IOException {
            super.writeInt(value);
            return this;
        }

        @Override
        public Package.Builder writeString(String value) throws IOException {
            super.writeString(value);
            return this;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeByteArray(this.data);
    }

    protected Package(Parcel in) {
        this.type = (char) in.readInt();
        this.data = in.createByteArray();
        this.stream = new PackageInputStream(new ByteArrayInputStream(data));
    }

    public static final Parcelable.Creator<Package> CREATOR = new Parcelable.Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel source) {
            return new Package(source);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };
}
