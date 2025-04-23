package com.smarthome.mobile.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.ActivityMainBinding;
import com.smarthome.mobile.view.fragment.HomeFragment;
import com.smarthome.mobile.view.fragment.RemoteFragment;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.view.widget.DialogChangePassword;
import com.smarthome.mobile.view.widget.DialogSetting;
import com.smarthome.mobile.viewmodel.AuthViewModel;

public class MainActivity extends AppCompatActivity {
    private CustomLoadingDialog loading;
    private BottomNavigationView bottomNav;
    private int currentFragment = R.id.navigation_home;
    private FragmentTransaction fragmentTransaction;
    private DialogSetting dialogSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        DialogChangePassword dialogChangePassword = new DialogChangePassword(this, authViewModel);
        dialogSetting = new DialogSetting(this, authViewModel, dialogChangePassword);
        loading = new CustomLoadingDialog(this);

        authViewModel.getLogoutStatus().observe(this, result -> {
            switch (result.status) {
                case ERROR:
                    loading.dismiss();
                    dialogSetting.dismiss();
                    CustomToast.showError(this, result.message);
                    break;
                case SUCCESS:
                    loading.dismiss();
                    dialogSetting.dismiss();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOADING:
                    loading.show();
                    break;
            }
        });

        authViewModel.getChangePasswordStatus().observe(this, result -> {
            switch (result.status) {
                case ERROR:
                    CustomToast.showError(this, result.message);
                    loading.dismiss();
                    break;
                case SUCCESS:
                    CustomToast.showSuccess(this, "Thay đổi mật khẩu thành công");
                    loading.dismiss();
                    break;
                case LOADING:
                    loading.show();
                    break;
            }
        });

        bottomNav = binding.bottomNavLayout.bottomNavigation;
        bottomNav.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId  == currentFragment) {
                return false;
            }
            if (itemId == R.id.navigation_profile) {
                dialogSetting.show();
                return false;
            }
            int prevFragment =  currentFragment;
            currentFragment = itemId;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (itemId ==  R.id.navigation_home) {
                slideInLeft(fragmentTransaction);
                fragmentTransaction.replace(R.id.fragmentContainerView, new HomeFragment());
            } else if (itemId == R.id.navigation_remote) {
                if (prevFragment == R.id.navigation_home)
                    slideInRight(fragmentTransaction);
                else
                    slideInLeft(fragmentTransaction);
                fragmentTransaction.replace(R.id.fragmentContainerView, new RemoteFragment());
            }
            fragmentTransaction.commit();
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new HomeFragment())
                    .commit();
        }
        showBottomNav();
    }

    public void slideInLeft(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
    }

    public void slideInRight(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
    }

    public void showBottomNav() {
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up));
    }

    public void hideBottomNav() {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
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