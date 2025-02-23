package com.smarthome.mobile.viewmodel;

import com.smarthome.mobile.repository.FaceAuthRepository;
import com.smarthome.mobile.util.FaceAuthCallback;

public class FaceAuthViewModel {
    private final FaceAuthRepository faceAuthRepository;
    private static FaceAuthViewModel _instance;

    private FaceAuthViewModel() {
        this.faceAuthRepository = new FaceAuthRepository();
    }

    public static FaceAuthViewModel getInstance() {
        if (_instance == null)
            _instance = new FaceAuthViewModel();
        return _instance;
    }

    public void uploadImageToServer(byte[] imageBytes, FaceAuthCallback faceAuthCallback) {
        faceAuthRepository.uploadImage(imageBytes, faceAuthCallback);
    }
}
