package de.yellow_ray.bluetoothtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import de.yellow_ray.bluetoothtest.protocol.Package;
import de.yellow_ray.bluetoothtest.protocol.PackageInputStream;
import de.yellow_ray.bluetoothtest.protocol.PackageOutputStream;
import de.yellow_ray.bluetoothtest.protocol.TechnoProtocol;

public class TechnoBluetoothClient extends BluetoothClient {

    public static final String TAG = "TechnoBluetoothClient";

    public static final int MESSAGE_BYTES_RECEIVED = 5;
    public static final int MESSAGE_PACKAGE_RECEIVED = 6;

    private static final long PING_INTERVAL = 1000;

    private BlockingQueue<Package> mPackageQueue = new LinkedBlockingDeque<>();
    private long mLastPingSent = 0;

    public TechnoBluetoothClient(Handler handler, InputStream input, OutputStream output) {
        super(handler, input, output);
    }

    public void sendPackage(Package pkg) {
        try {
            mPackageQueue.put(pkg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            PackageInputStream packageInput = new PackageInputStream(mInput);
            PackageOutputStream packageOutput = new PackageOutputStream(mOutput);

            /*
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PackageOutputStream testPackageOutput = new PackageOutputStream(buffer);
            */

            while (true) {
                Package pkg = packageInput.readPackage();
                if (pkg != null) {
                    handlePackage(pkg);
                }

                if (System.currentTimeMillis() - PING_INTERVAL > mLastPingSent) {
                    mLastPingSent = System.currentTimeMillis();
                    sendPackage(TechnoProtocol.createPing());
                }
                while (!mPackageQueue.isEmpty()) {
                    Package pkgToSend = mPackageQueue.take();
                    Log.v(TAG, "Sending package with type " + (int) pkgToSend.type);
                    packageOutput.writePackage(pkgToSend);

                    /*
                    testPackageOutput.writePackage(pkgToSend);
                    Log.v(TAG, "Sent: " + formatByteArray(buffer.toByteArray()));
                    buffer.reset();
                    */
                }
                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String formatByteArray(byte[] buffer) {
        String str = "len=" + buffer.length + " {";
        for (byte b : buffer) {
            str += String.format("%02X ", b) + ", ";
        }
        return str + "}";
    }

    private void handlePackage(Package pkg) {
        Message message = mHandler.obtainMessage(MESSAGE_PACKAGE_RECEIVED);
        Bundle bundle = new Bundle();
        bundle.putParcelable("package", pkg);
        bundle.putParcelable("data", TechnoProtocol.parsePackage(pkg));
        message.setData(bundle);
        message.sendToTarget();
    }

}
