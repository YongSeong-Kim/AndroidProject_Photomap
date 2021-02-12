package com.example.workspace1.utility;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

///////////////////////////////////////////////////////////////////////////////////
/* Class for Network */
///////////////////////////////////////////////////////////////////////////////////
public class NetworkTask extends AsyncTask<Void, Void, String> {

    private String url;                     //api url
    private ContentValues values;           //보통은 null로 input받음
    private String method;                  //method 종류: GET, POST 등
    private JSONObject jsonObject;          //POST로 준다면, jsonobject 포함시키기
    private AsyncTaskCallback mCallback;    //callback 함수


    public NetworkTask(String url, ContentValues values, String method , JSONObject jsonObject, AsyncTaskCallback mCallback) {

        this.url = url;
        this.values = values;
        this.method = method;
        this.jsonObject = jsonObject;
        this.mCallback= mCallback;


    }

    /*
     * NetworkTask가 execute가 되면 doInBackground 함수가 먼저 실행이 됨.
     * 이 함수가 실행이 되고 나서 noPostExecute라는 함수가 후의 실행이됨.
     */
    @Override
    protected String doInBackground(Void... params) {

        String result; // 요청 결과를 저장할 변수.
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        if(jsonObject == null){         //jsonObject가 없다는 건 GET method라는 뜻임.
            result = requestHttpURLConnection.request_get(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }
        else if(method.equals("POST")){      //POST method.
            System.out.println("POST 들어갑니다");
            result = requestHttpURLConnection.request_post(url, values, jsonObject); // 해당 URL로 POST 보내기.
            return result;
        }
        else{                           //jsonObject가 있는데 POST가 아니면 PUT method 라는 뜻.
            result = requestHttpURLConnection.request_put(url, values, jsonObject); // 해당 URL로 POST 보내기.
            return result;
        }
    }

    /*
     * doInBackground의 return값인 result가 onPostExecute의 parameter인 String s에 들어감.
     * 즉 간략하게, String s = doInBackgroud()라고 보면 됨.
     * 그리고 mCallback.method() 라는 함수로 network.execute() 했던 java 파일로 돌아가게 되고
     * 해당 파일에서 http response 에 따른 추가 동작을 제어해주면 됨.
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //System.out.println(s);
        if(jsonObject == null)          //GET method로 호출하면 method1이 실행
            mCallback.method1(s);
        else if(method == "POST")       //POST method로 호출하면 method2가 실행
            mCallback.method2(s);
        else                            //PUT method로 호출하면 method3이 실행
            mCallback.method3(s);
    }
}
/////////////////////////////////////////////////////////////////////////////////////