package de.yellow_ray.bluetoothtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import de.yellow_ray.bluetoothtest.protocol.Package;
import de.yellow_ray.bluetoothtest.protocol.PackageInputStream;
import de.yellow_ray.bluetoothtest.protocol.PackageOutputStream;

public class TechnoBluetoothClient extends BluetoothClient {

    public static final int MESSAGE_BYTES_RECEIVED = 5;
    public static final int MESSAGE_PACKAGE_RECEIVED = 6;

    private BlockingQueue<Package> mPackageQueue = new LinkedBlockingDeque<>();

    public TechnoBluetoothClient(Handler handler, InputStream input, OutputStream output) {
        super(handler, input, output);
    }

    public void sendPackage(Package pkg) {
        mPackageQueue.add(pkg);
    }

    public void run() {
        PrintStream printer = new PrintStream(mOutput);
        printer.println("Hello World!");
        printer.println("Current time millis: " + System.currentTimeMillis());

        try {
            PackageInputStream packageInput = new PackageInputStream(mInput);
            PackageOutputStream packageOutput = new PackageOutputStream(mOutput);
            while (true) {
                Package pkg = packageInput.readPackage();
                if (pkg == null) {
                    continue;
                }
                handlePackage(pkg);

                while (!mPackageQueue.isEmpty()) {
                    packageOutput.writePackage(mPackageQueue.take());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handlePackage(Package pkg) {
        Message message = mHandler.obtainMessage(MESSAGE_PACKAGE_RECEIVED);
        Bundle bundle = new Bundle();
        bundle.putParcelable("package", pkg);
        message.setData(bundle);
        message.sendToTarget();
    }

}
