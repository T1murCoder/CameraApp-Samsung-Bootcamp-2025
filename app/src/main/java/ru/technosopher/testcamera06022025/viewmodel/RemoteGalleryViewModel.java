package ru.technosopher.testcamera06022025.viewmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bolts.Task;

public class RemoteGalleryViewModel extends ViewModel {
    public static final String TAG = "REMOTE_GALLERY_VIEWMODEL";
    private final MutableLiveData<List<Bitmap>> mutableBitmapListLiveData = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Bitmap>> bitmapListLiveData = mutableBitmapListLiveData;

    public void update() {
        downloadImages();
    }

    private void downloadImages() {
        Task<List<ParseObject>> imagesTask = ParseQuery.getQuery("Images").findInBackground();
        List<Bitmap> bitmaps = new ArrayList<>();
        imagesTask
                .onSuccessTask(task -> {
                    List<ParseFile> imageList = task.getResult()
                            .stream()
                            .map(parseObject -> parseObject.getParseFile("image"))
                            .collect(Collectors.toList());
                    imageList.forEach(parseFile -> {
                        if (parseFile != null) {
                            try {
                                byte[] data = parseFile.getData();
                                if (data != null) {
                                    bitmaps.add(BitmapFactory.decodeByteArray(data, 0, data.length));
                                }
                            } catch (ParseException e) {
                                Log.e(TAG, "Loading of file failed: " + parseFile, e);
                            }
                        }
                    });
                    return null;
                })
                .continueWith(task -> {
                    Log.d(TAG, "Data loaded: " + bitmaps);
                    mutableBitmapListLiveData.postValue(bitmaps);
                    return null;
                });
    }
}
