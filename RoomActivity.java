package com.example.bluetometa_device;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RoomActivity extends AppCompatActivity {

    private Button manualOnOffButton;
    private Button sleepModeButton;
    private boolean isLightOn = false;

    private final Handler sleepHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // Handle sleep timer expiration
            if (msg.what == 1) {
                // Notify user
                Toast.makeText(RoomActivity.this, "Sleep timer expired", Toast.LENGTH_SHORT).show();
                // Optionally turn off the light or snooze
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        manualOnOffButton = findViewById(R.id.manualOnOffButton);
        sleepModeButton = findViewById(R.id.sleepModeButton);

        manualOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLightOn = !isLightOn;
                updateLightStatus();
            }
        });

        sleepModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepTimerDialog();
            }
        });
    }

    private void updateLightStatus() {
        if (isLightOn) {
            // Send signal to turn on the light
            Toast.makeText(this, "Light turned ON", Toast.LENGTH_SHORT).show();
        } else {
            // Send signal to turn off the light
            Toast.makeText(this, "Light turned OFF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSleepTimerDialog() {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(RoomActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the sleep timer
                        setSleepTimer(hourOfDay, minute);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void setSleepTimer(int hourOfDay, int minute) {
        // Calculate delay in milliseconds
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

        // Set a handler to notify when the timer expires
        sleepHandler.sendEmptyMessageDelayed(1, delay);
        Toast.makeText(this, "Sleep timer set", Toast.LENGTH_SHORT).show();
    }
}
