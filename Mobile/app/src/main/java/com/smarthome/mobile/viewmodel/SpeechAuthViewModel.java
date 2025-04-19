package com.smarthome.mobile.viewmodel;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.dto.response.AuthResponse;
import com.smarthome.mobile.repository.SpeechAuthRepository;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.Result;

import java.io.IOException;
import java.io.OutputStream;

public class SpeechAuthViewModel extends AndroidViewModel {
    private final SpeechAuthRepository speechAuthRepository;
    private AudioRecorderHelper audioRecorderHelper;

    public SpeechAuthViewModel(@NonNull Application application) {
        super(application);
        speechAuthRepository = new SpeechAuthRepository();
    }

    public void setAudioRecorderHelper(AudioRecorderHelper audioRecorderHelper) {
        this.audioRecorderHelper = audioRecorderHelper;
    }

    public MutableLiveData<Result<AuthResponse>> getAuthStatus() {
        return speechAuthRepository.getAuthStatus();
    }

    public void startRecording() throws Exception {
        if (audioRecorderHelper == null) {
            throw new Exception(
                    "AudioRecorderHelper is not initialized. Please call setAudioRecorderHelper() first."
            );
        }
        audioRecorderHelper.startRecording();
    }

    public void stopRecording() {
        audioRecorderHelper.stopRecording();
    }

    public void onAudioDataCaptured(byte[] audioData) {
        speechAuthRepository.uploadAudio(audioData);
    }

    public void uploadAudio(byte[] audioData) {
        speechAuthRepository.uploadAudio(audioData);
    }

    public void saveAudioExternalStorage(Context context, byte[] audioData) {
        String fileName = "audio_" + System.currentTimeMillis() + ".wav";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);

        Uri audioUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(audioUri)) {
            assert outputStream != null;
            AudioRecorderHelper.writeWavHeader(outputStream, audioData.length);
            outputStream.write(audioData);
            Log.d("Save Audio", "File saved at: " + audioUri.toString());
        } catch (IOException e) {
            Log.e("Save Audio", "Error saving file", e);
        }
    }
}
