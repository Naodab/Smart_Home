package com.smarthome.mobile.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.dto.response.AuthResponse;
import com.smarthome.mobile.repository.FaceAuthRepository;
import com.smarthome.mobile.util.FaceAuthCallback;
import com.smarthome.mobile.util.Result;

public class FaceAuthViewModel extends AndroidViewModel {
    private final FaceAuthRepository faceAuthRepository;

    public FaceAuthViewModel(Application application) {
        super(application);
        this.faceAuthRepository = new FaceAuthRepository();
    }

    public void uploadImageToServer(byte[] imageBytes, FaceAuthCallback faceAuthCallback) {
        faceAuthRepository.uploadImage(imageBytes, faceAuthCallback);
    }

    public MutableLiveData<Result<AuthResponse>> getAuthenticateStatus() {
        return faceAuthRepository.getAuthenticateStatus();
    }

    public void authenticateFace(byte[] imageBytes) {
        faceAuthRepository.authenticate(imageBytes);
    }

    public void resetState() {
        faceAuthRepository.getAuthenticateStatus().setValue(null); // Đặt lại LiveData nếu cần
    }
}
