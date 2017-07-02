package de.yellow_ray.bluetoothtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import de.yellow_ray.bluetoothtest.protocol.Package;
import de.yellow_ray.bluetoothtest.protocol.PackageInputStream;
import de.yellow_ray.bluetoothtest.protocol.PackageOutputStream;
import de.yellow_ray.bluetoothtest.protocol.TechnoProtocol;

public class TechnoBluetoothClient extends BluetoothClient {

    public static final String TAG = "TechnoBluetoothClient";

    public static final int MESSAGE_BYTES_RECEIVED = 5;
    public static final int MESSAGE_PACKAGE_RECEIVED = 6;

    private static final long PACKAGE_PING_INTERVAL = 1000;
    private static final long PACKAGE_SET_PARAMETER_VALUE_INTERVAL = 1000 / 25;

    private BlockingQueue<Package> mPackageQueue = new LinkedBlockingDeque<>();
    private Map<Integer, Float> mParameterValues = new ConcurrentHashMap<>();
    private long mLastPingSent = 0;
    private long mLastSetParameterValueSent = 0;

    private boolean mStopped = false;

    public TechnoBluetoothClient(BluetoothService bluetoothService, Handler handler, InputStream input, OutputStream output) {
        super(bluetoothService, handler, input, output);
    }

    public void sendPackage(Package pkg) {
        try {
            mPackageQueue.put(pkg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setParameter(int index, float value) {
        mParameterValues.put(index, value);
    }

    public void run() {
        try {
            PackageInputStream packageInput = new PackageInputStream(mInput);
            PackageOutputStream packageOutput = new PackageOutputStream(mOutput);
            Thread.sleep(1000);
            sendPackage(TechnoProtocol.createRequestParameters());

            /*
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PackageOutputStream testPackageOutput = new PackageOutputStream(buffer);
            */

            while (!mStopped) {
                Package pkg = packageInput.readPackage();
                if (pkg != null) {
                    handlePackage(pkg);
                }

                long time = System.currentTimeMillis();
                if (time - PACKAGE_PING_INTERVAL > mLastPingSent) {
                    mLastPingSent = time;
                    sendPackage(TechnoProtocol.createPing());
                }

                if (time - PACKAGE_SET_PARAMETER_VALUE_INTERVAL > mLastSetParameterValueSent) {
                    mLastSetParameterValueSent = time;
                    Map<Integer, Float> parameterValues = new ConcurrentHashMap<>(mParameterValues);
                    mParameterValues.clear();
                    for (Map.Entry<Integer, Float> entry : parameterValues.entrySet()) {
                        sendPackage(TechnoProtocol.createSetParameterValue((char) entry.getKey().intValue(), entry.getValue()));
                    }
                }

                while (!mPackageQueue.isEmpty()) {
                    Package pkgToSend = mPackageQueue.take();
                    //Log.v(TAG, "Sending package with type " + (int) pkgToSend.type);
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
            mBluetoothService.closeSocketAfterError(e.toString());
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

    public void stopClient() {
        mStopped = true;
    }

}
