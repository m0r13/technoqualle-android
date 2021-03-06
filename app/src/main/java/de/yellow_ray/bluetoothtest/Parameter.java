package de.yellow_ray.bluetoothtest;

import android.util.Log;

public class Parameter {

    public static final String TAG = "Parameter";

    public static final int FLAG_HIDE = 1;
    public static final int FLAG_READ_ONLY = 2;
    public static final int FLAG_TYPE_BOOL = 4;
    public static final int FLAG_TYPE_INT = 8;
    public static final int FLAG_SEMANTIC_HOLD = 16;
    public static final int FLAG_SEMANTIC_ONCE = 32;
    public static final int FLAG_SEMANTIC_SWITCH = 64;
    public static final int FLAG_TYPE_SELECT = 128;

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

    public boolean hasFlag(int flag) {
        return (mFlags & flag) != 0;
    }

    public boolean isHidden() {
        return hasFlag(FLAG_HIDE);
    }

    public boolean isReadOnly() {
        return hasFlag(FLAG_READ_ONLY);
    }

    public boolean isTypeSwitch() {
        return hasFlag(FLAG_TYPE_SELECT);
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
