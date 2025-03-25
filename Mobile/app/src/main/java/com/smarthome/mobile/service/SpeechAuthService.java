package com.smarthome.mobile.service;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface SpeechAuthService {
    @Multipart
    @POST("api/speeches/upload/")
    Call<Void> uploadAudio(
        @Part MultipartBody.Part file,
        @PartMap Map<String, RequestBody> metadata
    );
}
