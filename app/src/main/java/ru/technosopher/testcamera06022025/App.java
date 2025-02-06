package ru.technosopher.testcamera06022025;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .server("https://parseapi.back4app.com")
                .applicationId("")
                .clientKey("")
                .enableLocalDataStore()
                .build()
        );

        ParseUser.enableAutomaticUser();
    }
}
