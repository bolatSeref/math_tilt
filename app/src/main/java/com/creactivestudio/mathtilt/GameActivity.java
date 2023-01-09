package com.creactivestudio.mathtilt;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;


/**
 * Second Activity after MainActivity
 * User can play the game
 * Users points saved automatically
 * User can start and stop the game music
 *
 */
public class GameActivity extends AppCompatActivity {
    private static final int THRESHOLD = 15;
    ProgressBar progressBarLinear;
    Random randomNumber;
    private TextView tvTargetNumber, tvCalculation, tvCalculationResult, text_operand, tvPoints;
    private static final String APP_LOG = "APP_LOG_";
    CountDownTimer countDownTimerForRandomNumbers, countDownTimerForGame;
    int RandomTargetNumber;
    double progress;
    int progressBarProgress = 5;
    int currentCalculationNumber = 0;
    TextView tvTime, tvUserSelectedNumbers;
     AlertDialog.Builder builder;
    AlertDialog dialogMathOperations;
    // Listen the Orientation Changes from device
    OrientationEventListener orientationListener;
    SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    SensorEventListener gyroscopeSensorListener;
    private static final long START_TIME_IN_MILLIS = 150000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    ArrayList<Integer> numbersForCalculation;
    ArrayList<Character> mathOperationList;
    ArrayList<Object> calculationDataList;
    public boolean isSoundOn;
    // Save user points via SharedPreferences
    private SharedPreferences sharedPreferences, sharedPreferencesSound;
    private SharedPreferences.Editor editor, editorSound;
    ImageView imgSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        init();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Set the orientation event listener to get the changes
        // User gets the random number if device from landscape to portrait mode switches
        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_GAME) {
            public void onOrientationChanged(int orientation) {
                Log.d(APP_LOG, "orientation : " + orientation);
                if (isLandscape(orientation)) {
                    // Do something in Landscape Mode
                } else if (isPortrait(orientation)) { // When the user switches to portrait mode
                    // stop counter .. get the random number and change activity  ...
                   /*
                    Log.d(APP_LOG, "inside orientation : " + orientation);
                    countDownTimerForRandomNumbers.cancel();
                    // save current number to the list
                    calculationDataList.add(currentCalculationNumber);
                    showMathOperationsDialog();
                    orientationListener.disable();

                    */
                }
            }
        };

        setTargetTextView();
        startCounterForRandomNumbers(); // Starts the game logic
        startGameTimer();
        startService(new Intent(GameActivity.this, CountDownTimerService.class));
        setUserPoints();

    }


    /**
     * Figure out if the device in Landscape Mode
     *
     * @param orientation
     * @return
     */
    private boolean isLandscape(int orientation) {// >75 - <105
        return orientation >= (90 - THRESHOLD) && orientation <= (90 + THRESHOLD);
    }


    /**
     * Check if the orientation Portrait is
     * @param orientation
     * @return
     */
    private boolean isPortrait(int orientation) { //>345 - <360   >0 - <15
        return (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD);
    }

    /**
     * Show Sensor Dialog after button click event.
     * @param view
     */
    public void showSensorDialog (View view)
    {
        countDownTimerForRandomNumbers.cancel();
        // save current number to the list
        calculationDataList.add(currentCalculationNumber);
        showMathOperationsDialog();
    }


    /**
     * Show the Custom Alert Dialog for to get Sensor data
     */
    private void showMathOperationsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_math_operations_layout, null);
        builder.setCancelable(false);
        builder.setView(view);

        dialogMathOperations = builder.create();
        dialogMathOperations.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogMathOperations.show();
        countDownTimerForGame.cancel();

        TextView txtCurrentNumber = view.findViewById(R.id.txt_current_number);

        txtCurrentNumber.setText(MessageFormat.format("{0}", currentCalculationNumber));

        // Create a listener
        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d(APP_LOG, "0 : " + sensorEvent.values[0]);
                Log.d(APP_LOG, "1 : " + sensorEvent.values[1]);
                Log.d(APP_LOG, "2 : " + sensorEvent.values[2]);

                if (sensorEvent.values[0] > 0.5f) { // anticlockwise x-axis fixed (pitch)
                    Log.d(APP_LOG, "right");
                    unregisterListener();
                    selectedMathOperand('-', "minus");
                    Log.d(APP_LOG, "minus");
                } else if (sensorEvent.values[0] < -0.5f) { // clockwise x-axis fixed (pitch)
                    Log.d(APP_LOG, "left");
                    unregisterListener();
                    selectedMathOperand('+', "addition");
                    Log.d(APP_LOG, "addition");
                }

                if (sensorEvent.values[1] > 0.5f) { // anticlockwise y-axis fixed (yaw)
                    Log.d(APP_LOG, "down");
                    unregisterListener();
                    selectedMathOperand('*', "multiple");
                    Log.d(APP_LOG, "multiple");
                } else if (sensorEvent.values[1] < -0.5f) { // clockwise y-axis fixed (yaw)
                    Log.d(APP_LOG, "up");
                    unregisterListener();
                    selectedMathOperand('/', "division");
                    Log.d(APP_LOG, "division");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

        };

        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    /**
     * Get the Sensor Data and save the data to lists
     * Update TextViews
     * Operand can be {+,*,/,-}
     *
     * @param operand
     * @param operationName
     */
    private void selectedMathOperand(char operand, String operationName) {
        Log.d(APP_LOG, operationName);
        text_operand.setText(String.format("%s", operand));
        startGameTimer();
        countDownTimerForRandomNumbers.start();
        numbersForCalculation.add(currentCalculationNumber);
        mathOperationList.add(operand);
        // calculationDataList.add(currentCalculationNumber);
        calculationDataList.add(operand);
        setCalculationTextView();
        setCalculationResultTextView();

    }

    /**
     * After getting the Sensor Data, close the dialog and unregister Gyroscope Listener
     * to save user resources
     */
    private void unregisterListener() {
        dialogMathOperations.dismiss(); // Close the dialog
        orientationListener.enable();
        sensorManager.unregisterListener(gyroscopeSensorListener); // Unregister GyroscopeListener
    }


    /**
     * Get the math operation from list and then update the calculation textview
     */
    public void setCalculationTextView() {
        //String calculation = RandomTargetNumber + " = ";
        String calculation = "";

        for (int i = 0; i < calculationDataList.size() - 1; i++) {
            calculation += calculationDataList.get(i);
        }

        tvUserSelectedNumbers.setText(calculation);
    }

    /**
     * Set the Calculation Result Text View and control if the user has
     * the target number reached
     * When the user reaches the target number than restart the game
     * when the user has not reached the target number than game goes on
     */
    public void setCalculationResultTextView() {
        String calculation = "";

        for (int i = 0; i < calculationDataList.size() - 1; i++) {
            calculation += calculationDataList.get(i);
        }
        Log.d(APP_LOG, "calculation : " + calculation);
        if (calculationDataList.size() > 2) {
           int number1 = (int) eval(calculation);
            //number1 == RandomTargetNumber
            if (number1==RandomTargetNumber) { // User has reached the target number
                Toast.makeText(this, "Du hast dein Ziel erreicht", Toast.LENGTH_SHORT).show();
                // TODO: 06.01.2023 Restart the game - leere die Listen - restart the timer - recalculate the points
                setTargetTextView();
                startCounterForRandomNumbers(); // Starts the game logic
                startGameTimer();
                startService(new Intent(GameActivity.this, CountDownTimerService.class));
                setUserPoints();

            } else {
                tvCalculationResult.setText(number1 + "");
                calculationDataList.removeAll(calculationDataList);
                calculationDataList.add(tvCalculationResult.getText());
                calculationDataList.add(mathOperationList.get(mathOperationList.size() - 1));
                currentCalculationNumber = (int) eval(calculation);
            }
        }
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
     * Sets a new Target Number after user the question correct answered
     *
     */
    public void updateRandomNumberTextView() {
        currentCalculationNumber = getRandomNumberForCalculation(1);
        tvTargetNumber.setText(String.valueOf(currentCalculationNumber));
    }
    /**
     * Update user points when the user the target number reaches
     * Get current user points from shared preferences and than set the new point
     */
    public void setUserPoints ()
    {
        int userCurrentPoint= sharedPreferences.getInt("user_points", 0);
        editor.putInt("user_points", ++userCurrentPoint);
        editor.commit();
        tvPoints.setText(userCurrentPoint+"");
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
     * Controll the ImageView for start and stop the game music
     * Uses SharedPreferences to save user preference
     * @param view
     */
    public void soundClick (View view) {
        isSoundOn = sharedPreferences.getBoolean("voice_on", true);
        if (isSoundOn) {
            imgSound.setImageResource(R.drawable.ic_music_turnoff);
            editor.putBoolean("voice_on", false);
            editor.commit();
            stopService(new Intent(GameActivity.this,AudioPlayService.class));
        } else {
            startService(new Intent(GameActivity.this,AudioPlayService.class));
            imgSound.setImageResource(R.drawable.ic_music_turnon);
            editor.putBoolean("voice_on", true);
            editor.commit();
        }
    }

    /**
     * Control the user sound settings from SharedPreferences and than set the views correctly
     * When sound on is than start Game Music
     */
    public void soundControl()
    {
        isSoundOn = sharedPreferences.getBoolean("voice_on", true);
        if (isSoundOn) {
            imgSound.setImageResource(R.drawable.ic_music_turnon);
            startService(new Intent(this,AudioPlayService.class));

        } else {
            imgSound.setImageResource(R.drawable.ic_music_turnoff);
        }

    }
    /**
     * Start Count Down Timer and the Game
     */
    public void startCounterForRandomNumbers() {
        progressBarLinear.setVisibility(View.VISIBLE);
        progress = 100;
        progressBarLinear.setProgress(progressBarProgress);

        countDownTimerForRandomNumbers = new CountDownTimer(4100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBarProgress--;
                progressBarLinear.setProgress((int) progressBarProgress * 100 / (5000 / 1000));
            }

            @Override
            public void onFinish() {
                tvCalculation.setText("Your Target: " + RandomTargetNumber);
                tvTargetNumber.setText("");
                progressBarLinear.setVisibility(View.INVISIBLE);
                startCounterForRandomNumbers();
                progressBarProgress = 5;
                updateRandomNumberTextView();
            }
        };
        // tvCalculation.setText(RandomTargetNumber + " = ");
        countDownTimerForRandomNumbers.start();
    }

    /**
     * Starts the timer for game. Starts with 60 seconds and counts down
     */
    public void startGameTimer() {
        startService(new Intent(GameActivity.this, CountDownTimerService.class));

        countDownTimerForGame = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                //  restTimeInSeconds=startValue- l / 1000;
                mTimeLeftInMillis = l;
                tvTime.setText(l / 1000 + "");

            }

            @Override
            public void onFinish() {
                // finish the game !!!
                // control the solution .. when the user +/- 5  achieves than becomes points
            }
        }.start();
        mTimerRunning = true;
    }




    @Override
    protected void onResume() {
        super.onResume();
        soundControl();
        orientationListener.enable();
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationListener.disable();

    }

    /**
     * Get a String value as parameter to calculate and than returns
     * the result as double
     *
     * @param str
     * @return
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')'))
                            throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    /**
     * Initialation the views, lists, sharedPrefs and etc.
     */
    public void init() {
        builder = new AlertDialog.Builder(this);
        tvTargetNumber = findViewById(R.id.tvQ);
        randomNumber = new Random();
        progressBarLinear = findViewById(R.id.progressBar2);
        tvCalculation = findViewById(R.id.tvCalculation);
        tvTime = findViewById(R.id.tvTime);
        numbersForCalculation = new ArrayList<>();
        mathOperationList = new ArrayList<>();
        calculationDataList = new ArrayList<>();
        tvUserSelectedNumbers = findViewById(R.id.tvUserSelectedNumbers);
        tvCalculationResult = findViewById(R.id.tvCalculationResult);
        text_operand = findViewById(R.id.text_operand);
        sharedPreferences = getSharedPreferences("user_points", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferencesSound = getSharedPreferences("setting_pref", MODE_PRIVATE);
        editorSound = sharedPreferences.edit();
        imgSound=findViewById(R.id.imgSound);
        tvPoints=findViewById(R.id.tvPoints);
    }


}
