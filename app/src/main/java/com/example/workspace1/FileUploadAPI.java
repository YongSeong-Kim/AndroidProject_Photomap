package com.example.workspace1;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface FileUploadAPI {

    final String Base_URL = "http://10.0.2.2:8000";
    @Multipart
    @POST("/photo/post/")
    Call<ResponseBody> createPost(@Part MultipartBody.Part image,
                                  @Part("latitude") RequestBody latitude,
                                  @Part("longtitude") RequestBody longtitude);
}
