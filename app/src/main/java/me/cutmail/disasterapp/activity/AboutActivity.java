package me.cutmail.disasterapp.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
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
            Timber.e(e, e.getMessage());
            version = "";
        }

        return version;
    }
}
