package de.yellow_ray.bluetoothtest;

public class Parameter {

    private String mName;
    private float mMin, mDefault, mMax;
    private float mValue;

    Parameter(final String name, float minValue, float defaultValue, float maxValue) {
        mName = name;
        mMin = minValue;
        mDefault = defaultValue;
        mMax = maxValue;
    }

    Parameter() {
        this("Parameter", 0.0f, 0.5f, 1.0f);
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

}
