package com.glacion.githubsearcher.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.glacion.githubsearcher.recycler.Repo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class NetworkUtils {
    private static final String GITHUB_REPO_URL = "https://api.github.com/search";
    private static final String REPO_PATH = "repositories";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_SORT = "sort";
    private static final String PARAM_ORDER = "order";

    /**
     * Build a URL from the query that searches the github repositories and sorts them by stars.
     * @param query Repo name to be searched.
     * @return A URL that can be queried
     * asc desc
     */
    public static String buildURL(String query, String sort, boolean isIncreasing) {
        // Determine if the order of the list will be in increasing or decreasing order.
        // Defaults to decreasing.
        String order = (isIncreasing) ? "asc" : "desc";
        Uri builtUri = Uri.parse(GITHUB_REPO_URL).buildUpon()
                .appendPath(REPO_PATH)
                .appendQueryParameter(PARAM_QUERY, query)
                .appendQueryParameter(PARAM_SORT, sort)
                .appendQueryParameter(PARAM_ORDER, order)
                .build();
        return builtUri.toString();
    }

    /**
     * Constructs a List of Repo objects from a given JSON object.
     * @param object Object to be parsed.
     * @return A list of all the Objects in the JSON.
     * @throws JSONException When the JSONObject is faulty.
     */
    public static List<Repo> parseJson(JSONObject object) throws JSONException {
        List<Repo> list = new LinkedList<>();
        JSONArray jsonArray = object.getJSONArray("items");
        // Construct a repo item for each item in the JSON.
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            Repo repo = new Repo(
                    item.getString("full_name"),
                    item.getString("description"),
                    item.getString("html_url"),
                    item.getInt("forks"),
                    item.getInt("stargazers_count"));
            list.add(repo);
        }
        return list;
    }
    /**
     * Checks if the device is connected to a network
     * @return True if the device is connected.
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = null;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}