package me.cutmail.disasterapp.controllers;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.uphyca.galette.SendScreenView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cutmail.disasterapp.R;

public class EntryDetailActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_URL = "url";

    private String url;

    @InjectView(R.id.web)
    WebView webView;

    public static Intent createIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, EntryDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    @SendScreenView(screenName = "entry_detail")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        ButterKnife.inject(this);

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

        url = intent.getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
        } else {
            webView.loadUrl(url);
        }
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
