package de.yellow_ray.bluetoothtest;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.yellow_ray.bluetoothtest.protocol.Package;
import de.yellow_ray.bluetoothtest.protocol.TechnoProtocol;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class ParameterFragment extends Fragment implements MessageHandler {

    public static final String TAG = "ParameterFragment";

    private ParameterFragmentListener mListener;

    private SectionedRecyclerViewAdapter mSectionAdapter;
    private RecyclerView mRecyclerView;

    private final Map<Integer, ParameterSlider> mParameterSliders = new HashMap<>();

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

        mSectionAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.sections);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSectionAdapter);

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
                mParameterSliders.clear();
                mSectionAdapter.removeAllSections();
                createSection("Global parameters");
                break;
            case TechnoProtocol.PACKAGE_SECTION:
                break;
            case TechnoProtocol.PACKAGE_PARAMETER:
                Log.v(TAG, "PACKAGE_PARAMETER");
                Log.v(TAG, "" + data);
                Parameter parameter = new Parameter(data.getInt("id"), data.getString("name"), data.getFloat("min"), data.getFloat("default"), data.getFloat("max"));
                ParameterSlider slider = new ParameterSlider(getContext());
                slider.setParameter(parameter);
                slider.setListener(mParameterSliderListener);
                mParameterSliders.put(parameter.getIndex(), slider);

                int sectionIndex = data.getInt("section");
                if (sectionIndex < mSectionAdapter.getSectionsMap().size()) {
                    ExpandableParameterSection section = (ExpandableParameterSection) mSectionAdapter.getSectionForPosition(sectionIndex);
                    section.addParameter(slider);
                } else {
                    Log.w(TAG, "Parameter '" + parameter.getName() + "' attempts to use unknown section " + sectionIndex);
                }
                break;
            case TechnoProtocol.PACKAGE_END_PARAMETERS:
                Log.v(TAG, "PACKAGE_END_PARAMETERS");
                break;
            case TechnoProtocol.PACKAGE_SET_PARAMETER_VALUE:
                Log.v(TAG, "PACKAGE_SET_PARAMETER_VALUE");
                Log.v(TAG, "" + data);
                int id = data.getInt("id");
                float value = data.getFloat("value");
                if (!mParameterSliders.containsKey(id)) {
                    Log.w(TAG, "Package was sent to set parameter with id " + id + ", but id is unknown!");
                } else {
                    mParameterSliders.get(id).setSliderValue(value);
                }
                break;
        }
    }

    private void createSection(final String name) {
        mSectionAdapter.addSection(new ExpandableParameterSection(name));
    }

    private final ParameterSlider.Listener mParameterSliderListener = new ParameterSlider.Listener() {
        @Override
        public void handleParameterChanged(int index, float value) {
            mListener.handleParameterChanged(index, value);
        }
    };

    private class ExpandableParameterSection extends StatelessSection {

        private String mTitle;
        private boolean mExpanded = true;
        private ArrayList<ParameterSlider> mParameters = new ArrayList<>();

        ExpandableParameterSection(final String title) {
            super(R.layout.parameter_section_header, R.layout.parameter_section_item);

            mTitle = title;
        }

        public void addParameter(final ParameterSlider parameter) {
            mParameters.add(parameter);
            mSectionAdapter.notifyDataSetChanged();
        }

        @Override
        public int getContentItemsTotal() {
            return mExpanded ? mParameters.size() : 0;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.setParameterWidget(mParameters.get(position));
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.setTitle(mTitle);
            headerHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpanded = !mExpanded;
                    headerHolder.setArrowImage(
                            mExpanded ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp
                    );
                    mSectionAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final View mRootView;
        private final TextView mTitle;
        private final ImageView mArrowImage;

        public HeaderViewHolder(View view) {
            super(view);

            mRootView = view;
            mTitle = (TextView) view.findViewById(R.id.title);
            mArrowImage = (ImageView) view.findViewById(R.id.arrow);
        }

        public void setOnClickListener(final View.OnClickListener listener) {
            mRootView.setOnClickListener(listener);
        }

        public void setTitle(final String text) {
            mTitle.setText(text);
        }

        public void setArrowImage(final int resId) {
            mArrowImage.setImageResource(resId);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout mParameterContainer;

        public ItemViewHolder(View view) {
            super(view);

            mParameterContainer = (LinearLayout) view.findViewById(R.id.parameterContainer);
        }

        public void setParameterWidget(final ParameterSlider slider) {
            if (slider.getParent() != null) {
                ((ViewGroup) slider.getParent()).removeView(slider);
            }
            mParameterContainer.addView(slider);
        }
    }

    public interface ParameterFragmentListener {
        public void handleParameterChanged(int index, float value);
    }
}
