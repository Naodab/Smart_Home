package com.smarthome.mobile.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.repository.DeviceRepository;
import com.smarthome.mobile.util.Result;

public class DeviceViewModel {
    private final DeviceRepository deviceRepository;

    public DeviceViewModel() {
        this.deviceRepository = new DeviceRepository();
    }

    public MutableLiveData<Result<Boolean>> getChangeDeviceStatus() {
        return deviceRepository.getChangeDeviceStatus();
    }

    public void changeStatusDevice(Device device, Status targetStatus) {
        this.deviceRepository.changeStatusDevice(device, targetStatus);
    }
}
