package com.creactivestudio.mathtilt;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CountDownTimerService extends Service {

    CountDownTimer countDownTimer;
    private static final long START_TIME_IN_MILLIS = 600000;
    public static final long START_TIME_IN_MILLIS_SERVICE = 90000;
    public long mTimeLeftInMillis = START_TIME_IN_MILLIS_SERVICE;

    public CountDownTimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void onCreate() {
        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                Log.d("APP_LOG_", "" + l);
                mTimeLeftInMillis = l;

            }

            @Override
            public void onFinish() {
                Toast.makeText(CountDownTimerService.this, "bitti", Toast.LENGTH_SHORT).show();
                Log.d("APP_LOG_", "time finish");
            }
        }.start();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }


}
