package com.smarthome.mobile.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentHomeBinding;
import com.smarthome.mobile.repository.AuthRepository;
import com.smarthome.mobile.view.activity.MainActivity;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private AuthRepository authRepository;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = HomeViewModel.getInstance();
        View view = binding.getRoot();
        authRepository = new AuthRepository();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showBottomNav();

        binding.faceAuthBtn.setOnClickListener(v -> {
            hideBottomNav();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_homeFragment_to_faceAuthFragment);
        });

        binding.speechAuthBtn.setOnClickListener(v -> {
            hideBottomNav();
            Navigation.findNavController(view)
                    .navigate(R.id.action_homeFragment_to_speechAuthFragment);
        });
    }

    public void showBottomNav() {
        if (getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).showBottomNav();
    }

    public void hideBottomNav() {
        if (getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).hideBottomNav();
    }
}