package com.smarthome.mobile.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.ActivityLoginBinding;
import com.smarthome.mobile.util.ColorUtil;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private float startY = 0;
    private boolean isSwipedUp = false;
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private CustomLoadingDialog loading;

    @SuppressLint("ClickableViewAccessibility")
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

        ColorUtil.setTitleMainColor(binding.title);

        animateText(binding.title, getString(R.string.project_name), () -> binding.title.animate()
            .translationYBy(-700f)
            .setDuration(600)
            .withEndAction(() -> {
                binding.backgroundImage.setVisibility(View.VISIBLE);
                binding.btnToLogin.setVisibility(View.VISIBLE);
                binding.backgroundImage.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .start();
                binding.btnToLogin.setAlpha(1f);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.angle_up);
                binding.btnToLogin.startAnimation(animation);
                binding.btnToLogin.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            float endY = event.getY();
                            if (startY - endY > 150 && !isSwipedUp) {
                                isSwipedUp = true;
                                binding.backgroundImage.animate()
                                        .translationY(-400f)
                                        .setDuration(400)
                                        .start();
                                binding.loginLayout.setAlpha(0f);
                                binding.loginLayout.setVisibility(View.VISIBLE);
                                binding.loginLayout.animate()
                                        .alpha(1f)
                                        .setDuration(400)
                                        .start();
                                binding.btnToLogin.setVisibility(View.GONE);
                                ColorUtil.changeFromTitleMainColorToWhite(binding.title, 400);
                            }
                            return true;
                    }
                    return false;
                });

            })
            .start());

        binding.emailTemp.setOnFocusChangeListener((v, hasFocus) -> showLoginDialog(binding.email));
        binding.passwordTemp.setOnFocusChangeListener((v, hasFocus) -> showLoginDialog(binding.password));

        binding.loginFrameContainer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View current = getCurrentFocus();
                if (current instanceof EditText) {
                    current.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
                }
            }
            binding.loginFrameContainer.setVisibility(View.GONE);
            return false;
        });

        loading = new CustomLoadingDialog(this);
        authViewModel.getLoginStatus().observe(this, result -> {
            switch (result.status) {
                case SUCCESS:
                    this.loading.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case LOADING:
                    this.loading.show();
                    break;
                case ERROR:
                    this.loading.dismiss();
                    CustomToast.showError(LoginActivity.this, "Đăng nhập thất bại!");
                    break;
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                CustomToast.showError(v.getContext(), "Vui lòng điền đầy đủ thông tin!");
            } else {
                authViewModel.login(email, password);
            }
        });
    }

    private void showLoginDialog(EditText editText) {
        binding.loginFrameContainer.setVisibility(View.VISIBLE);
        binding.loginLayoutReal.setAlpha(0f);
        binding.loginLayoutReal.setTranslationY(100f);
        binding.loginLayoutReal.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();

        editText.post(() -> {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void animateText(final TextView textView, final String text,
                             final Runnable onDone) {
        final StringBuilder builder = new StringBuilder();
        final int[] index = {0};
        textView.setText("");

        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index[0] < text.length()) {
                    builder.append(text.charAt(index[0]));
                    textView.setText(builder.toString());
                    index[0]++;
                    textView.postDelayed(this, 200);
                } else {
                    if (onDone != null) onDone.run();
                }
            }
        }, 200);
    }
}