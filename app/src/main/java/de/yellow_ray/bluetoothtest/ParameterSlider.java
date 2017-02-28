package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ParameterSlider extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "SliderParameter";

    private static final int SLIDER_MAX = 65535;

    private Parameter mParameter = new Parameter();
    private Listener mListener = null;

    private SeekBar mSlider;
    private TextView mNameText, mMinText, mMaxText;

    public ParameterSlider(Context context) {
        super(context);
        initialize();
    }

    public ParameterSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ParameterSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void setParameter(final Parameter parameter) {
        mParameter = parameter;
        updateLabels();
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    private void initialize() {
        inflate(getContext(), R.layout.layout_parameter_slider, this);

        mSlider = (SeekBar) findViewById(R.id.parameterSlider);
        mSlider.setOnSeekBarChangeListener(this);
        mSlider.setMax(SLIDER_MAX);

        mNameText = (TextView) findViewById(R.id.parameterName);
        mMinText = (TextView) findViewById(R.id.parameterMin);
        mMaxText = (TextView) findViewById(R.id.parameterMax);

        updateLabels();
    }

    protected void updateLabels() {
        mNameText.setText(mParameter.getName());
        mMinText.setText(String.format("%.2f", mParameter.getMin()));
        mMaxText.setText(String.format("%.2f", mParameter.getMax()));
        setSliderValue(mParameter.getDefault());
    }

    protected void setSliderValue(float value) {
        float relativeValue = (value - mParameter.getMin()) / mParameter.getMax();
        relativeValue = Math.max(0.0f, Math.min(1.0f, relativeValue));
        mSlider.setProgress(Math.round(relativeValue * SLIDER_MAX));
    }

    protected float getSliderValue() {
        float relativeValue = (float) mSlider.getProgress() / SLIDER_MAX;
        return (1-relativeValue) * mParameter.getMin() + relativeValue * mParameter.getMax();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        Log.v(TAG, "Parameter '" + mParameter.getName() + "' changed to: " + getSliderValue());
        if (mListener != null) {
            mListener.handleParameterChanged(mParameter.getIndex(), getSliderValue());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public interface Listener {
        void handleParameterChanged(int index, float value);
    }
}
