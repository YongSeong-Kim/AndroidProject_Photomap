package com.example.workspace1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;

public class PhotoRegister extends Activity {
    private static final int REQUEST_CODE = 0;
    private ImageView imageView;
    String imgPath;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_photo_register);


        Log.d("이거 에러1","이거 에러1");
        imageView = findViewById(R.id.temp_photo);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    imageView.setImageURI(uri);

                    //갤러리앱에서 관리하는 DB정보가 있는데, 그것이 나온다 [실제 파일 경로가 아님!!]
                    //얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
                    //업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
                    //Uri -->절대경로(String)로 변환
                    imgPath = getRealPathFromUri(uri);
                    Toast.makeText(this, imgPath, Toast.LENGTH_LONG).show();
//                    try{
//                    InputStream in = getContentResolver().openInputStream(data.getData());
//
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    in.close();
//
//                    imageView.setImageBitmap(img);
//                }catch(Exception e)
//                {
//
//                }
                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getRealPathFromUri(Uri uri){ // 절대경로
            String[] proj= {MediaStore.Images.Media.DATA};
            CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
            Cursor cursor= loader.loadInBackground();
            int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result= cursor.getString(column_index);
            cursor.close();
            return  result;
    }

    public void mOnClose(View v)
    {
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        if(imageView.getDrawable() == null)
        {
            Toast.makeText(this, "등록하실 이미지가 없습니다.", Toast.LENGTH_LONG).show();

        }
        else
        {

//            HttpURLConnection httpUrl = null;
//            URL url = null;
//            try {
//                url = new URL("http://35.238.98.83:8000/photo/post/");
//                httpUrl = (HttpURLConnection) url.openConnection();
//                httpUrl.setUseCaches(false);
//                httpUrl.setDoOutput(true);
//
//                httpUrl.setRequestMethod("POST");
//                httpUrl.setRequestProperty("Connection", "Keep-Alive");
//                httpUrl.setRequestProperty("Cache-Control", "no-cache");
//                httpUrl.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//
//                DataOutputStream request = new DataOutputStream(httpUrl.getOutputStream());
//
//                request.writeBytes
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            queue = Volley.newRequestQueue(this);
//            EditText temp_title,temp_content;
//            temp_title = findViewById(R.id.temp_title);
//            temp_content = findViewById(R.id.temp_content);
//
//            String title= temp_title.getText().toString();
//            String content= temp_content.getText().toString();
//
//            //안드로이드에서 보낼 데이터를 받을 php 서버 주소
//            String url="http://35.238.98.83:8000/photo/post/";
//
//            Intent tempIntent = getIntent();
//            double lat = tempIntent.getDoubleExtra("latitude",0);
//            double longti = tempIntent.getDoubleExtra("longtitude",0);
//
//            Toast.makeText(PhotoRegister.this, lat +"\n"+ longti, Toast.LENGTH_SHORT).show();
//
//            Map<String, String> params = new HashMap<String, String>();
//            Log.d("유저네임", "들어갑니다.");
//
//            params.put("image",imgPath);
//
//            params.put("latitude", Double.toString(lat));
//            params.put("longtitude", Double.toString(longti));
//
//            JSONObject parameters = new JSONObject(params);
//
//            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//
//                    try {
//                        if(response.getString("message").equals("SUCCESS"))
//
//                            Toast.makeText(PhotoRegister.this, "응답", Toast.LENGTH_LONG).show();
//
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//
//            queue.add(jsonRequest);

//            SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    new AlertDialog.Builder(PhotoRegister.this).setMessage("응답:"+response).create().show();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(PhotoRegister.this, "ERROR", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            //요청 객체에 보낼 데이터를 추가
////            smpr.addStringParam("title", title);
////            smpr.addStringParam("content", content);
//
//            Intent tempIntent = getIntent();
//            int lat = tempIntent.getIntExtra("latitude",0);
//            int longti = tempIntent.getIntExtra("longtitude",0);
//            Toast.makeText(PhotoRegister.this, lat + longti, Toast.LENGTH_SHORT).show();
//
//            smpr.addStringParam("latitude", lat);
//            smpr.addStringParam("longtitude", longti);
//            //이미지 파일 추가
//            smpr.addFile("img", imgPath);
//
//            //요청객체를 서버로 보낼 우체통 같은 객체 생성
//            RequestQueue requestQueue= Volley.newRequestQueue(this);
//            requestQueue.add(smpr);

            Toast.makeText(this, "안녕", Toast.LENGTH_LONG).show();

        }
        finish();
    }

}