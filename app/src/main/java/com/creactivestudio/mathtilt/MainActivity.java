package com.creactivestudio.mathtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView btnStartGame; // Start button
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Sound Control Image gets a boolean value
    private ImageView imgSoundControlMain;
    public boolean isSoundOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization views etc.
        init();



        imgSoundControlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSoundOn = sharedPreferences.getBoolean("voice_on", true);
                if (isSoundOn) {
                    imgSoundControlMain.setImageResource(R.drawable.ic_music_turnoff);
                    editor.putBoolean("voice_on", false);
                    editor.commit();
                    stopService(new Intent(MainActivity.this,AudioPlayService.class));
                } else {
                    startService(new Intent(MainActivity.this,AudioPlayService.class));
                    imgSoundControlMain.setImageResource(R.drawable.ic_music_turnon);
                    editor.putBoolean("voice_on", true);
                    editor.commit();
                }

            }
        });
    }

    /**
     * Initialize the Views
     */
    public void init () {
        btnStartGame = findViewById(R.id.btnStartGame);
        imgSoundControlMain = findViewById(R.id.imgSoundControlMain);
        sharedPreferences = getSharedPreferences("setting_pref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    /**
     * Starts the Game
     * @param view
     */
    public void startGame (View view) {
        startActivity(new Intent(MainActivity.this, GameActivity.class));

    }



    /**
     * Control the user sound settings from SharedPreferences and than set the views correctly
     * When sound on is than start Game Music
     */
    public void soundControl()
    {
        isSoundOn = sharedPreferences.getBoolean("voice_on", true);
        if (isSoundOn) {

            imgSoundControlMain.setImageResource(R.drawable.ic_music_turnon);
            startService(new Intent(this,AudioPlayService.class));

        } else {
            imgSoundControlMain.setImageResource(R.drawable.ic_music_turnoff);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        soundControl();
    }
}