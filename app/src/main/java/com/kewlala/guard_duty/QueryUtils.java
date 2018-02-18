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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public static final String response_key = "response";
    public static final String results_key = "results";
    public static final String time_key = "time";
    public static final String title_key = "webTitle";
    public static final String date_published_key = "webPublicationDate";
    public static final String uri_key = "webUrl";
    public static final String fields_key = "fields";
    public static final String author_key = "byline";
    public static final String LOG_TAG = "QueryUtils";

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
    public static List<NewsListItem> getInitialList() {
        Log.d(LOG_TAG, "getInitialList");
        ArrayList<NewsListItem> list = new ArrayList<NewsListItem>();
        return list;
    }

    /**
     * Return a list of {@link NewsListItem} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<NewsListItem> getNews(String urlQuery) {

        Log.d(LOG_TAG, "getNews:: starting");
        Log.d(LOG_TAG, "urlQuery = " + urlQuery);

        // Create an empty ArrayList that we can start adding articles to
        ArrayList<NewsListItem> articles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            URL url = createUrl(urlQuery);

            JSONObject queryResult = new JSONObject(makeHttpRequest(url));
            JSONObject response = queryResult.getJSONObject(response_key);
            JSONArray resultsArr = response.getJSONArray(results_key);
            int len = resultsArr.length();
            for (int i = 0; i < len; i++) {
                JSONObject curArticle = resultsArr.getJSONObject(i);

                Date articleDate=null;
                try {
                    articleDate = parseDate(curArticle.getString(date_published_key));
                } catch (ParseException e){
                    Log.d(LOG_TAG, "unable to parse date: "
                            + curArticle.getString(date_published_key), e);
                }

                NewsListItem listItem = new NewsListItem(curArticle.getString(title_key),
                        curArticle.getString(author_key),
                        articleDate,
                        curArticle.getString(uri_key));
                articles.add(listItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(NewsActivity.LOG_TAG, "Problem parsing the earthquake JSON results",
                    e);
        } catch (IOException e) {
            Log.e(NewsActivity.LOG_TAG, "i/o exception making http request", e);
        }
        // Return the list of articles
        return articles;
    }

    private static SimpleDateFormat articleDateFormat
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static Date parseDate(String string) throws ParseException {
        return articleDateFormat.parse(string);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(NewsActivity.LOG_TAG, "Error with creating URL ", e);
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
                Log.e(NewsActivity.LOG_TAG, "Error response code: "
                        + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(NewsActivity.LOG_TAG, "Problem retrieving the earthquake " +
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