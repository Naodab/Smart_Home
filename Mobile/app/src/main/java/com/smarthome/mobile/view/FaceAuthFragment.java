package com.smarthome.mobile.view;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
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
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.smarthome.mobile.R;
import com.smarthome.mobile.databinding.FragmentFaceAuthBinding;
import com.smarthome.mobile.viewmodel.FaceAuthViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceAuthFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 101;
    private FragmentFaceAuthBinding binding;
    private ExecutorService executorService;
    private ImageCapture imageCapture;
    private FaceAuthViewModel faceAuthViewModel;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            Navigation.findNavController(view).navigate(R.id.action_faceAuthFragment_to_homeFragment);
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
                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Navigation.findNavController(requireView()).navigate(R.id.action_faceAuthFragment_to_homeFragment);
                }, 2000);
            }
        }).addOnFailureListener(e -> Log.d("Analyze Image", Objects.requireNonNull(e.getMessage())));
    }

    public void takePhoto() {
        if (imageCapture == null) return;
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                requireActivity().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Toast.makeText(requireContext(), "Ảnh đã lưu!", Toast.LENGTH_SHORT).show();
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
}