package com.smarthome.mobile.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smarthome.mobile.databinding.FragmentMonitorBinding;
import com.smarthome.mobile.model.Home;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.DeviceAdapter;
import com.smarthome.mobile.viewmodel.HomeViewModel;

public class MonitorFragment extends Fragment {
    private FragmentMonitorBinding binding;
    private DeviceAdapter deviceAdapter;
    private HomeViewModel homeViewModel;
    private CustomLoadingDialog loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        binding.devicesList.setLayoutManager(new LinearLayoutManager(requireContext()));
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        loading = new CustomLoadingDialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel.fetchHome();
        homeViewModel.getHomeLiveData().observe(getViewLifecycleOwner(), result -> {
            switch (result.status) {
                case ERROR:
                    loading.dismiss();
                    CustomToast.showError(getContext(),  "Có lỗi xảy ra");
                    break;
                case LOADING:
                    loading.show();
                    break;
                case SUCCESS:
                    Home home = result.data;
                    binding.tvTemperature.setText(String.valueOf(home.getTemperature()));
                    binding.tvHumidity.setText(String.valueOf(home.getHumidity()));
                    binding.devicesList.setLayoutManager(new LinearLayoutManager(requireContext()));
                    deviceAdapter =  new DeviceAdapter(home.getDevices());
                    binding.devicesList.setAdapter(deviceAdapter);
                    loading.dismiss();
            }
        });
    }
}