package de.yellow_ray.bluetoothtest;

import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class MyBluetoothClient extends BluetoothClient {

    public MyBluetoothClient(Handler handler, InputStream input, OutputStream output) {
        super(handler, input, output);
    }

    public void run() {
        PrintStream printer = new PrintStream(mOutput);
        printer.println("Hello World!");
        printer.println("Current time millis: " + System.currentTimeMillis());
    }

}
