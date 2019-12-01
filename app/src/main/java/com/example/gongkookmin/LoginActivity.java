package com.example.gongkookmin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9]+@kookmin.ac.kr+$", Pattern.CASE_INSENSITIVE);

    Button btn_login;
    EditText input_id;
    EditText input_pw;

    private boolean checkEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        input_id = findViewById(R.id.inputID_EditText);
        input_pw = findViewById(R.id.inputPW_EditText);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = input_id.getText().toString();
                String pw = input_pw.getText().toString();

                if (id.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호 모두 입력하셔야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if (!checkEmail(id)) {
                    Toast.makeText(LoginActivity.this, "국민대학교 이메일 형식인지 확인하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    JsonMaker json = new JsonMaker();
                    json.putData("username", id);
                    json.putData("password", pw);

                    BackgroundTask task = new BackgroundTask();
                    task.execute(getResources().getString(R.string.server_address) + "token-auth/"
                            , HttpRequestHelper.POST, json.toString());
                }
            }
        });

        Button btn_go_register = findViewById(R.id.btn_go_register);

        btn_go_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
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
                Toast.makeText(LoginActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            if(values[0]){
                String token;
                try {
                    token = jsonObject.getString("token");
                    TokenHelper tokenHelper = new TokenHelper(getSharedPreferences("pref",MODE_PRIVATE));
                    if(tokenHelper.setToken(token)) {   // token 이 성공적으로 저장된다면
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            else{
                Toast.makeText(LoginActivity.this, ""+httpRequestHelper.getData(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
