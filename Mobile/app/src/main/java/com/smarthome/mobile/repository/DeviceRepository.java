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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceRepository {
    private final ApiService apiService;
    private final MutableLiveData<Result<Boolean>> changeDeviceStatus;

    public DeviceRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.changeDeviceStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Result<Boolean>> getChangeDeviceStatus() {
        return changeDeviceStatus;
    }

    public void changeStatusDevice(Device device, Status targetStatus) {
        ChangeDeviceRequest request = new ChangeDeviceRequest(device.getId(), targetStatus.toApiValue(),
                MyApp.getInstance().getSessionManager().fetchPersonID());
        changeDeviceStatus.setValue(Result.loading());

        // In the real app should be not command this
//        apiService.changeStatusDevice(request).enqueue(new Callback<ChangeDeviceResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<ChangeDeviceResponse> call,
//                                   @NonNull Response<ChangeDeviceResponse> response) {
//                if (response.isSuccessful()&& response.body() != null && response.body().isSuccess()) {
//                    changeDeviceStatus.setValue(Result.success(true));
//                } else {
//                    changeDeviceStatus.setValue(Result.error("Can't change status this device"));
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ChangeDeviceResponse> call,
//                                  @NonNull Throwable throwable) {
//                changeDeviceStatus.setValue(Result.error("Can't change status this device"));
//            }
//        });

        // Mock request to test
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                changeDeviceStatus.setValue(Result.success(true));
            } catch (InterruptedException e) {
                changeDeviceStatus.setValue(Result.error("Can't change status this device"));
            }
        }).start();
    }
}
