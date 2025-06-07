package com.smarthome.mobile.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.dto.request.ChangeDeviceRequest;
import com.smarthome.mobile.dto.response.ChangeDeviceResponse;
import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.network.ApiClient;
import com.smarthome.mobile.network.ApiService;
import com.smarthome.mobile.util.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceRepository {
    private final ApiService apiService;
    private final MutableLiveData<Map<Integer, Result<Status>>> changeDeviceStatus;
    private final Map<Integer, Result<Status>> results;

    public DeviceRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.results = new ConcurrentHashMap<>();
        this.changeDeviceStatus = new MutableLiveData<>(this.results);
    }

    public MutableLiveData<Map<Integer, Result<Status>>> getChangeDeviceStatus() {
        return changeDeviceStatus;
    }

    public void changeStatusDevice(Device device, Status targetStatus) {
        int deviceId= device.getId();
        results.put(device.getId(), Result.loading());
        ChangeDeviceRequest request = new ChangeDeviceRequest(
            deviceId,
            targetStatus.toApiValue(),
            MyApp.getInstance().getSessionManager().fetchPersonID()
        );
        changeDeviceStatus.postValue(new HashMap<>(results));

        apiService.changeStatusDevice(deviceId, request).enqueue(new Callback<ChangeDeviceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChangeDeviceResponse> call,
                                   @NonNull Response<ChangeDeviceResponse> response) {
                if (response.isSuccessful()&& response.body() != null && response.body().isSuccess()) {
                    results.put(deviceId, Result.success(targetStatus));
                } else {
                    results.put(deviceId, Result.error("Can't change status of this device"));
                }
                changeDeviceStatus.postValue(new ConcurrentHashMap<>(results));
            }

            @Override
            public void onFailure(@NonNull Call<ChangeDeviceResponse> call,
                                  @NonNull Throwable throwable) {
                results.put(deviceId, Result.error("Can't change status of this device"));
                changeDeviceStatus.postValue(new ConcurrentHashMap<>(results));
            }
        });
    }
}
