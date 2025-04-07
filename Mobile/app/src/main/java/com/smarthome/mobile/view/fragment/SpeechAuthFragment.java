package com.smarthome.mobile.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.smarthome.mobile.view.activity.MainActivity;
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

    private int playSound() {
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.start_audio);
        int duration = mediaPlayer.getDuration();
        mediaPlayer.release();
        return duration;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backBtn.setOnClickListener(v -> backToHome());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNav();
        }

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
            Log.d("record", "1");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("record", "2");
                    int duration = playSound();
                    scaleView(binding.btnMic, 1.0f, SCALE);
                    new Handler(Looper.getMainLooper()).postDelayed(
                            () -> speechAuthViewModel.startRecording(), duration);
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

    private void backToHome() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNav();
        }
    }
}