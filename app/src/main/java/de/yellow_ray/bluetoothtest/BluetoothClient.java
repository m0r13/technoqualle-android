package de.yellow_ray.bluetoothtest;

import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class BluetoothClient extends Thread {

    protected BluetoothService mBluetoothService;
    protected Handler mHandler;
    protected InputStream mInput;
    protected OutputStream mOutput;

    public BluetoothClient(final BluetoothService bluetoothService, final Handler handler, final InputStream input, final OutputStream output) {
        mBluetoothService = bluetoothService;
        mHandler = handler;
        mInput = input;
        mOutput = output;
    }

}
