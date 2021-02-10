package com.example.workspace1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText r_email, r_pass, r_name, r_age;
    private Button btn_register;
    private String username, password;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        r_email = findViewById(R.id.r_email);
        r_pass = findViewById(R.id.r_pass);
        r_name = findViewById(R.id.r_name);
        r_age = findViewById(R.id.r_age);

        btn_register = findViewById(R.id.r_btn_register);

        queue = Volley.newRequestQueue(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String userPass = et_pass.getText().toString();
//                String email = et_email.getText().toString();
//                String userName = et_name.getText().toString();
//                int userAge = Integer.parseInt(et_age.getText().toString());

                String url = "http://192.168.75.198:8000/user/signin/";

                Map<String, String> params = new HashMap<String, String>();
                Log.d("유저네임", "들어갑니다.");
                params.put("username", r_name.getText().toString());
                params.put("email", r_email.getText().toString());
                params.put("password", r_pass.getText().toString());
                JSONObject parameters = new JSONObject(params);

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                        if(response.getString("message").equals("SUCCESS"))

                            Log.d("Debug", response.getString("message"));
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);

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
