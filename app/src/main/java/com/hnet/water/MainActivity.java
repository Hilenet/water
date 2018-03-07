package com.hnet.water;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static int STATE_READY = 0;
    public static int STATE_DONE = 1;

    private FrameLayout mFrameLayout = null;
    private TimePicker mTimePicker = null;
    private TextView mTextView = null;
    private Button setButton = null;
    private Button cancelButton = null;

    private AlarmManager manager = null;
    private PendingIntent pendingIntent = null;

    private Calendar target = null;
    private static String TAG = "Water";
    private static String LOCAL_FILE = "Millisec";
    private int requestCode = 1;
    private int state = STATE_READY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // データ残ってたら
        readRecord();
        state = target==null ? STATE_READY : STATE_DONE;

        setupIntent();
        setupUI();
        updateUI();
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


    private void setupUI() {
        mFrameLayout = findViewById(R.id.frameLayout);

        mTimePicker = new TimePicker(this);
        mTextView = new TextView(this);
        mTextView.setTextSize(100);
        mTextView.setGravity(Gravity.CENTER);

        setButton = findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
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

                state = STATE_DONE;
                updateUI();
            }
        });

        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlert();

                state = STATE_READY;
                updateUI();
            }
        });

        Log.d(TAG, "setup ui");
    }

    protected void cancelAlert() {
        manager.cancel(pendingIntent);
        writeRecord(0);
        Toast.makeText(this, "water unset", Toast.LENGTH_SHORT).show();
    }

    protected void registerAlert(Calendar c) {
        // pendingIntentがFlag_update_currentなので上書き
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        target = c;
        writeRecord(c.getTimeInMillis());
        //Toast.makeText(this, "water setting", Toast.LENGTH_SHORT).show();
    }

    protected void updateUI() {
        mFrameLayout.removeAllViews();

        if(state == STATE_READY) {
            setButton.setVisibility(Button.VISIBLE);
            cancelButton.setVisibility(Button.INVISIBLE);

            mFrameLayout.addView(mTimePicker);


        } else if (state == STATE_DONE) {
            setButton.setVisibility(Button.INVISIBLE);
            cancelButton.setVisibility(Button.VISIBLE);

            String text =
                    Integer.toString(target.get(Calendar.HOUR_OF_DAY))
                    + " : "
                    + Integer.toString(target.get(Calendar.MINUTE));
            mTextView.setText(text);
            mFrameLayout.addView(mTextView);
        }
    }


    /**
     * write time
     * @param ms
     */
    private void writeRecord(long ms) {
        Toast.makeText(this, "write: "+String.valueOf(ms), Toast.LENGTH_SHORT).show();

        OutputStream out;
        try {
            out = openFileOutput(LOCAL_FILE, MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            Log.d(TAG, "write success");
            writer.print(ms);
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            Log.d(TAG, "err");
        }

    }

    /**
     * read ms
     */
    private void readRecord() {
        // time = read
        // calendar = From(time)
        InputStream in;
        String lineBuffer = null;

        try {
            in = openFileInput(LOCAL_FILE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            /*
            while( (lineBuffer = reader.readLine()) != null ){
                Log.d("FileAccess",lineBuffer);
            }
            */
            lineBuffer = reader.readLine();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            //e.printStackTrace();
            Log.d(TAG,"err");
        }

        lineBuffer = (lineBuffer==null) ? "0" : lineBuffer;
        Log.d(TAG, "read str, "+lineBuffer);

        long ms = Long.parseLong(lineBuffer);
        Toast.makeText(this, "load: "+String.valueOf(ms), Toast.LENGTH_SHORT).show();

        target = Calendar.getInstance();
        if(target.getTimeInMillis() >= ms) {
            target = null;
        } else {
            target.setTimeInMillis(ms);
        }

        Log.d(TAG, "read ms: "+String.valueOf(ms) );

        return;
    }
}


