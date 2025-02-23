package com.smarthome.mobile.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.model.UserAuthentication;
import com.smarthome.mobile.repository.AuthRepository;

public class HomeViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<UserAuthentication> userLiveData;
    private static HomeViewModel _instance;

    private HomeViewModel() {
        this.authRepository = AuthRepository.getInstance();
        userLiveData = this.authRepository.getUserLiveData();
    }

    public static HomeViewModel getInstance() {
        if (_instance == null)
            _instance = new HomeViewModel();
        return _instance;
    }

    public MutableLiveData<UserAuthentication> getUserLiveData() {
        return this.userLiveData;
    }

    public void logout() {
        authRepository.logout();
    }
}
