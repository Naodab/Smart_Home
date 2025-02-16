package com.smarthome.mobile.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.smarthome.mobile.model.UserAuthentication;
import com.smarthome.mobile.service.FirebaseAuthService;

public class AuthRepository {
    private final FirebaseAuthService firebaseAuthService;
    private final MutableLiveData<UserAuthentication> userLiveData;

    private AuthRepository() {
        this.firebaseAuthService = new FirebaseAuthService();
        this.userLiveData = new MutableLiveData<>();
    }

    private static AuthRepository _instance;

    public static AuthRepository getInstance() {
        if (_instance == null)
            _instance = new AuthRepository();
        return _instance;
    }

    public MutableLiveData<UserAuthentication> getUserLiveData() {
        return this.userLiveData;
    }

    public void login(String email, String password) {
        firebaseAuthService.login(email, password, new FirebaseAuthService.AuthCallBack() {
            @Override
            public void onSuccess(FirebaseUser user) {
                UserAuthentication u = new UserAuthentication(user.getUid(), user.getEmail());
                userLiveData.postValue(u);
            }

            @Override
            public void onFailure(Exception e) {
                userLiveData.postValue(null);
            }
        });
    }

    public void logout() {
        firebaseAuthService.logout();
        userLiveData.postValue(null);
    }
}
