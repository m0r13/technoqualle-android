package de.yellow_ray.bluetoothtest.protocol;

import android.os.Bundle;

import java.io.IOException;

public class TechnoProtocol {

    public static final char PACKAGE_PING = 0;
    public static final char PACKAGE_LOG = 1;

    public static final char PACKAGE_REQUEST_PARAMETERS = 12;
    public static final char PACKAGE_BEGIN_PARAMETERS = 13;
    public static final char PACKAGE_SECTION = 14;
    public static final char PACKAGE_PARAMETER = 15;
    public static final char PACKAGE_END_PARAMETERS = 16;
    public static final char PACKAGE_SET_PARAMETER_VALUE = 17;

    public static Package createPing() {
        return new Package.Builder(PACKAGE_PING).createPackage();
    }

    public static Package createLog(String message) {
        try {
            return new Package.Builder(PACKAGE_LOG)
                    .writeString(message)
                    .createPackage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Package createRequestParameters() {
        return new Package.Builder(PACKAGE_REQUEST_PARAMETERS).createPackage();
    }

    public static Package createSetParameterValue(char id, float value) {
        try {
            return new Package.Builder(PACKAGE_SET_PARAMETER_VALUE)
                    .writeByte(id)
                    .writeFloat(value)
                    .createPackage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bundle parsePackage(Package pkg) {
        Bundle bundle = new Bundle();
        try {
            switch (pkg.type) {
                case PACKAGE_LOG:
                    bundle.putString("message", pkg.stream.readString());
                    break;
                case PACKAGE_SECTION:
                    bundle.putString("name", pkg.stream.readString());
                    break;
                case PACKAGE_PARAMETER:
                    bundle.putInt("id", pkg.stream.readByte());
                    bundle.putInt("section", pkg.stream.readByte());
                    bundle.putString("name", pkg.stream.readString());
                    bundle.putFloat("min", pkg.stream.readFloat());
                    bundle.putFloat("default", pkg.stream.readFloat());
                    bundle.putFloat("max", pkg.stream.readFloat());
                    break;
                case PACKAGE_SET_PARAMETER_VALUE:
                    bundle.putInt("id", pkg.stream.readByte());
                    bundle.putFloat("value", pkg.stream.readFloat());
                    break;
                default:
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
}
