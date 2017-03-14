package de.yellow_ray.bluetoothtest;

import android.util.Log;

public class Parameter {

    public static final String TAG = "Parameter";

    private int mIndex;
    private String mName;
    private float mMin, mDefault, mMax;

    private Listener mListener = null;

    Parameter(int index, final String name, float minValue, float defaultValue, float maxValue) {
        mIndex = index;
        mName = name;
        mMin = minValue;
        mDefault = defaultValue;
        mMax = maxValue;
    }

    Parameter() {
        this(0, "Parameter", 0.0f, 0.5f, 1.0f);
    }

    public int getIndex() {
        return mIndex;
    }

    public String getName() {
        return mName;
    }

    public float getMin() {
        return mMin;
    }

    public float getDefault() {
        return mDefault;
    }

    public float getMax() {
        return mMax;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public void handleValueChanged(float value) {
        Log.v(TAG, "Parameter '" + mName + "' changed to: " + value);
        mListener.handleParameterChanged(mIndex, value);
    }

    public interface Listener {
        void handleParameterChanged(int index, float value);
    }
}
