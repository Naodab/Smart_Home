package com.smarthome.mobile.view.activity;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.ActivityMainBinding;
import com.smarthome.mobile.view.fragment.HomeFragment;
import com.smarthome.mobile.view.fragment.MonitorFragment;
import com.smarthome.mobile.view.fragment.ProfileFragment;
import com.smarthome.mobile.view.fragment.RemoteFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private int currentFragment = R.id.navigation_home;
    private FragmentTransaction fragmentTransaction;

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

        bottomNav = binding.bottomNavLayout.bottomNavigation;
        bottomNav.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId  == currentFragment) {
                return false;
            }
            int prevFragment =  currentFragment;
            currentFragment = itemId;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (itemId ==  R.id.navigation_home) {
                slideInLeft();
                fragmentTransaction.replace(R.id.fragmentContainerView, new HomeFragment());
            } else if (itemId == R.id.navigation_remote) {
                if (prevFragment == R.id.navigation_home)
                    slideInRight();
                else
                    slideInLeft();
                fragmentTransaction.replace(R.id.fragmentContainerView, new RemoteFragment());
            } else if (itemId == R.id.navigation_monitor) {
                if (prevFragment == R.id.navigation_profile)
                    slideInLeft();
                else
                    slideInRight();
                fragmentTransaction.replace(R.id.fragmentContainerView, new MonitorFragment());
            } else if (itemId ==  R.id.navigation_profile) {
                slideInRight();
                fragmentTransaction.replace(R.id.fragmentContainerView, new ProfileFragment());
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

    private void slideInLeft() {
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
    }

    private void slideInRight() {
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