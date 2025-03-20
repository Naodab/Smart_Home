package com.smarthome.mobile.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.model.UserAuthentication;
import com.smarthome.mobile.repository.AuthRepository;

public class LoginViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<UserAuthentication> userLiveData;
    private static LoginViewModel _instance;

    private LoginViewModel() {
        this.authRepository = AuthRepository.getInstance();
        userLiveData = this.authRepository.getUserLiveData();
    }

    public static LoginViewModel getInstance() {
        if (_instance == null)
            _instance = new LoginViewModel();
        return _instance;
    }

    public MutableLiveData<UserAuthentication> getUserLiveData() {
        return this.userLiveData;
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }
}
