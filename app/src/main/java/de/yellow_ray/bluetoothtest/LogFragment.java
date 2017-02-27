package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import de.yellow_ray.bluetoothtest.protocol.Package;
import de.yellow_ray.bluetoothtest.protocol.TechnoProtocol;

public class LogFragment extends Fragment implements MessageHandler {

    private LogFragmentListener mListener;

    private TextView mLogView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LogFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_log, container, false);

        mLogView = (TextView) root.findViewById(R.id.logText);

        return root;
    }

    @Override
    public void handleMessage(final Message msg) {
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case BluetoothService.MESSAGE_CONNECTED:
                mLogView.setText("");
        }
    }

    @Override
    public void handlePackage(final Package pkg, final Bundle data) {
        if (pkg.type == TechnoProtocol.PACKAGE_LOG) {
            mLogView.append(data.getString("message"));
        }
    }

    public interface LogFragmentListener {
    }
}
