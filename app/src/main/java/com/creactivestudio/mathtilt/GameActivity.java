package com.creactivestudio.mathtilt;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final int THRESHOLD = 15;

    ProgressBar progressBarLinear;
    Random randomNumber;
    private TextView tvTargetNumber, tvCalculation;
    CountDownTimer countDownTimer;
    int RandomTargetNumber;
    double progress;
    int progressBarProgress = 5;
    int currentCalculationNumber = 0;

    // To get the data from Intent
    Bundle bundle;
    String selectedMathOperation;

    // Listen the Orientation Changes from device
    OrientationEventListener orientationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();

        bundle = getIntent().getExtras();
        if (bundle != null) { // Control the bundle if it is null or not
            selectedMathOperation = bundle.getString("selected_operation"); // when not null is than get the value
        }


        // Set the orientation event listener to get the changes
        // User gets the random number if device from landscape to portrait mode switches
        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_GAME) {
            public void onOrientationChanged(int orientation) {
                if (isLandscape(orientation)) {
                    // Do something in Landscape Mode
                } else if (isPortrait(orientation)) { // When the user switches to portrait mode
                    // stop counter .. get the random number and change activity  ...
                    countDownTimer.cancel();
                    //  tvCalculation.setText(currentCalculationNumber);
                    startActivity(new Intent(GameActivity.this, MathOperationsActivity.class));
                    orientationListener.disable();
                }
            }
        };

        setTargetTextView();
        startCounter(); // Starts the game logic

    }

    /**
     * Figure out if the device in Landscape Mode
     * @param orientation
     * @return
     */
    private boolean isLandscape(int orientation) {// >75 - <105
        return orientation >= (90 - THRESHOLD) && orientation <= (90 + THRESHOLD);
    }

    private boolean isPortrait(int orientation) { //>345 - <360   >0 - <15
        return (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD);
    }


    /**
     * Initialation the views etc.0
     */
    public void init() {
        tvTargetNumber = findViewById(R.id.tvQ);
        randomNumber = new Random();
        progressBarLinear = findViewById(R.id.progressBar2);
        tvCalculation = findViewById(R.id.tvCalculation);

    }

    /**
     * Return an int number for level
     *
     * @param level
     * @return
     */
    public int getRandomNumberForQuestion(int level) {
        int returnValue = 0;
        if (level == 1) {
            returnValue = randomNumber.nextInt(100);
        }
        return returnValue;
    }

    /**
     * Get a random number to calculate
     *
     * @param level
     * @return
     */
    public int getRandomNumberForCalculation(int level) {
        int returnValue = 0;
        if (level == 1) {
            returnValue = randomNumber.nextInt(10) + 1;// Starts with 1 not with 0
        }
        return returnValue;
    }

    /**
     * Start Count Down Timer and the Game
     */
    public void startCounter() {
        progressBarLinear.setVisibility(View.VISIBLE);
        progress = 100;
        progressBarLinear.setProgress(progressBarProgress);

        countDownTimer = new CountDownTimer(4100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBarProgress--;
                progressBarLinear.setProgress((int) progressBarProgress * 100 / (5000 / 1000));
            }

            @Override
            public void onFinish() {

                tvCalculation.setText(RandomTargetNumber + " = ");
                tvTargetNumber.setText("");
                progressBarLinear.setVisibility(View.INVISIBLE);
                startCounter();
                progressBarProgress = 5;
                updateRandomNumberTextView();
            }
        };
        countDownTimer.start();
    }

    /**
     * Get a random number and than set the target TextView
     */
    public void setTargetTextView() {
        RandomTargetNumber = getRandomNumberForQuestion(1);
        String targetNumberText = RandomTargetNumber + "";
        tvTargetNumber.setText(targetNumberText);

    }

    /**
     *
     */
    public void updateRandomNumberTextView() {

        currentCalculationNumber = getRandomNumberForCalculation(1);
        tvTargetNumber.setText(String.valueOf(currentCalculationNumber));

    }


    @Override
    protected void onResume() {
        super.onResume();
        orientationListener.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationListener.disable();

    }
}
