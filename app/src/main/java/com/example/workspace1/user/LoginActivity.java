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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText l_id, l_pass;
    private Button btn_login, btn_register;
    private String username, password;
    private RequestQueue queue;

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

//                String userPass = et_pass.getText().toString();
//                String email = et_email.getText().toString();
//                String userName = et_name.getText().toString();


                String url = "http://192.168.75.198:8000/user/login/";


                Map<String, String> params = new HashMap<String, String>();
                Log.d("유저네임", "들어갑니다.");
                params.put("username", l_id.getText().toString());
                Log.d("test", l_id.getText().toString());
                params.put("email", "");
                params.put("password", l_pass.getText().toString());
                Log.d("test", l_pass.getText().toString());
                JSONObject parameters = new JSONObject(params);

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("뭐야","뭐야");
                        try {
                            if(response.getString("message").equals("SUCCESS"))
                            {
                                Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else { // 로그인에 실패한 경우
                                Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(jsonRequest);

            }
        });
    }
}