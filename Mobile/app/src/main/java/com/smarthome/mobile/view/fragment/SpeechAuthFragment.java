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
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentSpeechAuthBinding;
import com.smarthome.mobile.util.AnimationUtil;
import com.smarthome.mobile.util.AudioRecorderHelper;
import com.smarthome.mobile.util.SoundRecordUtil;
import com.smarthome.mobile.view.activity.MainActivity;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.SpeechAuthViewModel;

public class SpeechAuthFragment extends Fragment {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private FragmentSpeechAuthBinding binding;
    private SoundPool soundPool;
    private int soundId;
    private final float SCALE = 0.9f;
    private SpeechAuthViewModel speechAuthViewModel;
    private CustomLoadingDialog loading;

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
        loading = new CustomLoadingDialog(getContext());
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

        speechAuthViewModel = new ViewModelProvider(requireActivity()).get(SpeechAuthViewModel.class);
        speechAuthViewModel.setAudioRecorderHelper(new AudioRecorderHelper(speechAuthViewModel::uploadAudio));

        speechAuthViewModel.getAuthStatus().observe(getViewLifecycleOwner(), result -> {
            switch (result.status) {
                case LOADING:
                    loading.show();
                    break;
                case SUCCESS:
                    loading.dismiss();
                    CustomToast.showSuccess(requireContext(), "Chào mừng " + result.data.getPersonName());
                    goToRemote();
                    break;
                case ERROR:
                    loading.dismiss();
                    CustomToast.showError(requireContext(), result.message);
                    backToHome();
                    break;
            }
        });

        binding.micBtn.btnMic.setOnTouchListener((v, event) -> {
            Log.d("record", "1");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("record", "2");
                    int duration = SoundRecordUtil.getInstance(requireContext())
                            .playSound(requireContext());
                    AnimationUtil.scaleView(binding.micBtn.btnMic, 1.0f, SCALE);
                    new Handler(Looper.getMainLooper()).postDelayed(
                        () -> {
                            try {
                                speechAuthViewModel.startRecording();
                            } catch (Exception e) {
                                CustomToast.showError(requireContext(), e.getMessage());
                            }
                        }, duration);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    AnimationUtil.scaleView(binding.micBtn.btnMic, SCALE, 1.0f);
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

    private void backToHome() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToRemote() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        ((MainActivity) requireActivity()).slideInLeft(transaction);
        transaction.replace(R.id.fragmentContainerView, new RemoteFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SoundRecordUtil.getInstance(requireContext()).release();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNav();
        }
    }
}