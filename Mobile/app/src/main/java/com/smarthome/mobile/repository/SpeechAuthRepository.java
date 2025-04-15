package com.smarthome.mobile.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.dto.response.AuthResponse;
import com.smarthome.mobile.network.ApiService;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeechAuthRepository {
    private final ApiService apiService;
    private final MutableLiveData<Result<AuthResponse>> authStatus;

    public SpeechAuthRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.authStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Result<AuthResponse>> getAuthStatus() {
        return authStatus;
    }

    public void uploadAudio(byte[] audioData) {
        try {
            authStatus.postValue(Result.loading());
            File templateFile = File.createTempFile("audio_1", ".wav");
            FileOutputStream fos = new FileOutputStream(templateFile);
            AudioRecorderHelper.writeWavHeader(fos, audioData.length);
            fos.write(audioData);
            fos.close();

            RequestBody requestFile = RequestBody
                    .create(templateFile, MediaType.parse("application/octet-stream"));
            MultipartBody.Part body = MultipartBody.Part
                    .createFormData("file", "audio.wav", requestFile);

            Map<String, RequestBody> metadata = new HashMap<>();
            String email = MyApp.getInstance().getSessionManager().fetchUserEmail();
            metadata.put("email", RequestBody
                    .create(email, MediaType.parse("text/plain")));

            apiService.authenticateSpeeches(body, metadata).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(@NonNull Call<AuthResponse> call,
                                       @NonNull Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MyApp.getInstance().getSessionManager().savePerson(response.body());
                        authStatus.postValue(Result.success(response.body()));
                    } else {
                        authStatus.postValue(Result
                                .error(Objects.requireNonNull(response.errorBody()).toString()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AuthResponse> call,
                                      @NonNull Throwable t) {
                    authStatus.postValue(Result.error(t.getMessage()));
                }
            });
        } catch (Exception e) {
            authStatus.postValue(Result.error(e.getMessage()));
        }
    }
}
