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
    }

    public void operationSelected (View view) {

        Intent intent = new Intent(MathOperationsActivity.this, GameActivity.class);

        switch (view.getTag().toString()) {
            case "multi":
                selectedOperation="multi";
                Toast.makeText(this, "multi", Toast.LENGTH_SHORT).show();

                break;
            case "dupli":
                selectedOperation="dupli";
                Toast.makeText(this, "dupli", Toast.LENGTH_SHORT).show();

                break;
            case "minus":
                selectedOperation="minus";
                Toast.makeText(this, "minus", Toast.LENGTH_SHORT).show();

                break;
            default: selectedOperation="plus";
                Toast.makeText(this, "plus", Toast.LENGTH_SHORT).show();



        }

        intent.putExtra("selected_operation", selectedOperation);
        startActivity(intent);
    }
}