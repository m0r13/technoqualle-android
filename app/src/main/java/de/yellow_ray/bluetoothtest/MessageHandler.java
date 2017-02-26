package de.yellow_ray.bluetoothtest;

import android.os.Message;

import de.yellow_ray.bluetoothtest.protocol.Package;

public interface MessageHandler {

    void handleMessage(final Message msg);
    void handlePackage(final Package pkg);

}
