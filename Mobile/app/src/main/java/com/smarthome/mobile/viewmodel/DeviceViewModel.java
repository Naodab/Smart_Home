package com.smarthome.mobile.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.repository.DeviceRepository;
import com.smarthome.mobile.util.Result;

import java.util.Map;

public class DeviceViewModel extends AndroidViewModel {
    private final DeviceRepository deviceRepository;

    public DeviceViewModel(Application application) {
        super(application);
        this.deviceRepository = new DeviceRepository();
    }

    public MutableLiveData<Map<Integer, Result<Status>>> getChangeDeviceStatus() {
        return deviceRepository.getChangeDeviceStatus();
    }

    public void changeStatusDevice(Device device, Status targetStatus) {
        this.deviceRepository.changeStatusDevice(device, targetStatus);
    }
}
