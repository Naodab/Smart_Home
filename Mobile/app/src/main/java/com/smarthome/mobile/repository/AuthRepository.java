package com.smarthome.mobile.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.dto.request.LoginRequest;
import com.smarthome.mobile.dto.response.LoginResponse;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final MutableLiveData<Boolean> loginStatus;

    public AuthRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.loginStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getLoginStatus() {
        return this.loginStatus;
    }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<LoginResponse> call,
                    @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse rp = response.body();
                    assert rp != null;
                    Log.i("LOGIN", "onResponse: " + rp.getTokens().toString());
                    MyApp.getInstance().getSessionManager().saveAuthToken(rp.getTokens().getAccess());
                    MyApp.getInstance().getSessionManager().saveAuthRefresh(rp.getTokens().getRefresh());
                    MyApp.getInstance().getSessionManager().saveUserAddress(rp.getAddress());
                    MyApp.getInstance().getSessionManager().saveUserEmail(rp.getEmail());
                    MyApp.getInstance().getSessionManager().saveUserId(rp.getId());
                    loginStatus.setValue(true);
                } else {
                    loginStatus.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                loginStatus.setValue(false);
            }
        });
    }

    public void logout() {

    }

    public void changePassword(String oldPassword, String newPassword) {
    }
}
