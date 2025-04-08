package com.smarthome.mobile.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.model.Home;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.network.ApiService;
import com.smarthome.mobile.util.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private final ApiService apiService;
    private final MutableLiveData<Result<Home>> homeLiveData;

    public HomeRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.homeLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Result<Home>> getHomeLiveData() {
        return homeLiveData;
    }

    public void getHome() {
        homeLiveData.setValue(Result.loading());
        apiService.getHome(MyApp.getInstance().getSessionManager()
                .fetchUserEmail()).enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call,
                                   @NonNull Response<Home> response) {
                if (response.isSuccessful()) {
                    homeLiveData.setValue(Result.success(response.body()));
                } else {
                    homeLiveData.setValue(Result.error("Không thể lấy dữ liệu từ server"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Home> call,
                                  @NonNull Throwable throwable) {
                homeLiveData.setValue(Result.error("Lỗi: " + throwable.getMessage()));
            }
        });
    }
}
