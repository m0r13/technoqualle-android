package de.yellow_ray.bluetoothtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class MyBluetoothClient extends BluetoothClient {

    public static final int MESSAGE_BYTES_RECEIVED = 5;

    public MyBluetoothClient(Handler handler, InputStream input, OutputStream output) {
        super(handler, input, output);
    }

    public void run() {
        PrintStream printer = new PrintStream(mOutput);
        printer.println("Hello World!");
        printer.println("Current time millis: " + System.currentTimeMillis());

        try {
            int received = 0;
            while (true) {
                if (mInput.available() > 0) {
                    byte buffer[] = new byte[1024];
                    received += mInput.read(buffer);
                    Message message = mHandler.obtainMessage(MESSAGE_BYTES_RECEIVED);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("bytes", buffer);
                    bundle.putInt("count", received);
                    message.setData(bundle);
                    message.sendToTarget();
                }
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
