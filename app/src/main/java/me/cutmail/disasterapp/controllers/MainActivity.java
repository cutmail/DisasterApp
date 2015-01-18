package me.cutmail.disasterapp.controllers;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.parse.ParseAnalytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import io.fabric.sdk.android.Fabric;
import me.cutmail.disasterapp.R;
import me.cutmail.disasterapp.adapter.EntriesAdapter;
import me.cutmail.disasterapp.model.Entry;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.list)
    ListView listView;

    @InjectView(R.id.adView)
    AdView adView;

    private EntriesAdapter adapter;

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
        AdRequest adRequest = new AdRequest.Builder()
                .build();
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
        adapter = new EntriesAdapter(this, Entry.getEntries());
        listView.setAdapter(adapter);
    }

    @OnItemClick(R.id.list)
    void onItemClicked(int position) {
        Entry entry = adapter.getItem(position);
        Intent intent = EntryDetailActivity.createIntent(this, entry);
        startActivity(intent);
    }

    private void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void openInquiry() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:cutmailapp@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "[地震・災害情報] お問い合わせ");
        intent.putExtra(Intent.EXTRA_TEXT, "こちらにお問い合わせ内容をご記入ください。");
        startActivity(intent);
    }

    private void openPlayStore() {
        Uri uri = Uri.parse("market://details?id=me.cutmail.disasterapp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
