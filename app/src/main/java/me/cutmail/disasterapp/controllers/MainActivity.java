package me.cutmail.disasterapp.controllers;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseAnalytics;
import com.parse.ParseQueryAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import me.cutmail.disasterapp.R;
import me.cutmail.disasterapp.model.Entry;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.list)
    ListView listView;

    @InjectView(R.id.adView)
    AdView adView;

    private ParseQueryAdapter<Entry> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setupLayout();

        Fabric.with(this, new Crashlytics());
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setupAdView();
    }

    private void setupAdView() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inquiry:
                openInquiry();
                return true;
            case R.id.action_review:
                openPlayStore();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupLayout() {
        adapter = new ParseQueryAdapter<>(this, Entry.class, android.R.layout.simple_list_item_1);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void openInquiry() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:cutmailapp@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[地震・災害情報] お問い合わせ");
            intent.putExtra(Intent.EXTRA_TEXT, "こちらにお問い合わせ内容をご記入ください。");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Crashlytics.getInstance().core.logException(e);
        }
    }

    private void openPlayStore() {
        try {
            Uri uri = Uri.parse("market://details?id=me.cutmail.disasterapp");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Crashlytics.getInstance().core.logException(e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Entry entry = adapter.getItem(position);
        Intent intent = EntryDetailActivity.createIntent(this, entry.getTitle(), entry.getUrl());
        startActivity(intent);
    }
}
