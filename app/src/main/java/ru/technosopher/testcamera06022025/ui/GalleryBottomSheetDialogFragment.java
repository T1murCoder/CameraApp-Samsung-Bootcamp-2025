package ru.technosopher.testcamera06022025.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ru.technosopher.testcamera06022025.R;
import ru.technosopher.testcamera06022025.adapter.ImageAdapter;
import ru.technosopher.testcamera06022025.databinding.GalleryBottomSheetBinding;
import ru.technosopher.testcamera06022025.viewmodel.RemoteGalleryViewModel;
import ru.technosopher.testcamera06022025.viewmodel.SharedGalleryViewModel;

public class GalleryBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private GalleryBottomSheetBinding binding;

    private SharedGalleryViewModel sharedGalleryViewModel;
    private RemoteGalleryViewModel remoteGalleryViewModel;

    private ImageAdapter adapter;

    public GalleryBottomSheetDialogFragment() {
        super(R.layout.gallery_bottom_sheet);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = GalleryBottomSheetBinding.bind(view);

        sharedGalleryViewModel = new ViewModelProvider(requireActivity()).get(SharedGalleryViewModel.class);
        remoteGalleryViewModel = new ViewModelProvider(this).get(RemoteGalleryViewModel.class);

        adapter = new ImageAdapter();
        adapter.setOnImageClick(sharedGalleryViewModel::uploadImage);

        binding.rvGallery.setAdapter(adapter);

        binding.btnLocal.setOnClickListener(v -> {
            adapter.setOnImageClick(sharedGalleryViewModel::uploadImage);
            sharedGalleryViewModel.update();
        });
        binding.btnRemote.setOnClickListener(v -> {
            adapter.setOnImageClick(null);
            remoteGalleryViewModel.update();
        });

        subscribe();
        sharedGalleryViewModel.update();
    }

    private void subscribe() {
        sharedGalleryViewModel.bitmapListLiveData.observe(getViewLifecycleOwner(), bitmapList -> {
            adapter.submitList(bitmapList);
        });
        remoteGalleryViewModel.bitmapListLiveData.observe(getViewLifecycleOwner(), bitmapList -> {
            adapter.submitList(bitmapList);
        });
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
