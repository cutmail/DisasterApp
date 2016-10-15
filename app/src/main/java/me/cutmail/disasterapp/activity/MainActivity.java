package me.cutmail.disasterapp.activity;

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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import hotchemi.android.rate.AppRate;
import io.fabric.sdk.android.Fabric;
import me.cutmail.disasterapp.R;
import me.cutmail.disasterapp.model.Entry;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.listView)
    ListView listView;

    private FirebaseListAdapter<Entry> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupLayout();

        Fabric.with(this, new Crashlytics());
        setupRateDialog();

        AppRate.showRateDialogIfMeetsConditions(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    private void setupRateDialog() {
        AppRate.with(this).monitor();
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("entries");
        adapter = new FirebaseListAdapter<Entry>(this, Entry.class, android.R.layout.simple_list_item_1, ref) {
            @Override
            protected void populateView(View v, Entry entry, int position) {
                ((TextView) v.findViewById(android.R.id.text1)).setText(entry.getTitle());
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void openAbout() {
        Answers.getInstance().logCustom(new CustomEvent("Open About"));
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void openInquiry() {
        Answers.getInstance().logCustom(new CustomEvent("Open Inquiry"));

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:cutmailapp@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[地震・災害情報] お問い合わせ");
            intent.putExtra(Intent.EXTRA_TEXT, "こちらにお問い合わせ内容をご記入ください。");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Timber.e(e, e.getMessage());
        }
    }

    private void openPlayStore() {
        Answers.getInstance().logCustom(new CustomEvent("Open PlayStore"));

        try {
            Uri uri = Uri.parse("market://details?id=me.cutmail.disasterapp");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Timber.e(e, e.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Entry entry = adapter.getItem(position);
        Intent intent = EntryDetailActivity.createIntent(this, entry.getTitle(), entry.getUrl());
        startActivity(intent);
    }
}
