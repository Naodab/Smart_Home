package com.smarthome.mobile.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.ActivityLoginBinding;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

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

        authViewModel.getLoginStatus().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                binding.loadingLayout.setVisibility(View.GONE);
                CustomToast.showError(LoginActivity.this, "Đăng nhập thất bại!");
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                CustomToast.showError(v.getContext(), "Vui lòng điền đầy đủ thông tin!");
            } else {
                binding.loadingLayout.setVisibility(View.VISIBLE);
                authViewModel.login(email, password);
            }
        });
    }
}