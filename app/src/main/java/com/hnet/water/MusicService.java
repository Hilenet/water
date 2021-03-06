package com.hnet.water;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service {
    private static String TAG = "service";
    private MediaPlayer mediaPlayer = null;


    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.jingle);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);

        playMusic();

        Log.d(TAG, "run Command");

        sendTransaction();

        return Service.START_NOT_STICKY;
    }


    public void playMusic() {
        //Toast.makeText(getApplication(), "watered", Toast.LENGTH_SHORT).show();

        mediaPlayer.start();
    }

    public void sendTransaction() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("transition", "watered");
        broadcastIntent.setAction(MainActivity.WATER_ACTION);
        getBaseContext().sendBroadcast(broadcastIntent);
    }
}
