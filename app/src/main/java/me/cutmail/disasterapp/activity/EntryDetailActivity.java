package me.cutmail.disasterapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.cutmail.disasterapp.R;

public class EntryDetailActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_URL = "url";

    private String title;
    private String url;

    @BindView(R.id.web)
    WebView webView;

    public static Intent createIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, EntryDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        ButterKnife.bind(this);

        setupWebView();

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(intent.getStringExtra(EXTRA_TITLE));
        }

        title = intent.getStringExtra(EXTRA_TITLE);
        url = intent.getStringExtra(EXTRA_URL);

        trackContentViewEventWithAnswers();

        if (TextUtils.isEmpty(url)) {
            finish();
        } else {
            webView.loadUrl(url);
        }
    }

    private void trackContentViewEventWithAnswers() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "title");
        bundle.putString("url", url);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_share:
                doShare();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doShare() {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
