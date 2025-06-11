package com.smarthome.mobile.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentHomeBinding;
import com.smarthome.mobile.view.activity.MainActivity;
import com.smarthome.mobile.view.widget.DialogSettingHome;
import com.smarthome.mobile.viewmodel.AuthViewModel;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DialogSettingHome dialogSettingHome;
    private AuthViewModel authViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("HomeFragment Log", "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("HomeFragment Log", "onCreateView");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        this.authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        Log.d("HomeFragment Log", "onCreated");
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) Objects.requireNonNull(requireActivity())).hideBottomNav();
        dialogSettingHome = new DialogSettingHome(requireContext(), () -> authViewModel.logout());

        binding.faceAuthBtn.setOnClickListener(v -> navigateToFragment(new FaceAuthFragment()));
        binding.speechAuthBtn.setOnClickListener(v -> navigateToFragment(new SpeechAuthFragment()));

        binding.btnSetting.setOnClickListener(v -> {
            this.dialogSettingHome.show();
        });
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }
}