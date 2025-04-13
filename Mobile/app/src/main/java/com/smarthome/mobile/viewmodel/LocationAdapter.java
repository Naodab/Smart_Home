package com.smarthome.mobile.viewmodel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.LocationItemBinding;
import com.smarthome.mobile.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private final List<Location> locations;

    public LocationAdapter(List<Location> locations) {
        this.locations = locations;
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LocationItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
        , R.layout.location_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        holder.bind(locations.get(position));
    }

    public void updateData(List<Location> newLocations) {
        this.locations.clear();
        this.locations.addAll(newLocations);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LocationItemBinding binding;

        public ViewHolder (LocationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Location location) {
            binding.setLocation(location);
            binding.executePendingBindings();

//            DeviceAdapter deviceAdapter = new DeviceAdapter(location.getDevices());
//            binding.devices.setLayoutManager(new LinearLayoutManager(binding.devices.getContext()));
//            binding.devices.setAdapter(deviceAdapter);
        }
    }
}
