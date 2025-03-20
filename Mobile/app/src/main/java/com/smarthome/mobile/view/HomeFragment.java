package com.smarthome.mobile.view;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentHomeBinding;
import com.smarthome.mobile.repository.AuthRepository;
import com.smarthome.mobile.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private BottomNavigationView bottomNav;
    private AuthRepository authRepository;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = HomeViewModel.getInstance();
        View view = binding.getRoot();
        bottomNav = view.findViewById(R.id.bottom_navigation);
        authRepository = AuthRepository.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showBottomNav();

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_logout) {
                authRepository.logout();
            } else if (item.getItemId() == R.id.navigation_monitor) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_homeFragment_to_monitorFragment);
            }
            return false;
        });

        homeViewModel.getUserLiveData().observe(getViewLifecycleOwner(), userAuthentication -> {
            if (userAuthentication == null) {
                Toast.makeText(view.getContext(), "Đăng xuất thành công.", Toast.LENGTH_SHORT).show();
                hideBottomNav();
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });

        binding.faceAuthBtn.setOnClickListener(v -> {
            hideBottomNav();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_homeFragment_to_faceAuthFragment);
        });

        binding.speechAuthBtn.setOnClickListener(v -> {
            hideBottomNav();
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_speechAuthFragment);
        });
    }

    public void showBottomNav() {
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
    }

    public void hideBottomNav() {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                bottomNav.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}
        });
        bottomNav.startAnimation(anim);
    }
}