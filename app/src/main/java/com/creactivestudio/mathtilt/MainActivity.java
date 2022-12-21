package com.creactivestudio.mathtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.SortedMap;

public class MainActivity extends AppCompatActivity {

    // Save user sound settings via SharedPreferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Sound Control Image gets a boolean value
    private ImageView imgSoundControlMain;
    private TextView tvAllSensors;
    public boolean isSoundOn;

    // A list of all sensors that available in the device
    private List <Sensor> sensorList;


    private SensorManager mSensorManager = null;
    private ImageView imgSeeAllSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initialization of the Views etc.
        init();
        tvAllSensors.setVisibility(View.INVISIBLE); // By default TextView is invisible
        setAllSensorsTextView(); // Set the textview with device available Sensors


        // User can control the App sound with these sound icon
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

        // Show a list of all Sensors from the device
        imgSeeAllSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvAllSensors.getVisibility()== View.INVISIBLE){
                    tvAllSensors.setVisibility(View.VISIBLE);
                }
                else tvAllSensors.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Initialize the Views
     */
    public void init () {
        imgSoundControlMain = findViewById(R.id.imgSoundControlMain);
        sharedPreferences = getSharedPreferences("setting_pref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mSensorManager= (SensorManager) getSystemService(MainActivity.this.SENSOR_SERVICE);
        sensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
        tvAllSensors=findViewById(R.id.textView5);
        imgSeeAllSensors=findViewById(R.id.imgSeeAllSensors);
    }

    /**
     * Set the textview with device available Sensors
     */
    public void setAllSensorsTextView () {
        String allSensors="";
        for(int i=0; i<sensorList.size(); i++)
        {
            allSensors+= sensorList.get(i).getStringType()+"\n";
        }
        tvAllSensors.setText(allSensors);
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