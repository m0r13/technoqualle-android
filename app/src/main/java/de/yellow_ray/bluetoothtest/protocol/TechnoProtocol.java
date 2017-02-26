package de.yellow_ray.bluetoothtest.protocol;

import java.io.IOException;

public class TechnoProtocol {

    public static final char PACKAGE_PING = 0;
    public static final char PACKAGE_LOG = 1;

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
}
