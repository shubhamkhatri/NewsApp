package com.example.android.shubhamnewsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String query = "politics";
    private static final String GUARDIAN_API_REQUEST_URL = "https://content.guardianapis.com/search";
    private static final int STORY_LOADER_ID = 1;
    private NewsAdapter mAdapter;

    @BindView(R.id.empty_view)
    TextView emptyStateTextView;

    @BindView(R.id.loading_indicator)
    View loadingIndicatorView;

    @BindView(R.id.list_view)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News currentNews = mAdapter.getItem(i);
                Uri storyUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, storyUri);
                startActivity(websiteIntent);
            }
        });
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(STORY_LOADER_ID, null, this);
        } else {
            loadingIndicatorView.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.device_offline);
        }
    }

    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(GUARDIAN_API_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minDate = sharedPrefs.getString(getString(R.string.settings_min_date_key), getString(R.string.settings_min_date_default_value));
        String section = sharedPrefs.getString(getString(R.string.settings_select_section_key), getString(R.string.settings_select_section_default_value));
        uriBuilder.appendQueryParameter("api-key", getResources().getString(R.string.guardian_api_key));
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("from-date", minDate);
        uriBuilder.appendQueryParameter("section", section);

        return new NewsGetter(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> stories) {
        loadingIndicatorView.setVisibility(View.GONE);
        emptyStateTextView.setText(R.string.no_news_found);
        mAdapter.clear();

        if (stories != null && !stories.isEmpty()) {
            emptyStateTextView.setVisibility(View.GONE);
            mAdapter.addAll(stories);
        }
        else
            emptyStateTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
