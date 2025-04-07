package com.smarthome.mobile.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.dto.request.ChangePasswordRequest;
import com.smarthome.mobile.dto.request.LoginRequest;
import com.smarthome.mobile.dto.request.RefreshRequest;
import com.smarthome.mobile.dto.response.LoginResponse;
import com.smarthome.mobile.dto.response.TokenResponse;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.network.ApiService;
import com.smarthome.mobile.util.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final MutableLiveData<Result<Boolean>> loginStatus;

    public AuthRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.loginStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Result<Boolean>> getLoginStatus() {
        return this.loginStatus;
    }

    public void login(String email, String password) {
        loginStatus.setValue(Result.loading());
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<LoginResponse> call,
                    @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse rp = response.body();
                    if (rp != null && rp.getTokens() != null) {
                        Log.i("LOGIN STATUS", "onResponse: " + rp.getEmail());
                        MyApp.getInstance().getSessionManager().saveAuthData(rp);
                        loginStatus.setValue(Result.success(true));
                    } else {
                        loginStatus.setValue(Result.error("Tài khoản hoặc mật khẩu không chính xác!"));
                    }
                } else {
                    loginStatus.setValue(Result.error("Tài khoản hoặc mật khẩu không chính xác!"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call,
                                  @NonNull Throwable t) {
                loginStatus.setValue(Result.error("Không thể gửi yêu cầu đển server!"));
            }
        });
    }

    public void logout() {
        loginStatus.setValue(null);
        MyApp.getInstance().getSessionManager().clear();
    }

    public void changePassword(String oldPassword, String newPassword) {
        apiService.changePassword(new ChangePasswordRequest(oldPassword, newPassword))
                .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Void> call,
                                  @NonNull Throwable throwable) {

            }
        });
    }

    public void refresh() {
        apiService.refreshToken(new RefreshRequest
                (MyApp.getInstance().getSessionManager()
                        .fetchUserRefreshToken())).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponse> call,
                                   @NonNull Response<TokenResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<TokenResponse> call,
                                  @NonNull Throwable t) {

            }
        });
    }
}
