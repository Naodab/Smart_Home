package com.smarthome.mobile.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.model.Home;
import com.smarthome.mobile.repository.AuthRepository;

public class HomeViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Home> userLiveData;
    private static HomeViewModel _instance;

    private HomeViewModel() {
        this.authRepository = AuthRepository.getInstance();
        userLiveData = this.authRepository.getLoginStatus();
    }

    public static HomeViewModel getInstance() {
        if (_instance == null)
            _instance = new HomeViewModel();
        return _instance;
    }

    public MutableLiveData<Home> getUserLiveData() {
        return this.userLiveData;
    }

    public void logout() {
        authRepository.logout();
    }
}
