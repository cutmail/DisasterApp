package me.cutmail.disasterapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cutmail.disasterapp.R;
import timber.log.Timber;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version)
    TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setupActionBar();
        setupLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
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
                    getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            version = "";
        }

        return version;
    }

    @OnClick(R.id.license_container)
    void openLicense() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source_licenses));
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    }
}
