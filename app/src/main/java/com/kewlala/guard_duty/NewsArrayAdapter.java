package com.kewlala.guard_duty;

import android.app.Activity;
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
import java.util.List;

/**
 * Created by jhancock2010 on 1/27/18.
 */

class NewsArrayAdapter extends ArrayAdapter<NewsListItem> {


    public NewsArrayAdapter(Activity context, List<NewsListItem> earthquakes) {
        super(context, 0, earthquakes);
        this.earthquakes = earthquakes;
    }

    private static DateFormat listItemDateFormat = new SimpleDateFormat("MMM dd, yyyy ");
    private static DateFormat listItemTimeFormat = new SimpleDateFormat("HH:mm:ss a");
    private static DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
    List<NewsListItem> earthquakes;
    public static final String LOG_TAG = NewsArrayAdapter.class.getName();

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
                    R.layout.news_list_item, parent, false);
        }


        NewsListItem newsListItem = getItem(position);

        // Find the TextViews and set their text
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.section_name);
        char sectionFirstChar = newsListItem.getSection().toUpperCase().trim().charAt(0);
        magnitudeTextView.setText(sectionFirstChar+"");

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getSubjectColor(sectionFirstChar);

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(ContextCompat.getColor(getContext(), magnitudeColor));


        ((TextView) listItemView.findViewById(R.id.text_view_news_article_title))
                .setText(newsListItem.getTitle());

        ((TextView) listItemView.findViewById(R.id.section_name_full))
                .setText(newsListItem.getSection());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.text_view_article_date);
        dateTextView.setText(listItemDateFormat.format(newsListItem.getDate()));

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.text_view_article_time);
        timeTextView.setText(listItemTimeFormat.format(newsListItem.getDate()));


        TextView authorTextView = (TextView) listItemView.findViewById(R.id.text_view_article_author);
        authorTextView.setText(listItemTimeFormat.format(newsListItem.getDate()));


        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    }

    /**
     * choose color baseed on frequency of first letter of word in the English language.
     * We use the data from:
     * https://en.wikipedia.org/wiki/Letter_frequency#Relative_frequencies_of_the_first_letters_of_a_word_in_the_English_language
     *
     * @param firstLetter first letter of article title
     * @return color of dot next to article
     */
    private int getSubjectColor(char firstLetter) {

        switch(firstLetter){
            case 'T':
                return R.color.magnitude1;
            case 'A':
                return R.color.magnitude2;
            case 'O':
                return R.color.magnitude3;
            case 'I':
                return R.color.magnitude4;
            case 'S':
                return R.color.magnitude5;
            case 'W':
                return R.color.magnitude6;
            case 'C':
                return R.color.magnitude7;
            case 'B':
                return R.color.magnitude8;
            case 'P':
                return R.color.magnitude9;
            default:
                return R.color.magnitude10plus;
        }
    }

    public void setEarthquakes(List<NewsListItem> earthquakes){
        if (earthquakes != null && earthquakes.size() > 0 && this.earthquakes != null) {
            this.earthquakes.clear();
            this.earthquakes.addAll(earthquakes);
            notifyDataSetChanged();
        }
    }
}
