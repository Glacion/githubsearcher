package com.glacion.githubsearcher;

import android.net.Uri;

import com.glacion.githubsearcher.recycler.Repo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

class NetworkUtils {
    private static final String GITHUB_REPO_URL = "https://api.github.com/search";
    private static final String REPO_PATH = "repositories";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_SORT = "sort";
    private static final String PARAM_ORDER = "order";

    // TODO Allow more options when querying.
    /**
     * Build a URL from the query that searches the github repositories and sorts them by stars.
     * @param query Repo name to be searched.
     * @return A URL that can be queried
     */
    public static String buildURL(String query, String sort) {
        Uri builtUri = Uri.parse(GITHUB_REPO_URL).buildUpon()
                .appendPath(REPO_PATH)
                .appendQueryParameter(PARAM_QUERY, query)
                .appendQueryParameter(PARAM_SORT, sort)
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
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String description = item.getString("description");
            // Ensures that the description isn't null.
            description = description.equals("null") ? "No Description Provided" : description;
            Repo repo = new Repo(
                    item.getString("full_name"),
                    description,
                    item.getString("html_url"),
                    item.getInt("forks"),
                    item.getInt("stargazers_count"));
            list.add(repo);
        }
        return list;
    }
}