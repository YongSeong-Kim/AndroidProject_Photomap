package com.example.workspace1.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegisterActivity extends AppCompatActivity implements AsyncTaskCallback {
    private EditText r_id, r_pass, r_name, r_age;
    private Button btn_register;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        r_id = findViewById(R.id.r_id);
        r_pass = findViewById(R.id.r_pass);
        r_name = findViewById(R.id.r_name);

        btn_register = findViewById(R.id.r_btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url = "http://"+getString(R.string.ipport)+"/user/signup/";
                String method = "POST";

                Map<String, String> params = new HashMap<String, String>();

                params.put("username", r_id.getText().toString());
                params.put("email", "");
                params.put("password", r_pass.getText().toString());
                JSONObject parameters = new JSONObject(params);

                //NetworkTaskAsync 호출로 login 요청하기
                NetworkTask networkTask = new NetworkTask(url, null, method ,parameters, RegisterActivity.this);
                networkTask.execute();

            }
        });
    }

    @Override
    public void method2(String s) {

        System.out.println("now response : " + s);
        JsonParser jsonParser = new JsonParser();
        JsonObject collections = (JsonObject) jsonParser.parse(s);

        String register_success = collections.get("message").getAsString();


        if(register_success.equals("SUCCESS"))
        {
            Toast.makeText(getApplicationContext(),"회원가입에 성공하였습니다.",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else { // 로그인에 실패한 경우
            Toast.makeText(getApplicationContext(),"회원가입에 실패하였습니다.",Toast.LENGTH_SHORT).show();
        }
    }
}
