package me.cutmail.disasterapp;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.uphyca.galette.TrackerProvider;

import me.cutmail.disasterapp.model.Entry;

public class DisasterApplication extends Application implements TrackerProvider {

    private static final String PROPERTY_ID = "UA-3314949-13";

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        setupGoogleAnalytics();
        setupParse();
    }

    @Override
    public Tracker getByName(String trackerName) {
        return tracker;
    }

    private void setupGoogleAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(PROPERTY_ID);
    }

    private void setupParse() {
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Entry.class);
        Parse.initialize(this, "QL3YO70ii8CHNVyUz591gUCgfimdVqbnLmzO2SFm", "IJ5aSNUzuAcRkmwiI7IsEkH2623rJVtbZQx6azKV");

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnonymousUtils.logInInBackground();
    }
}
