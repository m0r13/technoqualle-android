package de.yellow_ray.bluetoothtest;

import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class BluetoothClient extends Thread {

    protected Handler mHandler;
    protected InputStream mInput;
    protected OutputStream mOutput;

    public BluetoothClient(final Handler handler, final InputStream input, final OutputStream output) {
        mHandler = handler;
        mInput = input;
        mOutput = output;
    }

}
