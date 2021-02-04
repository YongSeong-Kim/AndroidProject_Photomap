package com.example.workspace1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.loader.content.CursorLoader;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoRegister extends Activity {
    private static final int REQUEST_CODE = 0;
    private ImageView imageView;
    String imgPath;
    private FileUploadAPI FileUploadApi;
    private String base_url = "http://10.0.2.2:8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_photo_register);



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
        if(imageView.getDrawable() == null)
        {
            Toast.makeText(this, "등록하실 이미지가 없습니다.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d("1","s");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            FileUploadApi = retrofit.create(FileUploadAPI.class);
            createPost();
            Toast.makeText(this, "안녕", Toast.LENGTH_LONG).show();
        }


        finish();
    }

    private void createPost() {

//        File filesDir = getApplicationContext().getFilesDir();
//        File file = new File(filesDir, "image" + ".png");
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
//        byte[] bitmapdata = bos.toByteArray();
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(bitmapdata);
//        fos.flush();
//        fos.close();
        Log.d("2","s");
        File file = new File(imgPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("image", imgPath, requestFile);

        RequestBody lati = RequestBody.create(MediaType.parse("text/plain"), "u101");

        RequestBody longti = RequestBody.create(MediaType.parse("text/plain"), "u101");

        final Call<ResponseBody> upload = FileUploadApi.createPost(uploadFile, longti, lati);

        upload.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.d("3","s");
            if (!response.isSuccessful()) {
                Toast.makeText(PhotoRegister.this,  "code " + response.code(), Toast.LENGTH_LONG).show();
                return;
            }
            String content = "";
            content += "Code : " + response.code() + "\n";
            Toast.makeText(PhotoRegister.this,  content, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(PhotoRegister.this,  "Wrong", Toast.LENGTH_LONG).show();
        }

        });
        Log.d("4","s");
    }

}