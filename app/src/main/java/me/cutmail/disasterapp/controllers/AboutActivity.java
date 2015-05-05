package me.cutmail.disasterapp.controllers;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cutmail.disasterapp.R;

public class AboutActivity extends AppCompatActivity {

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
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
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
