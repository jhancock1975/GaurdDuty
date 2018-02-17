/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kewlala.guard_duty;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<QuakeListItem>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    public static final String BASE_URL = "https://earthquake.usgs.gov";
    public static final String PATH = "/fdsnws/event/1/query";

    private QuakeArrayAdapter adapter;
    private ListView earthquakeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate::starting up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setOnItemClickListener(new QuakeClickListener());

        

        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new QuakeArrayAdapter(this, new ArrayList<QuakeListItem>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        
        //check network status if network is available, show progress bar
        //and load earthquakes, otherwise show message that network is not available
        if (connected()) {
            //get the actual list of earthquake data in the background
            //using async task loader
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EarthquakeLoader.EARTHQUAKE_LOADER_ID, null,
                    this);
        } else {
            TextView notConnected = (TextView) findViewById(R.id.text_not_connected);
            earthquakeListView.setEmptyView(notConnected);
            notConnected.setVisibility(View.VISIBLE);
        }
    }

    private boolean connected() {
        Log.d(LOG_TAG, "checking network connection...");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        Log.d(LOG_TAG, "network info = " + networkInfo);
        Log.d(LOG_TAG, "networkInfo != null && networkInfo.isConnected() == "
                + (networkInfo != null && networkInfo.isConnected()));

        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<QuakeListItem>> onCreateLoader(int i, Bundle bundle) {
        Log.d(EarthquakeActivity.LOG_TAG, "oncreateLoader::begin");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));



        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String maxResults = sharedPrefs.getString(
                getString(R.string.settings_max_results_key),
                getString(R.string.settings_max_results_default));



        Uri baseUri = Uri.parse(EarthquakeActivity.BASE_URL + PATH);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", maxResults);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);


        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<QuakeListItem>> loader,
                               List<QuakeListItem> quakeListItems) {
        Log.d(EarthquakeActivity.LOG_TAG, "onLoadFinished begin");

        //hide the progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        if (connected()) {

            TextView notConnected = (TextView) findViewById(R.id.text_not_connected);
            notConnected.setVisibility(View.GONE);

            TextView empty = (TextView) findViewById(R.id.empty);
            earthquakeListView.setEmptyView(empty);
            empty.setVisibility(View.VISIBLE);
            adapter.clear();

            adapter.setEarthquakes(quakeListItems);
        } else {
            TextView notConnected = (TextView) findViewById(R.id.text_not_connected);
            earthquakeListView.setEmptyView(notConnected);
            notConnected.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<QuakeListItem>> loader) {
        Log.d(EarthquakeActivity.LOG_TAG, "onLoaderRest begin");
        adapter.setEarthquakes(QueryUtils.getInitialList());
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
