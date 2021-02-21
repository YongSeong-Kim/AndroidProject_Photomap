package com.example.workspace1.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.workspace1.MainActivity;
import com.example.workspace1.R;
import com.example.workspace1.utility.AsyncTaskCallback;
import com.example.workspace1.utility.NetworkTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements AsyncTaskCallback {
    private EditText l_id, l_pass;
    private Button btn_login, btn_register;
    private String username, password;
    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        l_id = findViewById(R.id.l_id);
        l_pass = findViewById(R.id.l_pass);
        btn_login = findViewById(R.id.l_btn_login);
        btn_register = findViewById(R.id.l_btn_register);

        queue = Volley.newRequestQueue(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://"+getString(R.string.ipport)+"/user/login/";
                String method = "POST";

                //POST-body에 들어가는 jsonobject 생성
                Map<String, String> params = new HashMap<String, String>();
                Log.d("유저네임", "들어갑니다.");
                params.put("username", l_id.getText().toString());
                params.put("email", "");
                params.put("password", l_pass.getText().toString());
                JSONObject parameters = new JSONObject(params);

                /// FOR DEBUG ///
                //Log.d("test", l_id.getText().toString());
                //Log.d("test", l_pass.getText().toString());
                /// FOR DEBUG ///

                //NetworkTaskAsync 호출로 login 요청하기
                NetworkTask networkTask = new NetworkTask(url, null, method ,parameters, LoginActivity.this);
                networkTask.execute();


            }
        });
    }

    @Override
    public void method2(String s) {

        System.out.println("now response : " + s);

        //JsonParser jsonParser = new JsonParser();
        //JsonObject collections = (JsonObject) jsonParser.parse(s);
        //String verified = collections.get("message").getAsString();
        String verified = s.substring(8,15);
        System.out.println("verified :  " + verified);


        if(verified.equals("SUCCESS"))
        {
            Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else { // 로그인에 실패한 경우
            Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
        }
    }
}