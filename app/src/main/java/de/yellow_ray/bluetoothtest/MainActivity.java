package de.yellow_ray.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 42;
    private static final int REQUEST_PICK_DEVICE = 43;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device doesn't support bluetooth.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }

        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            Log.v(TAG, "Paired: " + device.getName() + " " + device.getAddress());
        }

        startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_PICK_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "Bluetooth was enabled.");
            } else {
                Log.v(TAG, "Bluetooth was not enabled.");
                Toast.makeText(this, "Bluetooth must be enabled", Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (requestCode == REQUEST_PICK_DEVICE) {
            if (resultCode == RESULT_OK) {
                BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v(TAG, "Device was picked: " + device.getAddress());
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.w(TAG, "Unable to connect!");
                Log.w(TAG, connectException);

                try {
                    Class<?> clazz = mmSocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[] {Integer.valueOf(1)};

                    mmSocket = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                    mmSocket.connect();
                } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    Log.w(TAG, e);
                    cancel();
                }
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

        private void manageMyConnectedSocket(BluetoothSocket socket) {
            try {
                Log.v(TAG, "Connection established.");
                PrintStream printer = new PrintStream(socket.getOutputStream());
                printer.println("Hello World!");
                printer.println("Time: " + System.currentTimeMillis());
                cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
