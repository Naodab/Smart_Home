package com.smarthome.mobile.view;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentLoginBinding;
import com.smarthome.mobile.util.Constants;
import com.smarthome.mobile.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginViewModel = new AuthViewModel();
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View[] viewsToAnimate = {binding.background, binding.title, binding.loginLayout};

        for (int i = 0; i < viewsToAnimate.length; i++) {
            View viewToAnimate = viewsToAnimate[i];

            long delay = i * 300L;

            ObjectAnimator animator = ObjectAnimator
                    .ofFloat(viewToAnimate, "alpha", 0f, 1f);
            animator.setStartDelay(delay);
            animator.setDuration(500);
            animator.start();
        }

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                CustomToast.showError(v.getContext(), "Vui lòng điền đầy đủ thông tin!");
            } else {
                binding.loadingLayout.setVisibility(View.VISIBLE);
                loginViewModel.login(email, password);
            }
        });

        loginViewModel.getUserLiveData().observe(getViewLifecycleOwner(), userAuthentication -> {
            if (userAuthentication != null) {
                CustomToast.showSuccess(requireContext(), "Chào mưng bạn trở lại");
                binding.loadingLayout.setVisibility(View.INVISIBLE);
                Constants.LOGGED_IN = true;
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_loginFragment_to_homeFragment);
                loginViewModel.getUserLiveData().removeObservers(getViewLifecycleOwner());
            } else {
                if (Constants.LOGGED_IN) {
                    Constants.LOGGED_IN = false;
                } else {
                    CustomToast.showError(requireContext(), "Tài khoản hoặc mật khẩu không chính xác!");
                    binding.loadingLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}