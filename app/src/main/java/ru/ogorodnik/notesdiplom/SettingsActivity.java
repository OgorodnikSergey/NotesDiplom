package ru.ogorodnik.notesdiplom;

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
    private final int pinSize = 4;
    private boolean hidePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeComponents();

        setButtonHideClickListener();
        setButtonSaveClickListener();
    }

    private void initializeComponents() {
        buttonSave = findViewById(R.id.buttonSave);
        buttonHide = findViewById(R.id.buttonHide);
        editPin = findViewById(R.id.editPin);
        editPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void setButtonSaveClickListener() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editPin.getText().length() != pinSize){
                    Toast.makeText(getApplicationContext() , getString(R.string.pin_size_message), Toast.LENGTH_LONG).show();
                } else {
                    String pin = String.valueOf(editPin.getText());
                    App.getKeyStore().saveKey(pin);
                    Toast.makeText(getApplicationContext() , getString(R.string.pin_saved_message), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void setButtonHideClickListener() {
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
    }
}
