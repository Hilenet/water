package com.hnet.water;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TimePicker mTimePicker = null;

    private AlarmManager manager = null;
    private PendingIntent pendingIntent = null;

    private static String TAG = "Water";
    private int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        setupIntent();
    }

    private void setupUI() {
        // TimePicker
        mTimePicker = findViewById(R.id.timePicker);
        Button mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar today = Calendar.getInstance();

                Calendar c = (Calendar) today.clone();
                c.setTimeInMillis(0);
                c.set(Calendar.YEAR, today.get(Calendar.YEAR));
                c.set(Calendar.MONTH, today.get(Calendar.MONTH));
                c.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
                c.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                c.set(Calendar.MINUTE, mTimePicker.getMinute());

                registerAlert(c);

                Log.d(TAG, "registered");
            }
        });
        Log.d(TAG, "setup ui");
    }

    private void setupIntent() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent it = new Intent(this, MusicService.class);
        it.putExtra(Intent.EXTRA_TEXT, "Water");

        pendingIntent = PendingIntent.getService(
                this, requestCode, it,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "setup intent");
    }

    protected void registerAlert(Calendar c) {
        // pendingIntentがFlag_update_currentなので上書き
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "water setting", Toast.LENGTH_SHORT).show();
    }

}
