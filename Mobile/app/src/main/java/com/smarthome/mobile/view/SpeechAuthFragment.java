package com.smarthome.mobile.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentSpeechAuthBinding;
import com.smarthome.mobile.util.SpeechAuthCallBack;
import com.smarthome.mobile.viewmodel.SpeechAuthViewModel;

public class SpeechAuthFragment extends Fragment {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private FragmentSpeechAuthBinding binding;
    private SoundPool soundPool;
    private int soundId;
    private final float SCALE = 0.9f;
    private SpeechAuthViewModel speechAuthViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setUpSound();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpeechAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void setUpSound() {
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundId = soundPool.load(requireContext(), R.raw.start_audio, 1);
    }

    private void playSound() {
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backBtn.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_speechAuthFragment_to_homeFragment));

        speechAuthViewModel = new SpeechAuthViewModel(new SpeechAuthCallBack() {
            @Override
            public void onSuccess() {
                // TODO: when valid authentication
            }

            @Override
            public void onFailure() {
                // TODO: when invalid authentication
            }
        });

        binding.btnMic.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playSound();
                    scaleView(binding.btnMic, 1.0f, SCALE);
                    speechAuthViewModel.startRecording();
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scaleView(binding.btnMic, SCALE, 1.0f);
                    speechAuthViewModel.stopRecording();
                    return true;
            }
            return false;
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_CODE);
        }
    }

    private void scaleView(View view, float from, float to) {
        ScaleAnimation animation = new ScaleAnimation(
                from, to,
                from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        animation.setInterpolator(new OvershootInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(300);
        view.startAnimation(animation);
    }
}