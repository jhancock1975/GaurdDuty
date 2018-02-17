package com.kewlala.guard_duty;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhancock2010 on 1/27/18.
 */

class QuakeArrayAdapter extends ArrayAdapter<QuakeListItem> {


    public QuakeArrayAdapter(Activity context, List<QuakeListItem> earthquakes) {
        super(context, 0, earthquakes);
        this.earthquakes = earthquakes;
    }

    private static DateFormat listItemDateFormat = new SimpleDateFormat("MMM dd, yyyy ");
    private static DateFormat listItemTimeFormat = new SimpleDateFormat("HH:mm:ss a");
    private static DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
    List<QuakeListItem> earthquakes;
    public static final String LOG_TAG = QuakeArrayAdapter.class.getName();

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.quake_list_item, parent, false);
        }


        QuakeListItem quakeListItem = getItem(position);

        // Find the TextViews and set their text
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.quake_list_magnitude);
        magnitudeTextView.setText(magnitudeFormat.format(quakeListItem.getMagnitude()));

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(quakeListItem.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(ContextCompat.getColor(getContext(), magnitudeColor));


        TextView locationDirectionTextView =
                (TextView) listItemView.findViewById(R.id.quake_list_location_direction);
        TextView locationPlaceTextView =
                (TextView) listItemView.findViewById(R.id.quake_list_location_place_name);


        try {
            String[] locationSplit = quakeListItem.getLocation()
                    .split(getContext().getString(R.string.location_seperator));

            for (String s: locationSplit){
                Log.d(LOG_TAG, "QuakeArrayAdapter::getView location" +
                        "split element = " + s + ".");

            }
            Log.d(LOG_TAG, "***");


            locationDirectionTextView.setText(locationSplit[0].trim()
                    + " " + getContext().getString(R.string.location_seperator));
            locationPlaceTextView.setText(locationSplit[1].trim());

        } catch (ArrayIndexOutOfBoundsException e) {
            locationDirectionTextView.setText(quakeListItem.getLocation());
        }

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.quake_list_date);
        dateTextView.setText(listItemDateFormat.format(quakeListItem.getDate()));

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.quake_list_time);
        timeTextView.setText(listItemTimeFormat.format(quakeListItem.getDate()));

        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    }


    private int getMagnitudeColor(double magnitude) {
        int magnitude_trunc = (int)magnitude;
        switch(magnitude_trunc){
            case 1:
                return R.color.magnitude1;
            case 2:
                return R.color.magnitude2;
            case 3:
                return R.color.magnitude3;
            case 4:
                return R.color.magnitude4;
            case 5:
                return R.color.magnitude5;
            case 6:
                return R.color.magnitude6;
            case 7:
                return R.color.magnitude7;
            case 8:
                return R.color.magnitude8;
            case 9:
                return R.color.magnitude9;
            default:
                return R.color.magnitude10plus;
        }
    }

    public void setEarthquakes(List<QuakeListItem> earthquakes){
        if (earthquakes != null && earthquakes.size() > 0 && this.earthquakes != null) {
            this.earthquakes.clear();
            this.earthquakes.addAll(earthquakes);
            notifyDataSetChanged();
        }
    }
}
