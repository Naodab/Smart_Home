package com.smarthome.mobile.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.dto.response.AuthResponse;
import com.smarthome.mobile.network.ApiService;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.util.FaceAuthCallback;
import com.smarthome.mobile.util.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceAuthRepository {
    private final ApiService apiService;
    private final MutableLiveData<Result<AuthResponse>> authenticateStatus;

    public FaceAuthRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.authenticateStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Result<AuthResponse>> getAuthenticateStatus() {
        return authenticateStatus;
    }

    public void authenticate(byte[] imageData) {
        try {
            authenticateStatus.postValue(Result.loading());
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
            File templateFile = File.createTempFile(timestamp, ".jpg");
            FileOutputStream fos = new FileOutputStream(templateFile);
            fos.write(imageData);
            fos.close();

            RequestBody requestFile = RequestBody
                    .create(templateFile, MediaType.parse("image/jpg"));
            MultipartBody.Part body = MultipartBody.Part
                    .createFormData("file", templateFile.getName(), requestFile);

            Map<String, RequestBody> metadata = new HashMap<>();
            String email = MyApp.getInstance().getSessionManager().fetchUserEmail();
            metadata.put("email", RequestBody
                    .create(email, MediaType.parse("text/plain")));

            apiService.authenticateFaces(body, metadata).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(@NonNull Call<AuthResponse> call,
                                       @NonNull Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MyApp.getInstance().getSessionManager().savePerson(response.body());
                        authenticateStatus.postValue(Result.success(response.body()));
                    } else {
                        authenticateStatus.postValue(Result
                                .error(Objects.requireNonNull(response.errorBody()).toString()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AuthResponse> call,
                                      @NonNull Throwable t) {
                    authenticateStatus.postValue(Result.error(t.getMessage()));
                }
            });
        } catch (Exception e) {
            authenticateStatus.postValue(Result.error(e.getMessage()));
        }
    }

    public void uploadImage(byte[] imageData, FaceAuthCallback callback) {
        new Thread(() -> {
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
                File templateFile = File.createTempFile(timestamp, ".jpg");
                FileOutputStream fos = new FileOutputStream(templateFile);
                fos.write(imageData);
                fos.close();

                RequestBody requestFile = RequestBody.create(templateFile, MediaType.parse("image/jpg"));
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", templateFile.getName(), requestFile);

                apiService.authenticateFaces(body).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // TODO: check if authenticate or not
                        Log.d("Face Auth Repo", "Upload image success");
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Face Auth Repo", "Fail when upload image");
                        callback.onFailure();
                    }
                });
            } catch (Exception e) {
                Log.e("Face Auth Repo", Objects.requireNonNull(e.getMessage()));
                callback.onFailure();
            }

        }).start();
    }
}
