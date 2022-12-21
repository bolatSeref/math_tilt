package com.creactivestudio.mathtilt;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class GameActivity extends AppCompatActivity {
    private static final int THRESHOLD = 15;
    String selectedOperation = "";
    ProgressBar progressBarLinear;
    Random randomNumber;
    private TextView tvTargetNumber, tvCalculation, tvCalculationResult;
    CountDownTimer countDownTimerForRandomNumbers, countDownTimerForGame;
    int RandomTargetNumber;
    double progress;
    int progressBarProgress = 5;
    int currentCalculationNumber = 0;
    TextView tvTime, tvUserSelectedNumbers;
    long restTimeInSeconds = 20000;
    // To get the data from Intent
    Bundle bundle;
    String selectedMathOperation;
    AlertDialog.Builder builder;
    AlertDialog dialogMathOperations;
    // Listen the Orientation Changes from device
    OrientationEventListener orientationListener;
    private static final long START_TIME_IN_MILLIS = 600000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    ArrayList<Integer> numbersForCalculation;
    ArrayList<Character> mathOperationList;
    ArrayList<Object> calculationDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
        System.out.println(eval("0"));
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
                    countDownTimerForRandomNumbers.cancel();
                    //  tvCalculation.setText(currentCalculationNumber);
                    // Intent intent=new Intent(GameActivity.this, MathOperationsActivity.class);
                    // intent.putExtra("rest_time", restTimeInSeconds);
                    // startActivity(intent);
                    // pauseTimer();
                    // save current number to the list
                    calculationDataList.add(currentCalculationNumber);
                    showMathOperationsDialog();
                    orientationListener.disable();
                }
            }
        };

        setTargetTextView();
        startCounterForRandomNumbers(); // Starts the game logic
        startGameTimer();
        startService(new Intent(GameActivity.this, CountDownTimerService.class));
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

    private boolean isPortrait(int orientation) { //>345 - <360   >0 - <15
        return (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD);
    }

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

        ImageView imgMultiplication = view.findViewById(R.id.img_multiplication);
        ImageView imgDuplication = view.findViewById(R.id.img_duplication);
        ImageView imgAddition = view.findViewById(R.id.img_addition);
        ImageView imgSubtraction = view.findViewById(R.id.img_subtraction);

        imgMultiplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameActivity.this, "multi", Toast.LENGTH_SHORT).show();
                startGameTimer();
                orientationListener.enable();
                countDownTimerForRandomNumbers.start();
                numbersForCalculation.add(currentCalculationNumber);
                mathOperationList.add('*');
                // calculationDataList.add(currentCalculationNumber);
                calculationDataList.add('*');
                setCalculationTextView();
                setCalculationResultTextView();

                dialogMathOperations.dismiss();

            }
        });

        imgDuplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameActivity.this, "dupli", Toast.LENGTH_SHORT).show();
                startGameTimer();
                orientationListener.enable();
                countDownTimerForRandomNumbers.start();
                numbersForCalculation.add(currentCalculationNumber);
                mathOperationList.add('/');
                //   calculationDataList.add(currentCalculationNumber);
                calculationDataList.add('/');
                setCalculationTextView();
                setCalculationResultTextView();
                dialogMathOperations.dismiss();
            }
        });

        imgAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameActivity.this, "addition", Toast.LENGTH_SHORT).show();
                startGameTimer();
                orientationListener.enable();
                countDownTimerForRandomNumbers.start();
                numbersForCalculation.add(currentCalculationNumber);
                mathOperationList.add('+');
                //  calculationDataList.add(currentCalculationNumber);
                calculationDataList.add('+');
                setCalculationTextView();
                setCalculationResultTextView();

                dialogMathOperations.dismiss();
            }
        });

        imgSubtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameActivity.this, "subtraction", Toast.LENGTH_SHORT).show();

                startGameTimer();
                orientationListener.enable();
                countDownTimerForRandomNumbers.start();
                numbersForCalculation.add(currentCalculationNumber);
                mathOperationList.add('-');
                //  calculationDataList.add(currentCalculationNumber);
                calculationDataList.add('-');
                setCalculationTextView();
                setCalculationResultTextView();

                dialogMathOperations.dismiss();
            }
        });


        /**
         * Set selected operation in a String than pass these value via intentExtra to GameActivity
         * @param view Math Operation Image Views
         */
/*
         switch (view.getTag().toString()) {
                case "multi": // Multiplication selected
                    selectedOperation = "multi";
                    Toast.makeText(this, "multi", Toast.LENGTH_SHORT).show();

                    break;
                case "dupli":// Duplication selected
                    selectedOperation = "dupli";
                    Toast.makeText(this, "dupli", Toast.LENGTH_SHORT).show();

                    break;
                case "minus":// Subtraction selected
                    selectedOperation = "minus";
                    Toast.makeText(this, "minus", Toast.LENGTH_SHORT).show();

                    break;
                default:
                    selectedOperation = "plus"; // Addition selected
                    Toast.makeText(this, "plus", Toast.LENGTH_SHORT).show();


        }


 */

    }

    /**
     * Initialation the views etc.0
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

    public void setCalculationTextView() {
        //String calculation = RandomTargetNumber + " = ";
        String calculation = "";

        for (int i = 0; i < calculationDataList.size()-1; i++) {
            calculation += calculationDataList.get(i);
        }

        tvUserSelectedNumbers.setText(calculation);
    }

    public void setCalculationResultTextView() {
        String calculation = "";

        for (int i = 0; i < calculationDataList.size()-1; i++) {
            calculation += calculationDataList.get(i);
        }
        if (calculationDataList.size() >2) {
            tvCalculationResult.setText(eval(calculation) + "");
            calculationDataList.removeAll(calculationDataList);
            currentCalculationNumber= (int) eval(calculation);
        }
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

    private void pauseTimer() {
        countDownTimerForGame.cancel();
        mTimerRunning = false;

    }

    /**
     * Get a random number and than set the target TextView
     */
    public void setTargetTextView() {
        RandomTargetNumber = getRandomNumberForQuestion(1);
        String targetNumberText = RandomTargetNumber + "";
        tvTargetNumber.setText(targetNumberText);

        //numbersForCalculation.add(RandomTargetNumber);
        //mathOperationList.add('=');
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
}
