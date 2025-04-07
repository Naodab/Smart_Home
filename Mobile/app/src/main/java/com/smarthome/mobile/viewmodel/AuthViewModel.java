package com.smarthome.mobile.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.repository.AuthRepository;
import com.smarthome.mobile.util.Result;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    public AuthViewModel(Application application) {
        super(application);
        this.authRepository = new AuthRepository();
    }

    public MutableLiveData<Result<Boolean>> getLoginStatus() {
        return this.authRepository.getLoginStatus();
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
