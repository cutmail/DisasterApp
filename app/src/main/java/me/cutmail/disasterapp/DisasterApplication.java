package me.cutmail.disasterapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

public class DisasterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseCrashReporting.enable(this);
        Parse.initialize(this, "QL3YO70ii8CHNVyUz591gUCgfimdVqbnLmzO2SFm", "IJ5aSNUzuAcRkmwiI7IsEkH2623rJVtbZQx6azKV");

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("");
    }
}
