package me.cutmail.disasterapp;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.HashMap;

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

        ParseCrashReporting.enable(this);
        Parse.initialize(this, "QL3YO70ii8CHNVyUz591gUCgfimdVqbnLmzO2SFm", "IJ5aSNUzuAcRkmwiI7IsEkH2623rJVtbZQx6azKV");

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("");
    }
}
