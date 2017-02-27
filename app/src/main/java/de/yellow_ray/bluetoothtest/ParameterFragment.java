package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.yellow_ray.bluetoothtest.protocol.Package;
import de.yellow_ray.bluetoothtest.protocol.TechnoProtocol;

public class ParameterFragment extends Fragment implements MessageHandler {

    public static final String TAG = "ParameterFragment";

    private ParameterFragmentListener mListener;

    private LinearLayout mParameterContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ParameterFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ParameterFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_parameters, container, false);
        mParameterContainer = (LinearLayout) root.findViewById(R.id.parameterContainer);
        return root;
    }

    @Override
    public void handleMessage(final Message msg) {
    }

    @Override
    public void handlePackage(final Package pkg, final Bundle data) {
        switch (pkg.type) {
            case TechnoProtocol.PACKAGE_BEGIN_PARAMETERS:
                Log.v(TAG, "PACKAGE_BEGIN_PARAMETERS");
                mParameterContainer.removeAllViews();
                break;
            case TechnoProtocol.PACKAGE_PARAMETER:
                Log.v(TAG, "PACKAGE_PARAMETER");
                Log.v(TAG, "" + data);
                Parameter parameter = new Parameter(data.getString("name"), data.getFloat("min"), data.getFloat("default"), data.getFloat("max"));
                ParameterSlider slider = new ParameterSlider(getContext());
                slider.setParameter(parameter);
                mParameterContainer.addView(slider);
                break;
            case TechnoProtocol.PACKAGE_END_PARAMETERS:
                Log.v(TAG, "PACKAGE_END_PARAMETERS");
                break;
        }
    }

    public interface ParameterFragmentListener {
        public void handleParameterChanged(int index, float value);
    }
}
