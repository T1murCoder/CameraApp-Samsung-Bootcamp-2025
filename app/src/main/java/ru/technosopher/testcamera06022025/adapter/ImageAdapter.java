package ru.technosopher.testcamera06022025.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import ru.technosopher.testcamera06022025.databinding.ItemImageBinding;

public class ImageAdapter extends ListAdapter<Bitmap, ImageAdapter.ViewHolder> {

    public static final String TAG = "IMAGE_ADAPTER";
    private Consumer<Bitmap> onImageClick;

    public ImageAdapter() {
        super(IMAGE_DIFF);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ItemImageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setOnImageClick(Consumer<Bitmap> onImageClick) {
        this.onImageClick = onImageClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ItemImageBinding binding;

        public ViewHolder(@NonNull ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Bitmap bitmap) {
            binding.getRoot().setOnLongClickListener(v -> {
                if (onImageClick != null) {
                    onImageClick.accept(bitmap);
                    Log.d(TAG, "Image is uploading!");
                }
                return true;
            });
            binding.setImageBitmap(bitmap);
            binding.executePendingBindings();
        }
    }

    private static final DiffUtil.ItemCallback<Bitmap> IMAGE_DIFF = new DiffUtil.ItemCallback<Bitmap>() {
        @Override
        public boolean areItemsTheSame(@NonNull Bitmap oldItem, @NonNull Bitmap newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Bitmap oldItem, @NonNull Bitmap newItem) {
            return oldItem.sameAs(newItem);
        }
    };
}
