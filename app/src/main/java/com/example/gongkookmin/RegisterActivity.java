package com.example.gongkookmin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText emailedit = (EditText)findViewById(R.id.mailText);
        EditText passwordeidt = (EditText)findViewById(R.id.passwordText);
        RadioButton Use_radio = (RadioButton)findViewById(R.id.btnAgree);
        String email = emailedit.getText().toString();
        String password = passwordeidt.getText().toString();
        boolean use = Use_radio.isChecked();

        try {
            final JSONObject file = new JSONObject();

            file.put("EMAIL", email);
            file.put("password", password);
            file.put("USE", use);

            final String data = file.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
