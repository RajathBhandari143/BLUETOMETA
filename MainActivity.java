package com.example.bluetometa_device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ADDRESS = "00:00:00:00:00:00"; // Replace with your Bluetooth module address
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 1;

    private TextView statusTextView;
    private Button roomButton;
    private Button kitchenButton;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private ConnectedThread connectedThread;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConnectedThread.MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    // Handle the received message
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        Button connectButton = findViewById(R.id.connectButton);
        roomButton = findViewById(R.id.roomButton);
        kitchenButton = findViewById(R.id.kitchenButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToBluetooth();
            }
        });

        roomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RoomActivity
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                startActivity(intent);
            }
        });

        kitchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to KitchenActivity
                Intent intent = new Intent(MainActivity.this, KitchenActivity.class);
                startActivity(intent);
            }
        });
    }

    private void connectToBluetooth() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();
            statusTextView.setText("Status: Connected");
            roomButton.setEnabled(true);
            kitchenButton.setEnabled(true);
        } catch (IOException e) {
            statusTextView.setText("Status: Connection Failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth connect permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private static final int MESSAGE_READ = 1;
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String message) {
            try {
                outStream.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }
}
