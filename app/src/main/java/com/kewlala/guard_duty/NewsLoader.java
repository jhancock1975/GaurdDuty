package com.kewlala.guard_duty;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.List;



/**
 * Created by jhancock2010 on 2/6/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsListItem>> {
    public static final int EARTHQUAKE_LOADER_ID=1;
    public NewsLoader(@NonNull Context context, String urlQuery) {
        super(context);
        this.urlQuery = urlQuery;
    }

    private String urlQuery;

    @Nullable
    @Override
    public List<NewsListItem> loadInBackground() {
        Log.d(NewsActivity.LOG_TAG, "doInBackground::begin");
        return QueryUtils.getNews(this.urlQuery);
    }

    public String getUrlQuery() {
        return urlQuery;
    }

    public void setUrlQuery(String urlQuery) {
        this.urlQuery = urlQuery;
    }

    @Override
    protected void onStartLoading() {
        Log.d(NewsActivity.LOG_TAG, "onStartLoading begin");
        forceLoad();
    }


}