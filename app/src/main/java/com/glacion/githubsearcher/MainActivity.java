package com.glacion.githubsearcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.glacion.githubsearcher.util.NetworkUtils;
import com.glacion.githubsearcher.util.PreferenceHandler;
import com.glacion.githubsearcher.util.VolleyCore;

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
    private PreferenceHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = VolleyCore.getCore(getApplicationContext().getCacheDir()).getRequestQueue();
        inputField = findViewById(R.id.query_input);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.repo_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler = new PreferenceHandler(this);
    }

    // Menu related stuff.
    // Todo clarify submenu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem subMenu = menu.findItem(R.id.sort_list); // Submenu for sort elements.
        getMenuInflater().inflate(R.menu.submenu_sort, subMenu.getSubMenu());
        MenuItem checkBox = menu.findItem(R.id.reverse_check);
        checkBox.setChecked(handler.getReverse()); // Check reverse box if necessary.
        Menu sortMenu = subMenu.getSubMenu();
        String sortParam = handler.getSort();
        switch (sortParam) {
            case "stars":
                sortMenu.findItem(R.id.repo_sort_stars).setChecked(true);
                break;
            case "forks":
                sortMenu.findItem(R.id.repo_sort_forks).setChecked(true);
                break;
            default:
                sortMenu.findItem(R.id.repo_sort_updated).setChecked(true);
                break;
        }
        return true;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_button:
                search();
                break;
            case R.id.repo_sort_forks:
                handler.setSort("forks");
                item.setChecked(true);
                break;
            case R.id.repo_sort_updated:
                handler.setSort("updated");
                item.setChecked(true);
                break;
            case R.id.repo_sort_stars:
                handler.setSort("stars");
                item.setChecked(true);
                break;
            case R.id.reverse_check:
                handler.setReverse(!item.isChecked());
                item.setChecked(!item.isChecked());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search() {
        String query = inputField.getText().toString();
        String sort = handler.getSort();
        boolean isIncreasing = handler.getReverse();
        if (query.equals(""))
            Toast.makeText(getApplicationContext(), "Empty query", Toast.LENGTH_SHORT).show();
        else if (!NetworkUtils.isNetworkAvailable(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            // The list will be created from scratch to avoid the same list being used by
            // multiple queries.
            repoList = new LinkedList<>();
            String url = NetworkUtils.buildURL(query, sort, isIncreasing);
            Log.d(getLocalClassName(), url);
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

}
