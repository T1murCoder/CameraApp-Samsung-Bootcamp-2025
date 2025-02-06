package ru.technosopher.testcamera06022025.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import ru.technosopher.testcamera06022025.databinding.ActivityMainBinding;
import ru.technosopher.testcamera06022025.viewmodel.SharedGalleryViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MAIN_ACTIVITY";
    private ActivityMainBinding binding;

    private LifecycleCameraController cameraController;

    private ActivityResultLauncher<String> requestPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            startCamera();
        } else {
            this.finish(); // FIXME - В реальном приложении так лучше не делать!
        }
    });

    private SharedGalleryViewModel sharedGalleryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedGalleryViewModel = new ViewModelProvider(this).get(SharedGalleryViewModel.class);


        binding.btnSwitch.setOnClickListener(v -> {
            switchCamera();
        });

        binding.btnPhoto.setOnClickListener(v -> {
            takePhoto();
        });

        binding.btnGallery.setOnClickListener(v -> {
            openGallery();
        });
    }

    private void takePhoto() {
        if (cameraController != null) {
            cameraController.takePicture(
                    ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageCapturedCallback() {
                        @Override
                        public void onCaptureSuccess(@NonNull ImageProxy image) {
                            super.onCaptureSuccess(image);

                            Matrix matrix = new Matrix();
                            matrix.postRotate(image.getImageInfo().getRotationDegrees());

                            Bitmap rotatedBitmap = Bitmap.createBitmap(
                                    image.toBitmap(),
                                    0,
                                    0,
                                    image.getWidth(),
                                    image.getHeight(),
                                    matrix,
                                    true
                            );

                            onPhotoTaken(rotatedBitmap);
                            image.close();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            super.onError(exception);
                        }
                    }
            );
        }
    }

    private void onPhotoTaken(Bitmap bitmap) {
        Toast.makeText(this, "Photo taken!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Photo taken: " + bitmap);
        sharedGalleryViewModel.onTakePhoto(bitmap);
    }

    private void startCamera() {
        cameraController = new LifecycleCameraController(this);
        cameraController.bindToLifecycle(this);

        cameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE);

        binding.pvCamera.setController(cameraController);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
    }

    private void openGallery() {
        GalleryBottomSheetDialogFragment galleryBottomSheetDialogFragment = new GalleryBottomSheetDialogFragment();
        galleryBottomSheetDialogFragment.show(getSupportFragmentManager(), TAG);
    }

    private void switchCamera() {
        if (cameraController != null) {
            if (cameraController.getCameraSelector() == CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraController.setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA);
            } else {
                cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
            }
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissionResultLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraController != null) {
            cameraController.unbind();
        }
    }
}