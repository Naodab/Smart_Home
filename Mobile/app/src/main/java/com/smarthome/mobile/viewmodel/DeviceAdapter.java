package com.smarthome.mobile.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.DeviceItemBinding;
import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final List<Device> devices;
    private final CustomLoadingDialog loading;
    private final DeviceViewModel deviceViewModel;

    public DeviceAdapter(List<Device> devices, CustomLoadingDialog loading,
                         DeviceViewModel deviceViewModel) {
        this.devices = devices;
        this.loading = loading;
        this.deviceViewModel = deviceViewModel;
    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeviceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.device_item,
                parent,
                false);
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

    public void updateDevices(List<Device> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
        notifyDataSetChanged();
    }

    public void changeStatus(int position, Status newStatus) {
        Device device = devices.get(position);
        if (device == null) return;
        deviceViewModel.changeStatusDevice(device, newStatus);
        deviceViewModel.getChangeDeviceStatus().observe();
        device.setStatus(newStatus);
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        DeviceItemBinding binding;

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
            binding.setDevice(device);
            binding.executePendingBindings();

            binding.statusesDefault.setVisibility(View.GONE);
            binding.statusesFan.setVisibility(View.GONE);
            binding.statusesLight.setVisibility(View.GONE);

            switch (device.getType()) {
                case DOOR:
                    binding.deviceIcon.setBackgroundResource(R.drawable.ic_door_open);
                    binding.statusesDefault.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case OPEN:
                            setButtonBackground(binding.openBtn, binding.closeBtn);
                            break;
                        case CLOSE:
                            setButtonBackground(binding.closeBtn, binding.openBtn);
                            break;
                    }
                    break;
                case FAN:
                    binding.deviceIcon.setBackgroundResource(R.drawable.ic_fan_on);
                    binding.statusesFan.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case STOP:
                            setButtonBackground(binding.offFanBtn, binding.lowFanBtn,
                                    binding.mediumFanBtn, binding.highFanBtn);
                            break;
                        case LOW:
                            setButtonBackground(binding.lowFanBtn, binding.offFanBtn,
                                    binding.mediumFanBtn, binding.highFanBtn);
                            break;
                        case MEDIUM:
                            setButtonBackground(binding.mediumFanBtn, binding.offFanBtn,
                                    binding.lowFanBtn, binding.highFanBtn);
                            break;
                        case HIGH:
                            setButtonBackground(binding.highFanBtn, binding.offFanBtn,
                                    binding.lowFanBtn, binding.mediumFanBtn);
                            break;
                    }
                    break;
                case LIGHT:
                    binding.deviceIcon.setBackgroundResource(R.drawable.ic_light_on);
                    binding.statusesLight.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case ON:
                            setButtonBackground(binding.onBtn, binding.offBtn);
                            break;
                        case CLOSE:
                            setButtonBackground(binding.offBtn, binding.onBtn);
                            break;
                    }
                    break;
                case CURTAIN:
                    binding.deviceIcon.setBackgroundResource(R.drawable.ic_curtain_on);
                    binding.statusesDefault.setVisibility(View.VISIBLE);
                    switch (device.getStatus()) {
                        case OPEN:
                            setButtonBackground(binding.openBtn, binding.closeBtn);
                            break;
                        case CLOSE:
                            setButtonBackground(binding.closeBtn, binding.openBtn);
                            break;
                    }
                    break;
            }
        }
    }
}
