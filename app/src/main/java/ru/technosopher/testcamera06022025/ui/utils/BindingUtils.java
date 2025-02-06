package ru.technosopher.testcamera06022025.ui.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class BindingUtils {

    @BindingAdapter("imageBitmap")
    public static void loadImage(ImageView view, Bitmap bitmap) {
        Glide.with(view)
                .load(bitmap)
                .into(view);
    }
}
