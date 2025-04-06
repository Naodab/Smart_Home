package com.smarthome.mobile.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> loginStatus;

    public AuthViewModel(Application application) {
        super(application);
        this.authRepository = new AuthRepository();
        loginStatus = this.authRepository.getLoginStatus();
    }

    public MutableLiveData<Boolean> getLoginStatus() {
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

    public void refreshToken() {
        authRepository.refresh();
    }
}
