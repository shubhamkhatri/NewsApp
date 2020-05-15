package com.example.android.shubhamnewsapp;

import android.text.TextUtils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class NewsInfo {

    private static final String LOG_TAG = NewsInfo.class.getSimpleName();
    private static final String JSON_STATUS_OK = "ok";
    private NewsInfo() {
    }
    public static List<News> fetchStoryData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<News> stories = extractFeatureFromJson(jsonResponse);
        return stories;
    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000 );
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
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

    private static List<News> extractFeatureFromJson(String storyJSON) {

        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }
       ArrayList<News> stories = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(storyJSON).getJSONObject("response");
            if (!baseJsonResponse.getString("status").equals(JSON_STATUS_OK)) {
                Log.e(LOG_TAG, "Bad status of json response");
                return stories;
            }
            JSONArray storyArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < storyArray.length(); i++) {
                JSONObject currentStory = storyArray.getJSONObject(i);
                String title = currentStory.getString("webTitle");
                String publicationDate = currentStory.getString("webPublicationDate");
                Date date = null;
                try {
                    date  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(publicationDate);
                }
                catch (Exception e){
                    Log.e(LOG_TAG, "Error getting publication date: " + e);
                }
                String storyUrl = currentStory.getString("webUrl");
                String section = currentStory.getString("sectionName");
                String imageUrl = "";
                if (currentStory.has("fields")) {
                    JSONObject fields = currentStory.getJSONObject("fields");
                    if (fields.has("thumbnail"))
                        imageUrl = fields.getString("thumbnail");
                }
                ArrayList<String> category = new ArrayList<>();
                if (currentStory.has("tags")){
                    JSONArray tags = currentStory.getJSONArray("tags");
                    JSONObject tag;
                    for (int j = 0; j < tags.length(); j++){
                        tag = tags.getJSONObject(j);
                        if (tag.has("webTitle"))
                            category.add(tag.getString("webTitle"));
                    }
                }
                News news = new News(title, category, date, storyUrl, section, imageUrl);
                stories.add(news);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the story JSON results", e);
        }
        return stories;
    }

}
