package com.example.android.shubhamnewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsGetter extends AsyncTaskLoader<List<News>> {

    private String mUrl;
    public NewsGetter(Context context, String url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<News> stories = NewsInfo.fetchStoryData(mUrl);
        return stories;
    }
}
