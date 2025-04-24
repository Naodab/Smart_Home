package com.smarthome.mobile.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.repository.SpeechRemoteRepository;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.Result;

public class SpeechRemoteViewModel extends AndroidViewModel {
    private final SpeechRemoteRepository speechRemoteRepository;
    private AudioRecorderHelper audioRecorderHelper;

    public SpeechRemoteViewModel(Application application) {
        super(application);
        this.speechRemoteRepository = new SpeechRemoteRepository();
    }

    public void setAudioRecorderHelper(AudioRecorderHelper audioRecorderHelper) {
        this.audioRecorderHelper = audioRecorderHelper;
    }

    public void startRecording() throws Exception {
        if (audioRecorderHelper == null) {
            throw new Exception(
                    "AudioRecorderHelper is not initialized. " +
                            "Please call setAudioRecorderHelper() first."
            );
        }
        audioRecorderHelper.startRecording();
    }

    public void changeBySpeech(byte[] data) {
        speechRemoteRepository.changeBySpeech(data);
    }

    public void stopRecording() {
        audioRecorderHelper.stopRecording();
    }

    public MutableLiveData<Result<Device>> getSpeechRemoteStatus() {
        return speechRemoteRepository.getSpeechRemoteStatus();
    }
}
