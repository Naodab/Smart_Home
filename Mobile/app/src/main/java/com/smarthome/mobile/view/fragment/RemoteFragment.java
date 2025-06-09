package com.smarthome.mobile.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.databinding.FragmentRemoteBinding;
import com.smarthome.mobile.model.Home;
import com.smarthome.mobile.model.Location;
import com.smarthome.mobile.network.WebSocketClient;
import com.smarthome.mobile.util.AnimationUtil;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.SoundRecordUtil;
import com.smarthome.mobile.util.WebSocketListenerInterface;
import com.smarthome.mobile.view.activity.MainActivity;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.view.widget.DialogChangePassword;
import com.smarthome.mobile.view.widget.DialogSetting;
import com.smarthome.mobile.viewmodel.AuthViewModel;
import com.smarthome.mobile.viewmodel.DeviceViewModel;
import com.smarthome.mobile.viewmodel.HomeViewModel;
import com.smarthome.mobile.viewmodel.LocationAdapter;
import com.smarthome.mobile.viewmodel.SpeechRemoteViewModel;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;

public class RemoteFragment extends Fragment {
    private FragmentRemoteBinding binding;
    private HomeViewModel homeViewModel;
    private CustomLoadingDialog loading;
    private LocationAdapter locationAdapter;
    private DialogSetting setting;
    private DialogChangePassword changePassword;
    private AuthViewModel authViewModel;
    private SpeechRemoteViewModel speechRemoteViewModel;
    private final float SCALE = 0.9f;
    private Home home;
    private WebSocketClient webSocketClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRemoteBinding.inflate(inflater, container, false);
        loading = new CustomLoadingDialog(getContext());
        speechRemoteViewModel = new ViewModelProvider(requireActivity())
                .get(SpeechRemoteViewModel.class);
        binding.locationList.setLayoutManager(new LinearLayoutManager(requireContext()));
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        DeviceViewModel deviceViewModel = new DeviceViewModel();
        this.authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        this.changePassword = new DialogChangePassword(requireContext(), authViewModel);
        setting = new DialogSetting(requireContext(), authViewModel, changePassword);
        locationAdapter = new LocationAdapter(new ArrayList<>(), loading,
                getViewLifecycleOwner(), deviceViewModel);
        binding.locationList.setAdapter(locationAdapter);
        return binding.getRoot();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        speechRemoteViewModel.setAudioRecorderHelper(new AudioRecorderHelper(speechRemoteViewModel::changeBySpeech));
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
                    home = result.data;
                    binding.tvTemperature.setText(MessageFormat.format("{0}°C",
                            (int) home.getTemperature()));
                    binding.tvHumidity.setText(MessageFormat.format("{0}%",
                            (int) home.getTemperature()));
                    locationAdapter.updateData(home.getLocations());
                    loading.dismiss();
            }
        });

        binding.btnMic.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("record", "2");
                    int duration = SoundRecordUtil.getInstance(requireContext())
                            .playSound(requireContext());
                    AnimationUtil.scaleView(binding.btnMic, 1.0f, SCALE);
                    new Handler(Looper.getMainLooper()).postDelayed(
                        () -> {
                            try {
                                speechRemoteViewModel.startRecording();
                            } catch (Exception e) {
                                CustomToast.showError(requireContext(), e.getMessage());
                            }
                        }, duration);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    AnimationUtil.scaleView(binding.btnMic, SCALE, 1.0f);
                    speechRemoteViewModel.stopRecording();
                    return true;
            }
            return false;
        });

        speechRemoteViewModel.getSpeechRemoteStatus().observe(getViewLifecycleOwner(), result -> {
            switch (result.status) {
                case LOADING:
                    loading.show();
                    break;
                case SUCCESS:
                    loading.dismiss();
                    int id = result.data.getId();
                    for (int i = 0; i < home.getLocations().size(); i++) {
                        boolean isFound = false;
                        Location location = home.getLocations().get(i);
                        for (int  j = 0; j < home.getLocations().get(i).getDevices().size(); j++) {
                            if (location.getDevices().get(j).getId() == id) {
                                isFound = true;
                                location.getDevices().get(j).setStatus(result.data.getStatus());
                                locationAdapter.notifyItemChanged(i);
                                break;
                            }
                        }
                        if (isFound) {
                            break;
                        }
                    }
                    break;
                case ERROR:
                    loading.dismiss();
                    CustomToast.showError(requireContext(), result.message);
                    break;
            }
        });

        this.webSocketClient = new WebSocketClient(new WebSocketListenerInterface() {
            @Override
            public void onMessageReceived(String message) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String command = jsonObject.getString("command");
                        if ("init_response".equals(command)) {
                            boolean isSuccess = jsonObject.getBoolean("success");
                            if (!isSuccess) {
                                CustomToast.showError(requireContext(), "Websocket failure");
                            }
                        } else if ("temp_humid".equals(command)) {
                            float temperature = (float) jsonObject.getDouble("temperature");
                            float humidity = (float) jsonObject.getDouble("humidity");
                            binding.tvTemperature.setText(MessageFormat.format("{0}°C",
                                    (int) temperature));
                            binding.tvHumidity.setText(MessageFormat.format("{0}%",
                                    (int) humidity));
                        }
                    } catch (Exception e) {
                        CustomToast.showError(requireContext(), "Websocket failure");
                    }
                });
            }

            @Override
            public void onWebSocketConnected() {

            }

            @Override
            public void onWebSocketDisconnected() {

            }

            @Override
            public void onWebSocketError(String error) {

            }
        });
        this.webSocketClient.startWebSocket();

        binding.settingBtn.setOnClickListener(v -> setting.show());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.webSocketClient.close();
        SoundRecordUtil.getInstance(requireContext()).release();
    }
}