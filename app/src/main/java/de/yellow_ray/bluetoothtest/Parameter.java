package de.yellow_ray.bluetoothtest;

import android.util.Log;

public class Parameter {

    public static final String TAG = "Parameter";

    public static final int FLAG_HIDE = 1;
    public static final int FLAG_READ_ONLY = 2;

    private int mIndex;
    private String mName;
    private float mMin, mDefault, mMax;
    private int mFlags;

    private Listener mListener = null;

    Parameter(int index, final String name, float minValue, float defaultValue, float maxValue, int flags) {
        mIndex = index;
        mName = name;
        mMin = minValue;
        mDefault = defaultValue;
        mMax = maxValue;
        mFlags = flags;
    }

    Parameter() {
        this(0, "Unknown Parameter", 0.0f, 0.5f, 1.0f, 0);
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

    public boolean isHidden() {
        return (mFlags & FLAG_HIDE) != 0;
    }

    public boolean isReadOnly() {
        return (mFlags & FLAG_READ_ONLY) != 0;
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
