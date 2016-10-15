package me.cutmail.disasterapp;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import me.cutmail.disasterapp.model.Entry;
import timber.log.Timber;

public class DisasterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        setupParse();
        setupTimber();
    }

    private void setupParse() {
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Entry.class);
        Parse.initialize(this, "QL3YO70ii8CHNVyUz591gUCgfimdVqbnLmzO2SFm", "IJ5aSNUzuAcRkmwiI7IsEkH2623rJVtbZQx6azKV");

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnonymousUtils.logInInBackground();
    }

    private void setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Crashlytics.getInstance().core.log(priority, tag, message);

            if (t != null) {
                Crashlytics.getInstance().core.logException(t);
            }
        }
    }
}
