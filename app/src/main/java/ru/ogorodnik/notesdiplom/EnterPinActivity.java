package ru.ogorodnik.notesdiplom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class EnterPinActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button button0;
    private Button buttonBackSpace;
    private RatingBar ratingBar;  // Самый простой вариант из тех что нагуглил, проще, чем рисовать  крожочки и закрашивать их

    private static final String PIN = "PIN";
    private static final int PINSIZE = 4;
    private String enteredPin;
    private int progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ratingBar = findViewById(R.id.ratingBar);

        enteredPin = "";
        progress = 0;

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        button0 = findViewById(R.id.button0);
        buttonBackSpace = findViewById(R.id.buttonBackspace);

        buttonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin = removeLastChar(enteredPin);
                progress--;
                checkPin();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "1";
                progress++;
                checkPin();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "2";
                progress++;
                checkPin();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "3";
                progress++;
                checkPin();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "4";
                progress++;
                checkPin();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "5";
                progress++;
                checkPin();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "6";
                progress++;
                checkPin();
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "7";
                progress++;
                checkPin();
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "8";
                progress++;
                checkPin();
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "9";
                progress++;
                checkPin();
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPin += "0";
                progress++;
                checkPin();
            }
        });
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private void checkPin()
    {
        ratingBar.setRating(progress);
        SharedPreferences sp = getSharedPreferences("password", Context.MODE_PRIVATE);
        String pinhash = sp.getString(PIN, "");
        if (enteredPin.length() == PINSIZE)
        {
            if (String.valueOf(enteredPin.hashCode()).equals(pinhash)){
                setResult(RESULT_OK);
                finish();
            } else {
                enteredPin = "";
                progress -= PINSIZE;
                ratingBar.setRating(progress);
                Toast.makeText(getApplicationContext() , "Invalid PIN", Toast.LENGTH_LONG).show();
            }
        }
    }
}
