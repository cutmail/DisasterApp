package me.cutmail.disasterapp.controllers;

import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.services.common.Crash;
import me.cutmail.disasterapp.R;

public class AboutActivity extends ActionBarActivity {

    @InjectView(R.id.version)
    TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        setupActionBar();
        setupLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setupLayout() {
        versionView.setText(getVersionName());
    }

    private String getVersionName() {
        String version;
        try {
            version = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(), 1).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
            version = "";
        }

        return version;
    }
}
