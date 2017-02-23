package de.yellow_ray.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothService {

    public static final String TAG = "BluetoothService";
    public static final int MESSAGE_DISCONNECTED = 0;
    public static final int MESSAGE_CONNECTING = 1;
    public static final int MESSAGE_CONNECT_FAILED = 2;
    public static final int MESSAGE_CONNECTED = 3;

    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;

    private ConnectThread mConnectThread = null;
    private BluetoothSocket mSocket = null;

    BluetoothService(final Handler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mHandler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget();
    }

    public boolean isConnected() {
        return true;
    }

    public void connect(final BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            try {
                mConnectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mConnectThread = null;
        }

        notifyUI(MESSAGE_CONNECTING, "");
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public void disconnect() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
        }
    }

    private void notifyUI(int what, final String message) {
        Message msg = mHandler.obtainMessage(what);
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
                notifyUI(MESSAGE_CONNECT_FAILED, e.toString());
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
                    notifyUI(MESSAGE_CONNECT_FAILED, e.toString());
                    cancel();
                }
            }

            notifyUI(MESSAGE_CONNECTED, "");

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
            notifyUI(MESSAGE_DISCONNECTED, "");
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
