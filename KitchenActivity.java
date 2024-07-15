package com.example.bluetometa_device;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class KitchenActivity extends AppCompatActivity {

    private Button offBuzzerButton;
    private boolean isGasDetected = false; // This should be updated based on your gas sensor input

    private static final String CHANNEL_ID = "GasAlertChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        offBuzzerButton = findViewById(R.id.offBuzzerButton);

        offBuzzerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Turn off the buzzer
                if (isGasDetected) {
                    isGasDetected = false;
                    updateBuzzerStatus();
                }
            }
        });

        // Create notification channel
        createNotificationChannel();

        // Simulate gas detection for demonstration purposes
        detectGas();
    }

    private void detectGas() {
        isGasDetected = true;
        updateBuzzerStatus();
        sendGasAlertNotification();
    }

    private void updateBuzzerStatus() {
        if (isGasDetected) {
            // Code to turn on the buzzer
            offBuzzerButton.setEnabled(true);
            Toast.makeText(this, "Gas detected! Buzzer is on.", Toast.LENGTH_SHORT).show();
        } else {
            // Code to turn off the buzzer
            offBuzzerButton.setEnabled(false);
            Toast.makeText(this, "Buzzer is off.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendGasAlertNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_gas_alert)
                .setContentTitle("Gas Alert")
                .setContentText("Gas detected in the kitchen! Please take action.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Gas Alert Channel";
            String description = "Channel for gas detection alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
