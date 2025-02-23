package com.smarthome.mobile.viewmodel;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.smarthome.mobile.repository.SpeechAuthRepository;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.SpeechAuthCallBack;

import java.io.IOException;
import java.io.OutputStream;

public class SpeechAuthViewModel {
    private final SpeechAuthRepository speechAuthRepository;
    private final AudioRecorderHelper audioRecorderHelper;
    private final SpeechAuthCallBack callBack;

    public SpeechAuthViewModel(SpeechAuthCallBack callBack) {
        speechAuthRepository = new SpeechAuthRepository();
        audioRecorderHelper = new AudioRecorderHelper(this::onAudioDataCaptured);
        this.callBack = callBack;
    }

    public void startRecording() {
        audioRecorderHelper.startRecording();
    }

    public void stopRecording() {
        audioRecorderHelper.stopRecording();
    }

    public void onAudioDataCaptured(byte[] audioData) {
        speechAuthRepository.uploadAudio(audioData, callBack);
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
