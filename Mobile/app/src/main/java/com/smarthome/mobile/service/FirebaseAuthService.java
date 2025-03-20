package com.smarthome.mobile.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthService {
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public interface AuthCallBack {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }

    public void login(String email, String password, AuthCallBack callBack) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    callBack.onSuccess(user);
                } else {
                    callBack.onFailure(task.getException());
                }
            });
    }

    public void logout() {
        this.firebaseAuth.signOut();
    }
}
