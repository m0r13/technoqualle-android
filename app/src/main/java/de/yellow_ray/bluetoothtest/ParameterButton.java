package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by moritz on 4/5/17.
 */

public class ParameterButton extends Button implements ParameterWidget {

    public static final String TAG = "ParameterButton";

    private Parameter mParameter;
    private float mInactiveValue = 0.0f, mActiveValue = 0.0f;

    public ParameterButton(Context context) {
        super(context);
        initialize();
    }

    public ParameterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ParameterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    protected void initialize() {
    }

    @Override
    public void setParameter(Parameter parameter) {
        mParameter = parameter;
        setText(mParameter.getName());
        if (Math.abs(mParameter.getDefault() - mParameter.getMin()) < 0.001f) {
            mInactiveValue = mParameter.getMin();
            mActiveValue = mParameter.getMax();
        } else {
            mInactiveValue = mParameter.getMax();
            mActiveValue = mParameter.getMin();
        }
    }

    @Override
    public void setValue(float value) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mParameter.handleValueChanged(mActiveValue);
                return true;
            }
            case MotionEvent.ACTION_UP: {
                mParameter.handleValueChanged(mInactiveValue);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
