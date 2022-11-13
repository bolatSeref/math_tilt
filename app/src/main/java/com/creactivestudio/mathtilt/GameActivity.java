package com.creactivestudio.mathtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Random randomNumber;
    private TextView tvTargetNumber, tvCalculation;
    CountDownTimer countDownTimer;
    int RandomTargetNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        init();
        setTargetTextView();
        startGame();
    }

    /**
     * Initialation the views etc.0
     */
    public void init () {
        tvTargetNumber=findViewById(R.id.tvQ);
        randomNumber=new Random();
        progressBar=findViewById(R.id.progressBar);
        tvCalculation=findViewById(R.id.tvCalculation);
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

    /**
     * Start Count Down Timer and the Game
     */
    public void startGame () {
        countDownTimer = new CountDownTimer(2100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              //  tvCountDownApp.setTextSize(100);
               // tvCountDownApp.setText(millisUntilFinished / 1000 + "");
            }

            @Override
            public void onFinish() {

                progressBar.setVisibility(View.INVISIBLE);
                tvCalculation.setText(RandomTargetNumber+" = ");
                tvTargetNumber.setText("");

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


}