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

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cutmail.disasterapp.R;
import me.cutmail.disasterapp.model.Entry;

public class EntryDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ENTRY = "entry";

    private Entry entry;

    @InjectView(R.id.web)
    WebView webView;

    public static Intent createIntent(Context context, Entry entry) {
        Intent intent = new Intent(context, EntryDetailActivity.class);
        intent.putExtra(EXTRA_ENTRY, entry);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        ButterKnife.inject(this);

        setupWebView();

        if (getIntent() != null) {
            entry = (Entry) getIntent().getSerializableExtra(EXTRA_ENTRY);
            if (entry == null) {
                finish();
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(entry.getTitle());
        }

        if (TextUtils.isEmpty(entry.getUrl())) {
            finish();
        } else {
            webView.loadUrl(entry.getUrl());
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
        Uri uri = Uri.parse(entry.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
