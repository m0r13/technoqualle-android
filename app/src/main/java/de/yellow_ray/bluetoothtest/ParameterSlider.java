package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ParameterSlider extends LinearLayout implements ParameterWidget, SeekBar.OnSeekBarChangeListener {

    private static final int SLIDER_MAX = 65535;

    private Parameter mParameter = new Parameter();

    private TextView mNameText, mMinText, mMaxText;
    private SeekBar mSlider;

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

    @Override
    public void setParameter(final Parameter parameter) {
        mParameter = parameter;
        updateLabels();
        setValue(mParameter.getDefault());
    }

    @Override
    public Parameter getParameter() {
        return mParameter;
    }

    private void initialize() {
        inflate(getContext(), R.layout.layout_parameter_slider, this);

        mNameText = (TextView) findViewById(R.id.parameterName);
        mMinText = (TextView) findViewById(R.id.parameterMin);
        mMaxText = (TextView) findViewById(R.id.parameterMax);

        mSlider = (SeekBar) findViewById(R.id.parameterSlider);
        mSlider.setOnSeekBarChangeListener(this);
        mSlider.setMax(SLIDER_MAX);

        updateLabels();
        setValue(mParameter.getDefault());
    }

    protected void updateLabels() {
        mNameText.setText(String.format("%.2f - %s", getValue(), mParameter.getName()));
        mMinText.setText(String.format("%.2f", mParameter.getMin()));
        mMaxText.setText(String.format("%.2f", mParameter.getMax()));
    }

    @Override
    public void setValue(float value) {
        float relativeValue = (value - mParameter.getMin()) / (mParameter.getMax() - mParameter.getMin());
        relativeValue = Math.max(0.0f, Math.min(1.0f, relativeValue));
        mSlider.setProgress(Math.round(relativeValue * SLIDER_MAX));
    }

    protected float getValue() {
        float relativeValue = (float) mSlider.getProgress() / SLIDER_MAX;
        return (1-relativeValue) * mParameter.getMin() + relativeValue * mParameter.getMax();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateLabels();
        if (!fromUser) {
            return;
        }

        mParameter.handleValueChanged(getValue());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
