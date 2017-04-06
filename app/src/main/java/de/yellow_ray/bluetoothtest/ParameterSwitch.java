package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

/**
 * Created by moritz on 4/7/17.
 */

public class ParameterSwitch extends ToggleButton implements ParameterWidget, View.OnClickListener{

    private Parameter mParameter = new Parameter();

    public ParameterSwitch(Context context) {
        super(context);
        initialize();
    }

    public ParameterSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ParameterSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setOnClickListener(this);

        updateLabels();
        setValue(mParameter.getDefault());
    }

    private void updateLabels() {
        setTextOn("Do: " + mParameter.getName());
        setTextOff("Don't: " + mParameter.getName());
    }

    @Override
    public void setParameter(Parameter parameter) {
        mParameter = parameter;
        updateLabels();
        setValue(mParameter.getDefault());
    }

    @Override
    public void setValue(float value) {
        if (Math.abs(value - mParameter.getMin()) < 0.01) {
            setChecked(false);
        } else {
            setChecked(true);
        }
    }

    public void onClick(View v) {
        mParameter.handleValueChanged(isChecked() ? mParameter.getMax() : mParameter.getMin());
    }
}
