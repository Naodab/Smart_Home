package com.smarthome.mobile.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.repository.AuthRepository;

public class AuthViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> loginStatus;

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
        loginStatus = this.authRepository.getLoginStatus();
    }

    public MutableLiveData<Boolean> getUserLiveData() {
        return this.loginStatus;
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void logout() {
        this.authRepository.logout();
    }

    public void changePassword(String oldPassword, String newPassword) {
        this.authRepository.changePassword(oldPassword, newPassword);
    }
}
