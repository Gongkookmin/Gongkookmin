package com.example.gongkookmin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

        /* 작성자 : 이재욱
           업데이트 : 2019년 11월 30일 12시
           이용 약관 바로 가기를 누르면 TOSActivity로 넘어간다. */
        TextView linkToTOS = (TextView)findViewById(R.id.tosTextView);
        linkToTOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TOSActivity.class);
                startActivity(intent);
            }
        });

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
                    JSONObject json = new JSONObject();
                    try {
                        JSONArray array = new JSONArray();
                        JSONObject user = new JSONObject();
                        user.put("username", email);
                        user.put("password",password);
                        user.put("first_name","");
                        user.put("last_name","");
                        user.put("USE",use);
                        array.put(user);
                        json.put("user",user);
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    BackgroundTask task = new BackgroundTask();
                    task.execute(getResources().getString(R.string.server_address)+"users/create",
                            HttpRequestHelper.POST,json.toString());
                }
            }
        });
    }

    class BackgroundTask extends CommunicationTask {

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            JSONObject jsonObject;
            jsonObject = httpRequestHelper.getDataByJSONObject();   // 서버에게 받은 json
            if(jsonObject == null) {
                Toast.makeText(RegisterActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            if(values[0]) {
                try {
                    if (jsonObject.getString("response").equals("error")) {
                        Log.d("data ",httpRequestHelper.getData());
                        Toast.makeText(RegisterActivity.this, "회원가입 양식을 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("response").equals("success")){
                        Toast.makeText(RegisterActivity.this, "회원가입이 정상적으로 처리되었습니다. 메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(RegisterActivity.this, "아이디 혹은 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
