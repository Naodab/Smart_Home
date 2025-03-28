package com.smarthome.mobile.repository;

import android.util.Log;

import com.smarthome.mobile.api.SpeechApiClient;
import com.smarthome.mobile.service.SpeechAuthService;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.SpeechAuthCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private final SpeechAuthService speechAuthService;
    private String email;

    public SpeechAuthRepository() {
        this.speechAuthService = SpeechApiClient.getClient().create(SpeechAuthService.class);
    }

    public void uploadAudio(byte[] audioData, SpeechAuthCallBack callBack) {
        new Thread(() -> {
            try {
                File templateFile = File.createTempFile("audio_1", ".wav");
                FileOutputStream fos = new FileOutputStream(templateFile);
                AudioRecorderHelper.writeWavHeader(fos, audioData.length);
                fos.write(audioData);
                fos.close();
                
                RequestBody requestFile = RequestBody.create(templateFile, MediaType.parse("application/octet-stream"));
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", "audio.wav", requestFile);

                Map<String, RequestBody> metadata = new HashMap<>();
                email = "hoang";
                metadata.put("email", RequestBody
                        .create(email, MediaType.parse("text/plain")));

                speechAuthService.uploadAudio(body, metadata).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("Upload audio", "Success");
                            callBack.onSuccess();
                        } else {
                            Log.d("Upload audio", "Failure");
                            callBack.onFailure();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("Upload audio", "Failure");
                        Log.d("Upload audio", Objects.requireNonNull(t.getMessage()));
                        callBack.onFailure();
                    }
                });
            } catch (Exception e) {
                Log.d("Speech Auth Repository", Objects.requireNonNull(e.getMessage()));
                callBack.onFailure();
            }
        }).start();
    }
}
