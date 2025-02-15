package com.smarthome.mobile.viewmodel;

import com.smarthome.mobile.repository.SpeechAuthRepository;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.SpeechAuthCallBack;

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
}
