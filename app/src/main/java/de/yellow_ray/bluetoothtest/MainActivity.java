package de.yellow_ray.bluetoothtest;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        Handler.Callback,
        StatusFragment.StatusFragmentListener,
        LogFragment.LogFragmentListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 42;
    private static final int REQUEST_PICK_DEVICE = 43;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Handler mHandler;
    private BluetoothService mBluetoothService;

    private PageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPageAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.hide();

        mHandler = new Handler(Looper.getMainLooper(), this);
        mBluetoothService = new BluetoothService(mHandler);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device doesn't support bluetooth.", Toast.LENGTH_LONG).show();
            finish();
        }

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "Bluetooth was enabled.");
            } else {
                Log.v(TAG, "Bluetooth was not enabled.");
                Toast.makeText(this, "Bluetooth must be enabled", Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (requestCode == REQUEST_PICK_DEVICE) {
            if (resultCode == RESULT_OK) {
                BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v(TAG, "Device was picked: " + device.getAddress());

                mBluetoothService.connect(device);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.v(TAG, "Received message: " + msg);
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case (BluetoothService.MESSAGE_DISCONNECTED):
                mProgressDialog.hide();
                break;
            case (BluetoothService.MESSAGE_CONNECTING):
                BluetoothDevice device = (BluetoothDevice) bundle.getParcelable("device");
                mProgressDialog.setMessage("Connecting...");
                mProgressDialog.setMessage("Connecting to " + (device.getName() == null ? device.getAddress() : device.getName()) + "...");
                mProgressDialog.show();
                break;
            case (BluetoothService.MESSAGE_CONNECTING_FAILED):
                mProgressDialog.hide();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Unable to connect: " + bundle.getString("reason"));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setCancelable(false);
                builder.create().show();

                break;
            case (BluetoothService.MESSAGE_CONNECTED):
                mProgressDialog.hide();
                break;
            case (MyBluetoothClient.MESSAGE_BYTES_RECEIVED):
                Log.d(TAG, "Received " + bundle.getInt("count") + " bytes");
        }

        if (mPageAdapter.getRegisteredFragment(0) != null) {
            StatusFragment status = (StatusFragment) mPageAdapter.getRegisteredFragment(0);
            status.handleMessage(msg);
        }

        if (mPageAdapter.getRegisteredFragment(1) != null) {
            LogFragment test = (LogFragment) mPageAdapter.getRegisteredFragment(1);
            test.handleMessage(msg);
        }

        return false;
    }

    @Override
    public void handleConnect() {
        final Intent connectIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(connectIntent, REQUEST_PICK_DEVICE);
    }

    @Override
    public void handleDisconnect() {
        mBluetoothService.disconnect();
    }

    public static class PageAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StatusFragment();
                case 1:
                    return new LogFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Status";
                case 1:
                    return "Test";
                default:
                    return "Unknown";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mRegisteredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return mRegisteredFragments.get(position);
        }
    }
}
