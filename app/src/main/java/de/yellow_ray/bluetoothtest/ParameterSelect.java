package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moritz on 5/22/17.
 */

public class ParameterSelect extends LinearLayout implements ParameterWidget, AdapterView.OnItemSelectedListener {

    public static final String TAG = "ParameterSelect";
    private Parameter mParameter;

    private TextView mNameText;
    private Spinner mSpinner;

    public ParameterSelect(Context context) {
        super(context);
        initialize();
    }

    public ParameterSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ParameterSelect(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    protected void initialize() {
        inflate(getContext(), R.layout.layout_parameter_select, this);

        mNameText = (TextView) findViewById(R.id.parameterName);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void setParameter(Parameter parameter) {
        mParameter = parameter;

        String args[] = parameter.getName().split("\\|");
        if (args.length > 0) {
            mNameText.setText(args[0]);
        } else {
            mNameText.setText("Unknown parameter");
        }

        List<String> choices = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            choices.add(args[i]);
        }

        if (choices.size() < (int) mParameter.getMax()) {
            for (int i = choices.size(); i <= (int) mParameter.getMax(); i++) {
                choices.add("Option " + (i + 1));
            }
        }

        mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_simple_item, choices));
    }

    @Override
    public void setValue(float value) {
        mSpinner.setSelection((int) value);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mParameter.handleValueChanged((float) position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
