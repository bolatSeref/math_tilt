package com.creactivestudio.mathtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MathOperationsActivity extends AppCompatActivity {

    String selectedOperation="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_operations);

        stopService(new Intent(MathOperationsActivity.this, CountDownTimerService.class));
    }

    /**
     * Set selected operation in a String than pass these value via intentExtra to GameActivity
     * @param view Math Operation Image Views
     */
    public void operationSelected (View view) {

        Intent intent = new Intent(MathOperationsActivity.this, GameActivity.class);

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






        intent.putExtra("selected_operation", selectedOperation); // Pass selected value via intent
        startActivity(intent);
    }
}