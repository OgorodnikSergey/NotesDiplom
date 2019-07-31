package ru.ogorodnik.notesdiplom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonSave;
    private ImageButton buttonHide;
    private EditText editPin;
    private int pinSize;
    private boolean hidePass;
    private String PIN = "PIN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSave = findViewById(R.id.buttonSave);
        buttonHide = findViewById(R.id.buttonHide);
        editPin = findViewById(R.id.editPin);
        pinSize = 4;
        editPin.setTransformationMethod(PasswordTransformationMethod.getInstance());

        buttonHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidePass){
                    editPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hidePass = false;
                } else {
                    editPin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hidePass = true;
                }
            }
        });

        boolean save = false;
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editPin.getText().length() != pinSize){
                    Toast.makeText(getApplicationContext() , "Pin size must be equal 4", Toast.LENGTH_LONG).show();
                } else {
                    String pin = String.valueOf(editPin.getText());
                    int hash = pin.hashCode();
                    SharedPreferences sp = getSharedPreferences("password", Context.MODE_PRIVATE);
                    sp.edit().putString(PIN, String.valueOf(hash)).commit();
                    Toast.makeText(getApplicationContext() , "Pin saved", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                    }
            }
        });
    }

}
