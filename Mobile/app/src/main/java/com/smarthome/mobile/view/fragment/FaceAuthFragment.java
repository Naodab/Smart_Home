package com.smarthome.mobile.view.fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentFaceAuthBinding;
import com.smarthome.mobile.view.activity.MainActivity;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.FaceAuthViewModel;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceAuthFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 101;
    private FragmentFaceAuthBinding binding;
    private ExecutorService executorService;
    private ImageCapture imageCapture;
    private FaceAuthViewModel faceAuthViewModel;
    private CustomLoadingDialog loading;
    private ImageAnalysis imageAnalysis;
    private boolean isAnalyzing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFaceAuthBinding.inflate(inflater, container, false);
        loading = new CustomLoadingDialog(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNav();
        }

        this.faceAuthViewModel = new ViewModelProvider(requireActivity())
                .get(FaceAuthViewModel.class);
        animatePreviewView();

        View[] viewsToAnimate = {binding.backBtn, binding.cameraStatus};

        for (int i = 0; i < viewsToAnimate.length; i++) {
            View viewToAnimate = viewsToAnimate[i];

            long delay = i * 300L;

            ObjectAnimator animator = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0f, 1f);
            animator.setStartDelay(delay);
            animator.setDuration(500);
            animator.start();
        }

        executorService = Executors.newSingleThreadExecutor();

        requestPermissions(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
        startCamera();
        binding.backBtn.setOnClickListener(v -> {
            if (isAnalyzing) {
                isAnalyzing = false;
            }
            backToHome();
        });

        faceAuthViewModel.getAuthenticateStatus().observe(getViewLifecycleOwner(), result -> {
            switch (result.status) {
                case LOADING:
                    loading.show();
                    break;
                case ERROR:
                    loading.dismiss();
                    CustomToast.showError(requireContext(), "Lỗi xác thực khuôn mặt");
                    backToHome();
                    break;
                case SUCCESS:
                    loading.dismiss();
                    CustomToast.showSuccess(requireContext(), "Chào mừng " + result.data.getPersonName());
                    goToRemote();
                    break;
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(requireContext());
        future.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = future.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Log.d("Camera Fragment", Objects.requireNonNull(e.getMessage()));
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases(ProcessCameraProvider provider) {
        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder().build();

        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        isAnalyzing = true;
        imageAnalysis.setAnalyzer(executorService, this::analyzeImage);

        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        provider.unbindAll();
        provider.bindToLifecycle(this, selector, preview, imageCapture, imageAnalysis);
        preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {
        if (!isAnalyzing) {
            imageProxy.close();
            return;
        }
        InputImage image = InputImage.fromMediaImage(
                Objects.requireNonNull(imageProxy.getImage()),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build();
        FaceDetector faceDetector = FaceDetection.getClient(options);
        faceDetector.process(image).addOnSuccessListener(faces -> {
            imageProxy.close();
            if (!faces.isEmpty()) {
                imageAnalysis.clearAnalyzer();
                isAnalyzing = false;
                takePhoto();
                binding.cameraStatus.setText("Đã nhận được ảnh");
//                new android.os.Handler(Looper.getMainLooper()).postDelayed(this::backToHome, 2000);
            }
        }).addOnFailureListener(e -> Log.d("Analyze Image", Objects.requireNonNull(e.getMessage())));
    }

    public void takePhoto() {
        if (imageCapture == null) return;

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        image.close();
                        faceAuthViewModel.authenticateFace(bytes);
//                        faceAuthViewModel.uploadImageToServer(bytes, new FaceAuthCallback() {
//                            @Override
//                            public void onSuccess() {
//                                Log.d("UploadSuccess", "Picture uploaded successfully!");
//                                Toast.makeText(requireContext(), "Ảnh đã đuợc upload đến server!", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onFailure() {
//                                Log.e("UploadFailure", "Picture upload failed");
//                                Toast.makeText(requireContext(), "Thất bại!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraX", "Chụp ảnh thất bại: " + exception.getMessage(), exception);
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void animatePreviewView() {
        CardView previewView = binding.cardView;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(previewView, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(previewView, "scaleY", 0.8f, 1f);

        scaleX.setDuration(300);
        scaleY.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
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
    }
}