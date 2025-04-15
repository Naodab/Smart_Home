package com.smarthome.mobile.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.DeviceItemBinding;
import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.util.Result;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;

import java.util.List;
import java.util.Objects;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final List<Device> devices;
    private final CustomLoadingDialog loading;
    private final DeviceViewModel deviceViewModel;
    private final LifecycleOwner owner;

    public DeviceAdapter(List<Device> devices, CustomLoadingDialog loading,
                         DeviceViewModel deviceViewModel, LifecycleOwner owner) {
        this.devices = devices;
        this.loading = loading;
        this.deviceViewModel = deviceViewModel;
        this.owner = owner;
    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeviceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.device_item,
                parent,
                false);
        binding.setLifecycleOwner(owner);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceAdapter.ViewHolder holder, int position) {
        holder.bind(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void changeStatus(int position, Status newStatus) {
        Device device = devices.get(position);
        if (device == null) return;
        deviceViewModel.changeStatusDevice(device, newStatus);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        DeviceItemBinding binding;
        private int currentDeviceId;

        public ViewHolder(DeviceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            setupStatusButton(binding.onBtn, Status.ON);
            setupStatusButton(binding.offBtn, Status.OFF);
            setupStatusButton(binding.openBtn, Status.OPEN);
            setupStatusButton(binding.closeBtn, Status.CLOSE);
            setupStatusButton(binding.offFanBtn, Status.STOP);
            setupStatusButton(binding.lowFanBtn, Status.LOW);
            setupStatusButton(binding.mediumFanBtn, Status.MEDIUM);
            setupStatusButton(binding.highFanBtn, Status.HIGH);

            deviceViewModel.getChangeDeviceStatus().observe(Objects
                    .requireNonNull(binding.getLifecycleOwner()), statusMap -> {
                Result<Status> result = statusMap.get(currentDeviceId);
                if (result == null) return;
                Device device = findDeviceById(currentDeviceId);
                if (device == null) return;

                int latestPosition = getDevicePosition(currentDeviceId);
                if (latestPosition == RecyclerView.NO_POSITION) return;

                switch (result.status) {
                    case LOADING:
                        loading.show();
                        break;
                    case SUCCESS:
                        loading.dismiss();
                        device.setStatus(result.data);
                        notifyItemChanged(latestPosition);
                        break;
                    case ERROR:
                        loading.dismiss();
                        CustomToast.showError(itemView.getContext(), result.message);
                        break;
                }
            });
        }

        private int getDevicePosition(int deviceId) {
            for (int i = 0; i < devices.size(); i++) {
                if (devices.get(i).getId() == deviceId) {
                    return i;
                }
            }
            return RecyclerView.NO_POSITION;
        }

        private Device findDeviceById(int deviceId) {
            for (Device device : devices) {
                if (device.getId() == deviceId) {
                    return device;
                }
            }
            return null;
        }

        private void setupStatusButton(View button, Status targetStatus) {
            button.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION &&
                        binding.getDevice().getStatus() != targetStatus) {
                    changeStatus(position, targetStatus);
                }
            });
        }

        private void setButtonBackground(TextView selected, TextView... others) {
            selected.setBackgroundResource(R.drawable.bg_selected_status);
            for (TextView btn : others) {
                btn.setBackgroundResource(R.drawable.bg_status);
            }
        }

        public void bind(Device device) {
            currentDeviceId = device.getId();
            binding.setDevice(device);
            binding.executePendingBindings();

            binding.statusesDefault.setVisibility(View.GONE);
            binding.statusesFan.setVisibility(View.GONE);
            binding.statusesLight.setVisibility(View.GONE);

            switch (device.getType()) {
                case DOOR:
                    binding.statusesDefault.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case OPEN:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_door_open);
                            setButtonBackground(binding.openBtn, binding.closeBtn);
                            break;
                        case CLOSE:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_door_close);
                            setButtonBackground(binding.closeBtn, binding.openBtn);
                            break;
                    }
                    break;
                case FAN:
                    binding.statusesFan.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case STOP:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_fan_off);
                            setButtonBackground(binding.offFanBtn, binding.lowFanBtn,
                                    binding.mediumFanBtn, binding.highFanBtn);
                            break;
                        case LOW:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_fan_on);
                            setButtonBackground(binding.lowFanBtn, binding.offFanBtn,
                                    binding.mediumFanBtn, binding.highFanBtn);
                            break;
                        case MEDIUM:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_fan_on);
                            setButtonBackground(binding.mediumFanBtn, binding.offFanBtn,
                                    binding.lowFanBtn, binding.highFanBtn);
                            break;
                        case HIGH:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_fan_on);
                            setButtonBackground(binding.highFanBtn, binding.offFanBtn,
                                    binding.lowFanBtn, binding.mediumFanBtn);
                            break;
                    }
                    break;
                case LIGHT:
                    binding.statusesLight.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case ON:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_light_on);
                            setButtonBackground(binding.onBtn, binding.offBtn);
                            break;
                        case OFF:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_light_off);
                            setButtonBackground(binding.offBtn, binding.onBtn);
                            break;
                    }
                    break;
                case CURTAIN:
                    binding.statusesDefault.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case OPEN:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_curtain_on);
                            setButtonBackground(binding.openBtn, binding.closeBtn);
                            break;
                        case CLOSE:
                            binding.deviceIcon.setBackgroundResource(R.drawable.ic_curtain_off);
                            setButtonBackground(binding.closeBtn, binding.openBtn);
                            break;
                    }
                    break;
            }
        }
    }
}
