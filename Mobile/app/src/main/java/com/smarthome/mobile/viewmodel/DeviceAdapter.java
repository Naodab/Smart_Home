package com.smarthome.mobile.viewmodel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.DeviceItemBinding;
import com.smarthome.mobile.model.Device;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    List<Device> devices;

    public DeviceAdapter(List<Device> devices) {
        this.devices = devices;
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        DeviceItemBinding binding;

        public ViewHolder(DeviceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Device device) {
            binding.setDevice(device);
            binding.executePendingBindings();

            switch (device.getCategory()) {
                case DOOR:
                    if (device.isState()) {
                        binding.deviceIcon.setImageResource(R.drawable.ic_door_open);
                        binding.deviceState.setText("Trạng thái: đang mở");
                    } else {
                        binding.deviceIcon.setImageResource(R.drawable.ic_door_close);
                        binding.deviceState.setText("Trạng thái: đang đóng");
                    }
                    break;
                case LIGHT:
                    if (device.isState()) {
                        binding.deviceIcon.setImageResource(R.drawable.ic_light_on);
                        binding.deviceState.setText("Trạng thái: đang bật");
                    } else {
                        binding.deviceIcon.setImageResource(R.drawable.ic_light_off);
                        binding.deviceState.setText("Trạng thái: đang tắt");
                    }
                    break;
            }
        }
    }
}
