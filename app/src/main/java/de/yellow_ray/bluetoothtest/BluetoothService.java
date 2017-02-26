package de.yellow_ray.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import de.yellow_ray.bluetoothtest.protocol.Package;

public class BluetoothService<T extends BluetoothClient> {

    public static final String TAG = "BluetoothService";
    public static final int MESSAGE_DISCONNECTED = 0;
    public static final int MESSAGE_CONNECTING = 1;
    public static final int MESSAGE_CONNECTING_FAILED = 2;
    public static final int MESSAGE_CONNECTED = 3;

    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;

    private BluetoothSocket mSocket = null;
    private ConnectThread mConnectThread = null;
    private TechnoBluetoothClient mClientThread = null;

    BluetoothService(final Handler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        notifyDisconnected();
    }

    public void connect(final BluetoothDevice device) {
        if (mSocket != null && mSocket.isConnected()) {
            closeSocket();
        }

        notifyConnecting(device);
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public void disconnect() {
        closeSocket();
    }

    public void sendPackage(Package pkg) {
        if (mClientThread != null) {
            mClientThread.sendPackage(pkg);
        }
    }

    private void notifyDisconnected() {
        mHandler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget();
    }

    private void notifyConnecting(final BluetoothDevice device) {
        Message msg = mHandler.obtainMessage(MESSAGE_CONNECTING);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void notifyConnectingFailed(final BluetoothDevice device, final String reason) {
        Message msg = mHandler.obtainMessage(MESSAGE_CONNECTING_FAILED);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        bundle.putString("reason", reason);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void notifyConnected(final BluetoothDevice device) {
        Message msg = mHandler.obtainMessage(MESSAGE_CONNECTED);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void closeSocket() {
        try {
            if (mSocket != null) {
                mSocket.close();
                notifyDisconnected();
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
            notifyDisconnected();
        }
    }

    private void handleConnection(final BluetoothSocket socket) {
        try {
            InputStream input = mSocket.getInputStream();
            OutputStream output = mSocket.getOutputStream();
            mClientThread = new TechnoBluetoothClient(mHandler, input, output);
            mClientThread.start();
            notifyConnected(socket.getRemoteDevice());
        } catch (IOException e) {
            Log.w(TAG, e);
            notifyConnectingFailed(socket.getRemoteDevice(), e.toString());
            closeSocket();
        }
    }

    private class ConnectThread extends Thread {

        private BluetoothDevice mDevice = null;

        public ConnectThread(BluetoothDevice device) {
            mDevice = device;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
                notifyConnectingFailed(mDevice, e.toString());
                closeSocket();
                return;
            }

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.w(TAG, "Unable to connect!");
                Log.w(TAG, connectException);

                try {
                    Class<?> clazz = mSocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};

                    mSocket = (BluetoothSocket) m.invoke(mSocket.getRemoteDevice(), params);
                    mSocket.connect();
                } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    Log.w(TAG, e);
                    notifyConnectingFailed(mDevice, e.toString());
                    closeSocket();
                    return;
                }
            }

            handleConnection(mSocket);
        }
    }
}
