package com.smarthome.mobile.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.network.ApiService;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeechRemoteRepository {
    private final ApiService apiService;
    private final MutableLiveData<Result<Device>> speechRemoteStatus;

    public SpeechRemoteRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        speechRemoteStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Result<Device>> getSpeechRemoteStatus() {
        return speechRemoteStatus;
    }

    public void changeBySpeech(byte[] data) {
        speechRemoteStatus.postValue(Result.loading());
        try {
            File templateFile = File.createTempFile("audio_1", ".wav");
            FileOutputStream fos = new FileOutputStream(templateFile);
            AudioRecorderHelper.writeWavHeader(fos, data.length);
            fos.write(data);
            fos.close();

            RequestBody requestFile = RequestBody.create(templateFile,
                    MediaType.parse("application/octet-stream"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file",
                    "audio.wav", requestFile);

            Map<String, RequestBody> metadata = new HashMap<>();
            String email = MyApp.getInstance().getSessionManager().fetchUserEmail();
            int personId = MyApp.getInstance().getSessionManager().fetchPersonID();
            metadata.put("email", RequestBody.create(email,
                    MediaType.parse("text/plain")));
            metadata.put("personId", RequestBody.create(String.valueOf(personId),
                    MediaType.parse("text/plain")));

            apiService.remoteBySpeech(body, metadata).enqueue(new Callback<Device>() {
                @Override
                public void onResponse(@NonNull Call<Device> call,
                                       @NonNull Response<Device> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        speechRemoteStatus.postValue(Result.success(response.body()));
                    } else {
                        speechRemoteStatus.postValue(Result.error("Something went wrong"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Device> call,
                                      @NonNull Throwable t) {
                    speechRemoteStatus.postValue(Result.error("Something went wrong"));
                }
            });
        } catch (Exception e) {
            speechRemoteStatus.postValue(Result.error(e.getMessage()));
        }
    }
}
