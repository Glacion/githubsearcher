package com.glacion.githubsearcher;

import android.content.Context;
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
import com.glacion.githubsearcher.recycler.Repo;
import com.glacion.githubsearcher.recycler.RepoAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<Repo> repoList;
    private TextView inputField;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = VolleyCore.getCore(getApplicationContext()).getRequestQueue();
        recyclerView = findViewById(R.id.repo_view);
        inputField = findViewById(R.id.query_input);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Menu related stuff.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_button){
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
        return super.onOptionsItemSelected(item);
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
}
