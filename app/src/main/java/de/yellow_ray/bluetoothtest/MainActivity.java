package de.yellow_ray.bluetoothtest;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 42;
    private static final int REQUEST_PICK_DEVICE = 43;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Handler mHandler;
    private BluetoothService mBluetoothService;

    private TextView mStatusView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusView = (TextView) findViewById(R.id.statusText);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.hide();

        final Intent connectIntent = new Intent(this, DeviceListActivity.class);
        ((Button) findViewById(R.id.connectButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(connectIntent, REQUEST_PICK_DEVICE);
            }
        });

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
            return;
        }

        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            Log.v(TAG, "Paired: " + device.getName() + " " + device.getAddress());
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
                mStatusView.setText("Disconnected");
                mStatusView.setTextColor(Color.RED);
                mProgressDialog.hide();
                break;
            case (BluetoothService.MESSAGE_CONNECTING):
                BluetoothDevice device = (BluetoothDevice) bundle.getParcelable("device");
                mStatusView.setText("Connecting...");
                mStatusView.setTextColor(Color.YELLOW);
                mProgressDialog.setMessage("Connecting...");
                mProgressDialog.setMessage("Connecting to " + (device.getName() == null ? device.getAddress() : device.getName()));
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
                mStatusView.setText("Connected");
                mStatusView.setTextColor(Color.GREEN);
                mProgressDialog.hide();
                break;
        }
        return false;
    }
}
