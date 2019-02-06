package me.cutmail.disasterapp.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import hotchemi.android.rate.AppRate;
import io.fabric.sdk.android.Fabric;
import me.cutmail.disasterapp.R;
import me.cutmail.disasterapp.model.Entry;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.paging_recycler)
    RecyclerView recyclerView;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("entries");

        setupLayout();

        Fabric.with(this, new Crashlytics());
        setupRateDialog();

        AppRate.showRateDialogIfMeetsConditions(this);
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
        Query query = collectionReference.orderBy("title");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<Entry> options = new FirestorePagingOptions.Builder<Entry>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Entry.class)
                .build();

        FirestorePagingAdapter<Entry, ItemViewHolder> adapter = new FirestorePagingAdapter<Entry, ItemViewHolder>(options) {
            @Override protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Entry entry) {
                holder.bind(entry);
            }

            @NonNull @Override public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
                return new ItemViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//
//        FirebaseListOptions<Entry> options = new FirebaseListOptions.Builder<Entry>()
//                .setQuery(query, Entry.class)
//                .setLayout(android.R.layout.simple_list_item_1)
//                .build();
//
//        adapter = new FirebaseListAdapter<Entry>(options) {
//            @Override protected void populateView(View v, Entry entry, int position) {
//                ((TextView) v.findViewById(android.R.id.text1)).setText(entry.getTitle());
//            }
//        };
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(this);
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
            Timber.e(e);
        }
    }

    private void openPlayStore() {
        Answers.getInstance().logCustom(new CustomEvent("Open PlayStore"));

        try {
            Uri uri = Uri.parse("market://details?id=me.cutmail.disasterapp");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (adapter != null) {
//            Entry entry = adapter.getItem(position);
//            Intent intent = EntryDetailActivity.createIntent(this, entry.getTitle(), entry.getUrl());
//            startActivity(intent);
//        }
//    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        TextView mTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Entry entry) {
            mTextView.setText(entry.getTitle());
        }
    }
}
