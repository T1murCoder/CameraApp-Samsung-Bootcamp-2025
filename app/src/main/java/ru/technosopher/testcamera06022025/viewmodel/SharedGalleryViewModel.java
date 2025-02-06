package ru.technosopher.testcamera06022025.viewmodel;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SharedGalleryViewModel extends ViewModel {

    public static final String TAG = "SHARED_GALLERY_VIEWMODEL";
    private final MutableLiveData<List<Bitmap>> mutableBitmapListLiveData = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Bitmap>> bitmapListLiveData = mutableBitmapListLiveData;
    private final List<Bitmap> bitmapList = new ArrayList<>();

    public void update() {
        mutableBitmapListLiveData.postValue(bitmapList);
    }

    public void onTakePhoto(Bitmap bitmap) {
        bitmapList.add(bitmap);
    }

    public void uploadImage(Bitmap bitmap) {
        AsyncTask.execute(() -> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile file = new ParseFile("image.png", byteArray);

            file.saveInBackground((SaveCallback) e -> {
                if (e == null) {
                    ParseObject imageObject = new ParseObject("Images");
                    imageObject.put("image", file);
                    imageObject.saveInBackground(e1 -> {
                        if (e1 == null) {
                            Log.d(TAG, "Image uploaded successfully!");
                        } else {
                            Log.e(TAG, "Image failed to save: " + e1.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to save file: " + e.getMessage());
                }
            });
        });
    }
}
