package com.smarthome.mobile.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.faceAuthBtn.setOnClickListener(v -> navigateToFragment(new FaceAuthFragment()));
        binding.speechAuthBtn.setOnClickListener(v -> navigateToFragment(new SpeechAuthFragment()));
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}