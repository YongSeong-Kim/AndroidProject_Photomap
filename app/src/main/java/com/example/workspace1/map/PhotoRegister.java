package com.example.workspace1.map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.loader.content.CursorLoader;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.workspace1.R;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.StringRequest;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
    private File tempFile;
    String imgPath;
    private RequestQueue queue;
    HttpURLConnection con = null;
    String boundary = "******";
    String crlf = "\r\n";
    String twoHyphens = "--";
    OutputStream httpConnOutputStream;
    Uri uri;
    Intent temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_photo_register);

        imageView = findViewById(R.id.temp_photo);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
//                startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT); // ACTION_PICK은 사용하지 말것, deprecated + formally
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//사진 잘가져오는지.
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK) {
                System.out.println("getdata:"+ data.getData().toString());
                imgPath = getRealPathFromUri(data.getData());

                File file = new File(imgPath);
                temp = data;
                Bitmap mBitmap = null;
                if (imgPath != null) {
                    //Log.v("SDK Version:","sdk_int " +Build.VERSION.SDK_INT);

                    if (Build.VERSION.SDK_INT >= 29){
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),Uri.fromFile(file));
                        try {
                            mBitmap = ImageDecoder.decodeBitmap(source);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        mBitmap = BitmapFactory.decodeFile(imgPath);
                        //mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    }

                    imageView.setImageBitmap(mBitmap);
                }
//                //uri = data.getData();
//                if (uri != null) {
//                    imageView.setImageURI(uri);
//                    //Uri -->절대경로(String)로 변환
//                    imgPath = getRealPathFromUri(uri);
//                    System.out.println("imgPath :" + imgPath);
//                    Toast.makeText(this, imgPath, Toast.LENGTH_LONG).show();
//                }
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
            tempFile = new File(cursor.getString(column_index));
            cursor.close();
            return  result;
    }


    public void mOnClose(View v) throws IOException {

        if(imageView.getDrawable() == null)
        {
            Toast.makeText(this, "등록하실 이미지가 없습니다.", Toast.LENGTH_LONG).show();
        }
        else
        {
            new Thread()
            {
                public void run()
                {
                    try {
                        upload();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }

    }

    public void upload() throws IOException {

        Intent FromMainIntent = getIntent();
        Double latitude = FromMainIntent.getDoubleExtra("latitude",0);
        Double longtitude = FromMainIntent.getDoubleExtra("longtitude",0);

        URL url = new URL("http://"+getString(R.string.ipport)+"/photo/post/");

        con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        con.setRequestMethod("POST");
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"latitude\"" + crlf+crlf);

        wr.writeBytes(latitude + crlf);


        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"longtitude\"" + crlf+crlf);

        wr.writeBytes(longtitude + crlf);


        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"title\"" + crlf+crlf);

        wr.writeBytes("test_title._123" + crlf);


        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"content\"" + crlf+crlf);

        wr.writeBytes("test_content" + crlf);

        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"author\"" + crlf+crlf);

        wr.writeBytes("test_author" + crlf);

//        Uri myUri = Uri.parse(tempFile.getAbsolutePath());
//        InputStream in = getContentResolver().openInputStream(myUri);
        InputStream in = getContentResolver().openInputStream(temp.getData());

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

//        System.out.println("사진의 절대위치 " + tempFile.getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeStream(in);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        wr.writeBytes(twoHyphens + boundary + crlf);
        wr.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imgPath + crlf);
        wr.writeBytes("Content-Type:image/png" + crlf);
        wr.writeBytes(crlf);
        wr.write(bytes);

        wr.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        wr.flush();
        wr.close();


        int status = con.getResponseCode();

        InputStream responseStream = new BufferedInputStream(con.getInputStream());
        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while((line = responseStreamReader.readLine()) !=null)
        {
            stringBuilder.append(line).append("\n");
        }
        responseStreamReader.close();
        String s = stringBuilder.toString();

        Log.d("상태코드", Integer.toString(status));

        Intent intent = new Intent();
        if(status == 200)
        {
            intent.putExtra("latitude",latitude);
            intent.putExtra("longtitude",longtitude);
            this.setResult(1,intent);

        }
        else
        {
            this.setResult(2,intent);
        }
        finish();
    }

}