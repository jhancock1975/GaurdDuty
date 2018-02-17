package com.kewlala.guard_duty;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public static final String features_key = "features";
    public static final String place_key = "place";
    public static final String time_key = "time";
    public static final String magnitude_key = "mag";
    public static final String properties_key = "properties";
    public static final String uri_key = "url";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * returns initial one-element list to display
     * while app is fetching earthquake data from USGS
     */
    public static List<QuakeListItem> getInitialList() {
        Log.d(EarthquakeActivity.LOG_TAG, "getInitialList");
        ArrayList<QuakeListItem> list = new ArrayList<QuakeListItem>();
        return list;
    }

    /**
     * Return a list of {@link QuakeListItem} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<QuakeListItem> extractEarthquakes(String urlQuery) {

        Log.d(EarthquakeActivity.LOG_TAG, "extractEarthQuakes:: starting");

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<QuakeListItem> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            URL url = createUrl(urlQuery);

            JSONObject quakeObj = new JSONObject(makeHttpRequest(url));
            JSONArray featuresArr = quakeObj.getJSONArray(features_key);
            int len = featuresArr.length();
            for (int i = 0; i < len; i++) {
                JSONObject curFeature = featuresArr.getJSONObject(i);
                JSONObject curProperties = curFeature.getJSONObject(properties_key);
                QuakeListItem listItem = new QuakeListItem(curProperties.getDouble(magnitude_key),
                        curProperties.getString(place_key),
                        new Date(curProperties.getLong(time_key)),
                        curProperties.getString(uri_key));
                earthquakes.add(listItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(EarthquakeActivity.LOG_TAG, "Problem parsing the earthquake JSON results",
                    e);
        } catch (IOException e) {
            Log.e(EarthquakeActivity.LOG_TAG, "i/o exception making http request", e);
        }
        // Return the list of earthquakes
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(EarthquakeActivity.LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(EarthquakeActivity.LOG_TAG, "Error response code: "
                        + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(EarthquakeActivity.LOG_TAG, "Problem retrieving the earthquake " +
                    "JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}