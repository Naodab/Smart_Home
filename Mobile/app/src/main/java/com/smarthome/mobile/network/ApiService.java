package com.smarthome.mobile.network;

import com.smarthome.mobile.dto.request.ChangePasswordRequest;
import com.smarthome.mobile.dto.request.LoginRequest;
import com.smarthome.mobile.dto.request.RefreshRequest;
import com.smarthome.mobile.dto.response.LoginResponse;
import com.smarthome.mobile.dto.response.TokenResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiService {
    @POST("api/users/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/users/logout/")
    Call<Void> logout();

    @POST("api/changePassword/")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    @POST("api/refresh/")
    Call<TokenResponse> refreshToken(@Body RefreshRequest request);

    @Multipart
    @POST("api/speeches/upload/")
    Call<Void> authenticateSpeeches(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> metadata
    );

    @Multipart
    @POST("face_detection/detect/")
    Call<ResponseBody> authenticateFaces(
            @Part MultipartBody.Part image
    );
}
