package de.yellow_ray.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DeviceListActivity extends AppCompatActivity {

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private ListView mListView = null;
    private BluetoothDevicesAdapter mDeviceAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mListView = (ListView) findViewById(R.id.deviceList);
        mDeviceAdapter = new BluetoothDevicesAdapter(this);
        mListView.setAdapter(mDeviceAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                Intent intent = getIntent();
                intent.putExtra(BluetoothDevice.EXTRA_DEVICE, (BluetoothDevice) adapterView.getItemAtPosition(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mBroadcastReceiver);
    }

    private void onDeviceFound(final BluetoothDevice device) {
        mDeviceAdapter.add(device);
    }

    private void showToast(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceAdapter.clear();
                showToast("Discovery started.");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showToast("Discovery finished.");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                onDeviceFound(device);
            }
        }
    };

    private static class BluetoothDevicesAdapter implements ListAdapter {

        private Context mContext;
        private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
        private List<DataSetObserver> mObservers = new LinkedList<>();

        public BluetoothDevicesAdapter(final Context context) {
            mContext = context;
        }

        public void clear() {
            mDevices.clear();
            notifyDataSetObserversChange();
        }

        public void add(final BluetoothDevice device) {
            for (int i = 0; i < mDevices.size(); i++) {
                if (mDevices.get(i).getAddress().equals(device.getAddress())) {
                    mDevices.set(i, device);
                    notifyDataSetObserversChange();
                    return;
                }
            }

            mDevices.add(device);
            notifyDataSetObserversChange();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(final int i) {
            return true;
        }

        @Override
        public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
            mObservers.add(dataSetObserver);
        }

        @Override
        public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
            mObservers.remove(dataSetObserver);
        }

        private void notifyDataSetObserversChange() {
            for (DataSetObserver observer : mObservers) {
                observer.onChanged();
            }
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(final int i) {
            return mDevices.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(final int i, final View convertView, final ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_simple_item, null);
            TextView text = (TextView) view.findViewById(R.id.text1);

            BluetoothDevice device = (BluetoothDevice) getItem(i);
            text.setText(device.getAddress());

            return view;
        }

        @Override
        public int getItemViewType(final int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return getCount() != 0;
        }
    }
}
