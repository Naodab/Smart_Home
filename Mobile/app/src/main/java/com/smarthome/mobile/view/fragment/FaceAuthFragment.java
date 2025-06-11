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
    private ProcessCameraProvider cameraProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("FaceAuthFragment Log", "onCreateView");
        binding = FragmentFaceAuthBinding.inflate(inflater, container, false);
        loading = new CustomLoadingDialog(requireContext());
        // Luôn tạo mới ExecutorService khi tạo lại view
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("FaceAuthFragment Log", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNav();
        }

        // Luôn tạo lại ExecutorService nếu cần
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
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

        requestPermissions(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);

        // Luôn khởi động lại camera khi vào lại fragment
        startCamera();

        binding.backBtn.setOnClickListener(v -> {
            if (isAnalyzing) {
                isAnalyzing = false;
            }
            backToHome();
        });

        // Luôn observe lại ViewModel mỗi lần fragment vào lại (onViewCreated luôn được gọi lại)
        faceAuthViewModel.getAuthenticateStatus().observe(getViewLifecycleOwner(), result -> {
            try {
                if (result == null) {
                    Log.w("FaceAuthFragment", "Received null result from ViewModel");
                    loading.dismiss();
                    return;
                }

                switch (result.status) {
                    case LOADING:
                        if (loading != null) loading.show();
                        break;
                    case ERROR:
                        if (loading != null) loading.dismiss();
                        if (getContext() != null) {
                            CustomToast.showError(requireContext(), "Lỗi xác thực khuôn mặt");
                        }
                        faceAuthViewModel.resetState();
                        backToHome();
                        break;
                    case SUCCESS:
                        if (loading != null) loading.dismiss();
                        if (getContext() != null) {
                            String personName = (result.data != null && result.data.getPersonName() != null)
                                    ? result.data.getPersonName()
                                    : "người dùng";
                            CustomToast.showSuccess(requireContext(), "Chào mừng " + personName);
                        }
                        faceAuthViewModel.resetState();
                        goToRemote();
                        break;
                }
            } catch (Exception e) {
                Log.e("FaceAuthFragment", "Error in observer: " + e.getMessage(), e);
                if (loading != null) loading.dismiss();
            }
        });
    }

    private void startCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(requireContext());
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                bindCameraUseCases(provider);
            } catch (Exception e) {
                Log.e("Camera Fragment", "Camera initialization error: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases(ProcessCameraProvider provider) {
        this.cameraProvider = provider;
        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder().build();

        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        // Luôn set lại analyzer và isAnalyzing = true khi bind camera
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
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraX", "Chụp ảnh thất bại: " + exception.getMessage(), exception);
                    }
                }
        );
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

    private void resetAuthenticationState() {
        // Luôn set lại analyzer và isAnalyzing = true khi quay lại fragment
        isAnalyzing = true;
        if (imageAnalysis != null && executorService != null && !executorService.isShutdown()) {
            imageAnalysis.setAnalyzer(executorService, this::analyzeImage);
        }
    }

    private void backToHome() {
        cleanupCamera();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new HomeFragment())
                .commit();
    }

    public void goToRemote() {
        cleanupCamera();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        ((MainActivity) requireActivity()).slideInLeft(transaction);
        transaction.replace(R.id.fragmentContainerView, new RemoteFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void cleanupCamera() {
        isAnalyzing = false;
        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
        }
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    @Override
    public void onPause() {
        Log.d("FaceAuthFragment Log", "onPause");
        super.onPause();
        isAnalyzing = false;
        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
        }
    }

    @Override
    public void onResume() {
        Log.d("FaceAuthFragment Log", "onResume");
        super.onResume();
        // Luôn tạo lại executorService nếu đã shutdown hoặc null
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        // Nếu cameraProvider chưa có, startCamera lại, còn không thì reset lại analyzer
        if (cameraProvider == null) {
            startCamera();
        } else {
            resetAuthenticationState();
        }
    }

    @Override
    public void onDestroyView() {
        Log.d("FaceAuthFragment Log", "onDestroyView");
        super.onDestroyView();

        isAnalyzing = false;

        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
            imageAnalysis = null;
        }

        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            executorService = null;
        }

        if (faceAuthViewModel != null) {
            faceAuthViewModel.getAuthenticateStatus().removeObservers(this);
        }
    }
}