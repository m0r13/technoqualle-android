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

public class LogFragment extends Fragment {

    private LogFragmentListener mListener;

    private TextView mStatusView;
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

        mStatusView = (TextView) root.findViewById(R.id.statusText);
        mLogView = (TextView) root.findViewById(R.id.logText);
        setReceivedBytes(0);

        return root;
    }

    public void setReceivedBytes(int count) {
        mStatusView.setText("Received " + count + " bytes.");
    }

    void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case (MyBluetoothClient.MESSAGE_BYTES_RECEIVED):
                try {
                    mLogView.append(new String(bundle.getByteArray("bytes"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                setReceivedBytes(bundle.getInt("count"));
                break;
        }
    }

    public interface LogFragmentListener {
    }

}
