package com.glacion.githubsearcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.glacion.githubsearcher.recycler.ItemOffset;
import com.glacion.githubsearcher.recycler.Repo;
import com.glacion.githubsearcher.recycler.RepoAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<Repo> repoList;
    private TextView inputField;
    private ProgressBar progressBar;
    private boolean reverseChecked = false;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = VolleyCore.getCore(getApplicationContext().getCacheDir()).getRequestQueue();
        inputField = findViewById(R.id.query_input);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.repo_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemOffset offset = new ItemOffset(this, R.dimen.side_margin);
        recyclerView.addItemDecoration(offset);
        preferences = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    // Menu related stuff.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem sortMenu = menu.findItem(R.id.sort_list);
        getMenuInflater().inflate(R.menu.submenu_sort, sortMenu.getSubMenu());
        Menu sortSubMenu = sortMenu.getSubMenu();
        String sorted = preferences.getString(getString(R.string.repo_sort_key), getString(R.string.stars));
        if (sorted.equals(getString(R.string.stars)))
            sortSubMenu.findItem(R.id.repo_sort_stars).setChecked(true);
        else if (sorted.equals(getString(R.string.forks)))
            sortSubMenu.findItem(R.id.repo_sort_forks).setChecked(true);
        else if (sorted.equals(getString(R.string.updated)))
            sortSubMenu.findItem(R.id.repo_sort_updated).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_button:
                search();
                return true;
            case R.id.reverse_check:
                item.setChecked(!reverseChecked);
                reverseChecked = !reverseChecked;
                return true;
            case R.id.repo_sort_forks:
                onSortSelect(item, getString(R.string.forks));
                return true;
            case R.id.repo_sort_stars:
                onSortSelect(item, getString(R.string.stars));
                return true;
            case R.id.repo_sort_updated:
                onSortSelect(item, getString(R.string.updated));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSortSelect(MenuItem item, String selection) {
        editor = preferences.edit();
        item.setChecked(true);
        editor.putString(getString(R.string.repo_sort_key), selection);
        editor.apply();
    }
    private void search() {
        String query = inputField.getText().toString();
        if (query.equals(""))
            Toast.makeText(getApplicationContext(), "Empty query", Toast.LENGTH_SHORT).show();
        else if (!isNetworkAvailable())
            Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            // The list will be created from scratch to avoid the same list being used by
            // multiple queries.
            repoList = new LinkedList<>();
            String url = NetworkUtils.buildURL(inputField.getText().toString());
            JsonObjectRequest request = makeRequest(url);
            requestQueue.add(request);
        }
    }
    /**
     * Parses and prepares the json object returned from the background task.
     * @param response A Json Object retrieved from the operation in the background.
     */
    private void onSuccess(JSONObject response) {
        try {
            repoList = NetworkUtils.parseJson(response);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(),
                    "Malformed data, please try again",
                    Toast.LENGTH_SHORT).show();
        }
        recyclerView.setAdapter(new RepoAdapter(repoList));
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Forges a Json request for volley with the given url but does not call it.
     * Success action is defined in the onSuccess method
     * @param url URL to be queried
     * @return A JsonObjectRequest to be used for retrieving data.
     */
    private JsonObjectRequest makeRequest(String url){
        // Call the onSuccess method with the resulted JSONObject when completed.
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onSuccess(response);
            }
        };

        // Display a toast on failure.
        Response.ErrorListener errorListener =  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        };

        return new JsonObjectRequest
                (Request.Method.GET, url, null, responseListener, errorListener);
    }

    /**
     * Checks if the device is connected to a network
     * @return True if the device is connected.
     */
    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = null;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        editor.commit();
        super.onDestroy();
    }
}
