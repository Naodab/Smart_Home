package com.smarthome.mobile.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.LocationItemBinding;
import com.smarthome.mobile.model.Location;
import com.smarthome.mobile.repository.DeviceRepository;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private final List<Location> locations;
    private final CustomLoadingDialog loading;
    private final DeviceViewModel deviceViewModel;

    public LocationAdapter(List<Location> locations, CustomLoadingDialog loading) {
        this.locations = locations;
        this.loading = loading;
        this.deviceViewModel = new DeviceViewModel();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        LocationItemBinding binding;
        DeviceAdapter deviceAdapter;

        public ViewHolder (LocationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.moreBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Location loc = locations.get(pos);
                    loc.setExpanded(true);
                    notifyItemChanged(pos);
                }
            });

            binding.littleBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Location loc = locations.get(pos);
                    loc.setExpanded(false);
                    notifyItemChanged(pos);
                }
            });
        }

        public void bind(Location location) {
            binding.setLocation(location);
            binding.executePendingBindings();

            DeviceAdapter deviceAdapter = new DeviceAdapter(location.getDevices(), loading, deviceViewModel);
            binding.devices.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            binding.devices.setAdapter(deviceAdapter);

            boolean isExpanded = location.isExpanded();
            binding.devices.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            binding.moreBtn.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            binding.littleBtn.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

    }
}
