package com.creactivestudio.mathtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {


    SensorManager gyroscopeSensorManager;
    Sensor gyroscopeSensor;
    ProgressBar progressBar, progressBarLinear;
    Random randomNumber;
    private TextView tvTargetNumber, tvCalculation;
    CountDownTimer countDownTimer;
    int RandomTargetNumber;
    double progress;
    int progressBarProgress = 5;
    int numberType=1;
    int currentCalculationNumber=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        init();
        setTargetTextView();
        startCounter();
    }

    /**
     * Initialation the views etc.0
     */
    public void init () {
        tvTargetNumber=findViewById(R.id.tvQ);
        randomNumber=new Random();
        progressBar=findViewById(R.id.progressBar);
        progressBarLinear=findViewById(R.id.progressBar2);
        tvCalculation=findViewById(R.id.tvCalculation);
        gyroscopeSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor=gyroscopeSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }
    /**
     * Return an int number for level
     * @param level
     * @return
     */
    public int getRandomNumberForQuestion (int level) {
        int returnValue=0;
        if (level==1)
        {
            returnValue=randomNumber.nextInt(100);
        }
        return returnValue;
    }


    public int getRandomNumberForCalculation (int level) {
        int returnValue=0;
        if (level==1)
        {
            returnValue=randomNumber.nextInt(10)+1;// Starts with 1 not with 0
        }
        return returnValue;
    }

    /**
     * Start Count Down Timer and the Game
     */
    public void startCounter() {
        progressBarLinear.setVisibility(View.VISIBLE);
       // progressBarLinear.setProgress(100);

        progress = 100;
        progressBarLinear.setProgress(progressBarProgress);

        countDownTimer = new CountDownTimer(4100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              //  tvCountDownApp.setTextSize(100);
               // tvCountDownApp.setText(millisUntilFinished / 1000 + "");
                progressBarProgress--;
               // progress=progress/3;
                progressBarLinear.setProgress((int)progressBarProgress*100/(5000/1000));
            }

            @Override
            public void onFinish() {

                progressBar.setVisibility(View.INVISIBLE);
                tvCalculation.setText(RandomTargetNumber+" = ");
                tvTargetNumber.setText("");
                progressBarLinear.setVisibility(View.INVISIBLE);
                startCounter();
                progressBarProgress=5;
                updateRandomNumberTextView();
            }
        };
        countDownTimer.start();
    }

    /**
     * Get a random number and than set the target TextView
     */
    public void setTargetTextView ()
    {
        RandomTargetNumber=getRandomNumberForQuestion(1);
        String targetNumberText= RandomTargetNumber+"";
        tvTargetNumber.setText(targetNumberText);

    }

    /**
     *
     */
    public void updateRandomNumberTextView () {

            currentCalculationNumber=getRandomNumberForCalculation(1);
            tvTargetNumber.setText(String.valueOf(currentCalculationNumber));

    }



    private SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // More code goes here
            if(sensorEvent.values[0] > 0.5f) { // anticlockwise
               // getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                Toast.makeText(GameActivity.this, "anti clock", Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(GameActivity.this, MathOperationsActivity.class));
            } else if(sensorEvent.values[0] < -0.5f) { // clockwise
               // getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                Toast.makeText(GameActivity.this, " clock", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        gyroscopeSensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscopeSensorManager.unregisterListener(gyroscopeSensorListener);

    }


}