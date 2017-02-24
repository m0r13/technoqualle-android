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

public class StatusFragment extends Fragment {

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

    void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case (BluetoothService.MESSAGE_DISCONNECTED):
                mStatusView.setText("Disconnected");
                mStatusView.setTextColor(Color.RED);
                break;
            case (BluetoothService.MESSAGE_CONNECTING):
                BluetoothDevice device = (BluetoothDevice) bundle.getParcelable("device");
                mStatusView.setText("Connecting...");
                mStatusView.setTextColor(Color.YELLOW);
                break;
            case (BluetoothService.MESSAGE_CONNECTING_FAILED):
                break;
            case (BluetoothService.MESSAGE_CONNECTED):
                mStatusView.setText("Connected");
                mStatusView.setTextColor(Color.GREEN);
        }
    }

    public interface StatusFragmentListener {
        public void handleConnect();
        public void handleDisconnect();
    }
}
