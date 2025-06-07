package com.smarthome.mobile.network;

import com.smarthome.mobile.dto.request.ChangeDeviceRequest;
import com.smarthome.mobile.dto.request.ChangePasswordRequest;
import com.smarthome.mobile.dto.request.LoginRequest;
import com.smarthome.mobile.dto.request.RefreshRequest;
import com.smarthome.mobile.dto.response.AuthResponse;
import com.smarthome.mobile.dto.response.ChangeDeviceResponse;
import com.smarthome.mobile.dto.response.LoginResponse;
import com.smarthome.mobile.dto.response.TokenResponse;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.model.Home;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/users/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/logout/")
    Call<Void> logout();

    @POST("api/change-password/")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    @POST("api/refresh/")
    Call<TokenResponse> refreshToken(@Body RefreshRequest request);

    @GET("api/users/homes/{email}/")
    Call<Home> getHome(@Path("email") String email);

    @PUT("api/users/devices/{deviceId}/")
    Call<ChangeDeviceResponse> changeStatusDevice(@Path("deviceId") int deviceId, @Body ChangeDeviceRequest request);

    @Multipart
    @POST("api/speeches/upload/")
    Call<AuthResponse> authenticateSpeeches(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> metadata
    );

    @Multipart
    @POST("api/speeches/remote/")
    Call<Device> remoteBySpeech(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> metadata
    );


    @Multipart
    @POST("face_detection/detect/")
    Call<AuthResponse> authenticateFaces(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> metadata
    );

    @Multipart
    @POST("face_detection/detect/")
    Call<ResponseBody> authenticateFaces(
            @Part MultipartBody.Part image
    );
}
