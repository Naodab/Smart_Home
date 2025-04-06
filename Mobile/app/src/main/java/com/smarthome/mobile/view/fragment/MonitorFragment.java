package com.smarthome.mobile.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentMonitorBinding;
import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.enums.Type;
import com.smarthome.mobile.model.Device;
import com.smarthome.mobile.repository.AuthRepository;
import com.smarthome.mobile.viewmodel.DeviceAdapter;

import java.util.ArrayList;
import java.util.List;

public class MonitorFragment extends Fragment {
    FragmentMonitorBinding binding;
    DeviceAdapter deviceAdapter;
    private BottomNavigationView bottomNav;
    private final AuthRepository authRepository = new AuthRepository();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        binding.devicesList.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Device> devices = new ArrayList<>();
        devices.add(new Device(0, "Cửa chính", Status.OPEN, Type.DOOR, null));
        devices.add(new Device(1, "Đèn phòng khách", Status.ON, Type.LIGHT, null));
        devices.add(new Device(2, "Đèn phòng ngủ", Status.OFF, Type.LIGHT, null));
        devices.add(new Device(3, "Đèn nhà bếp", Status.OFF, Type.LIGHT, null));
        bottomNav = binding.getRoot().findViewById(R.id.bottom_navigation);
        deviceAdapter = new DeviceAdapter(devices);
        binding.devicesList.setAdapter(deviceAdapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNav.setSelectedItemId(R.id.navigation_monitor);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_logout) {
                authRepository.logout();
            } else if (item.getItemId() == R.id.navigation_home) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_monitorFragment_to_homeFragment);
            }
            return false;
        });

    }
}