package me.cutmail.disasterapp;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.HashMap;

import me.cutmail.disasterapp.model.Entry;

public class DisasterApplication extends Application {

    private static final String PROPERTY_ID = "UA-3314949-13";

    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER
    }

    HashMap<TrackerName, Tracker> trackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!trackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            trackers.put(trackerId, t);
        }

        return trackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupParse();
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
