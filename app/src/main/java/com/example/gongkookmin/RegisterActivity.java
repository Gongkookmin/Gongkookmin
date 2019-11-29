package com.example.gongkookmin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("^[a-zA-Z0-9]+@kookmin.ac.kr+$", Pattern.CASE_INSENSITIVE);
    public static final Pattern EMAIL_PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");


    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }


    private boolean checkPassword(String password) {
        return EMAIL_PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText emailEdit = (EditText)findViewById(R.id.mailText);
        final EditText passwordEdit = (EditText)findViewById(R.id.passwordText);
        final EditText passwordCheckEdit = (EditText)findViewById(R.id.passwordCheckText);
        final RadioButton Use_radio = (RadioButton)findViewById(R.id.btnAgree);

        final ImageView correctImg = (ImageView)findViewById(R.id.imgNotSame);

        /* 작성자 : 이재욱 */
        passwordCheckEdit.addTextChangedListener(new TextWatcher() {    // Checking password
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEdit.getText().toString().equals(passwordCheckEdit.getText().toString())) { correctImg.setImageResource(android.R.drawable.presence_online); }
                else { correctImg.setImageResource(android.R.drawable.ic_delete); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        /* 작성자 조영완 */
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {    // Make json object contains 'email', 'password', 'use'
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                boolean use = Use_radio.isChecked();
                if(!checkEmail(email)){
                    Toast.makeText(getApplicationContext(), "Email 형식을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(!checkPassword(password)){
                    Toast.makeText(getApplicationContext(), "비밀번호 형식을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(!passwordEdit.getText().toString().equals(passwordCheckEdit.getText().toString())){
                    Toast.makeText(getApplicationContext(), "비밀번호가 확인과 같은지 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(!use){
                    Toast.makeText(getApplicationContext(), "이용약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final JsonMaker json = new JsonMaker();

                    json.putData("EMAIL", email);
                    json.putData("password", password);
                    json.putData("USE", use);

                    final String data = json.toString();
                    System.out.print(data + "\n");


                    Toast.makeText(getApplicationContext(), "회원가입이 성공적으로 진행됐습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
