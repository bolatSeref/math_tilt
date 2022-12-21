package com.creactivestudio.mathtilt;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

public class CountDownTimerService extends Service {

     CountDownTimer countDownTimer;
     private static final long START_TIME_IN_MILLIS = 600000;
    public static final long START_TIME_IN_MILLIS_SERVICE = 600000;
    public long mTimeLeftInMillis = START_TIME_IN_MILLIS_SERVICE;
    public CountDownTimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    public void onCreate() {
        countDownTimer=new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                Toast.makeText(CountDownTimerService.this, "selam", Toast.LENGTH_SHORT).show();
                mTimeLeftInMillis=l;

            }

            @Override
            public void onFinish() {
                Toast.makeText(CountDownTimerService.this, "bitti", Toast.LENGTH_SHORT).show();
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
