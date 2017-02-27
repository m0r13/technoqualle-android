package de.yellow_ray.bluetoothtest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.yellow_ray.bluetoothtest.protocol.Package;

public class StatusFragment extends Fragment implements MessageHandler {

    private StatusFragmentListener mListener;

    private TextView mStatusView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (StatusFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StatusFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        mStatusView = (TextView) root.findViewById(R.id.statusText);
        setDisconnected();

        ((Button) root.findViewById(R.id.connectButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.handleConnect();
            }
        });
        ((Button) root.findViewById(R.id.disconnectButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.handleDisconnect();
            }
        });

        return root;
    }

    public void setConnected(final BluetoothDevice device) {
        mStatusView.setText("Connected to " + device.getAddress());
        if (device.getName() != null) {
            mStatusView.append(" (" + device.getName() + ")");
        }
        mStatusView.setTextColor(Color.GREEN);
    }

    private void setDisconnected() {
        mStatusView.setText("Disconnected");
        mStatusView.setTextColor(Color.RED);
    }

    @Override
    public void handleMessage(final Message msg) {
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case (BluetoothService.MESSAGE_DISCONNECTED):
                setDisconnected();
                break;
            case (BluetoothService.MESSAGE_CONNECTED):
                BluetoothDevice device = (BluetoothDevice) bundle.getParcelable("device");
                setConnected(device);
                break;
        }
    }

    @Override
    public void handlePackage(final Package pkg, final Bundle data) {
    }

    public interface StatusFragmentListener {
        public void handleConnect();
        public void handleDisconnect();
    }
}
