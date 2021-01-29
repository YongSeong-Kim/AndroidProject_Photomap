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
import com.android.volley.toolbox.Volley;

import java.io.InputStream;

public class PhotoRegister extends Activity {
    private static final int REQUEST_CODE = 0;
    private ImageView imageView;
    String imgPath;
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
        if(imageView.getDrawable() == null)
        {
            Toast.makeText(this, "등록하실 이미지가 없습니다.", Toast.LENGTH_LONG).show();

        }
        else
        {

            EditText temp_title,temp_content;
            temp_title = findViewById(R.id.temp_title);
            temp_content = findViewById(R.id.temp_content);

            String title= temp_title.getText().toString();
            String content= temp_content.getText().toString();

            //안드로이드에서 보낼 데이터를 받을 php 서버 주소
            String serverUrl="http://35.238.98.83:8000/photo/post";

            //Volley plus Library를 이용해서
            //파일 전송하도록..
            //Volley+는 AndroidStudio에서 검색이 안됨 [google 검색 이용]

            //파일 전송 요청 객체 생성[결과를 String으로 받음]
            SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    new AlertDialog.Builder(PhotoRegister.this).setMessage("응답:"+response).create().show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PhotoRegister.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            });

            //요청 객체에 보낼 데이터를 추가
//            smpr.addStringParam("title", title);
//            smpr.addStringParam("content", content);

            Intent tempIntent = getIntent();
            int lat = tempIntent.getIntExtra("latitude",0);
            int longti = tempIntent.getIntExtra("longtitude",0);
            Toast.makeText(PhotoRegister.this, lat + longti, Toast.LENGTH_SHORT).show();

            smpr.addStringParam("latitude", lat);
            smpr.addStringParam("longtitude", longti);
            //이미지 파일 추가
            smpr.addFile("img", imgPath);

            //요청객체를 서버로 보낼 우체통 같은 객체 생성
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(smpr);

            Toast.makeText(this, "2", Toast.LENGTH_LONG).show();

        }
        finish();
    }

}