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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
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

    @BindView(R.id.paging_loading)
    ProgressBar progressBar;

    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                        .inflate(R.layout.entry_list_item, parent, false);
                return new ItemViewHolder(view, (title, url) -> openEntry(title, url));
            }

            @Override protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case LOADED:
                    case FINISHED:
                        progressBar.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        break;
                }
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void openEntry(String title, String url) {
        Intent intent = EntryDetailActivity.createIntent(this, title, url);
        startActivity(intent);
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

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        public interface OnEntryClickListener {
            void onItemClick(String title, String url);
        }

        @BindView(R.id.container)
        View mView;

        @BindView(R.id.title)
        TextView mTextView;

        private final OnEntryClickListener listener;

        ItemViewHolder(View itemView, OnEntryClickListener listener) {
            super(itemView);
            this.listener = listener;
            ButterKnife.bind(this, itemView);
        }

        void bind(final Entry entry) {
            mTextView.setText(entry.getTitle());
            mView.setOnClickListener(v -> listener.onItemClick(entry.getTitle(), entry.getUrl()));
        }
    }
}
