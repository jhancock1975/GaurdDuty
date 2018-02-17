package com.kewlala.guard_duty;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by jhancock2010 on 2/3/18.
 */

public class GetQuakeDataAsync extends AsyncTask<String, Void, List<QuakeListItem>> {

    public GetQuakeDataAsync(Activity activity){
        super();
        this.activity = activity;
    }
    private Activity activity;

    @Override
    protected List<QuakeListItem> doInBackground(String... urlQuery) {
        Log.d(EarthquakeActivity.LOG_TAG, "doInBackground::begin");
        return QueryUtils.extractEarthquakes(urlQuery[0]);
    }

    @Override
    protected void onPostExecute(List<QuakeListItem> quakeListItems) {
        super.onPostExecute(quakeListItems);
        Log.d(EarthquakeActivity.LOG_TAG, "onPostExecute::setting up list view");
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) this.activity.findViewById(R.id.list);
        earthquakeListView.setOnItemClickListener(new QuakeClickListener());


        // Create a new {@link ArrayAdapter} of earthquakes
        ArrayAdapter<QuakeListItem> adapter = new QuakeArrayAdapter(this.activity, quakeListItems);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
    }
}
