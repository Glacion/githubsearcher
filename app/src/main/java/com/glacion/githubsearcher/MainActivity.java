package com.glacion.githubsearcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
        // Initialize required objects in the creation of the activity.
        requestQueue = VolleyCore.getCore(getApplicationContext().getCacheDir()).getRequestQueue();
        inputField = findViewById(R.id.query_input);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.repo_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler = new PreferenceHandler(this);
        repoList = new LinkedList<>();

        // Enables to press enter on keyboard to perform the search.
        inputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                    return true;
                }
                return false;
            }
        });
    }

    // Menu related stuff.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        Menu subMenu = menu.findItem(R.id.sort_list).getSubMenu(); // Submenu for sort elements.
        getMenuInflater().inflate(R.menu.submenu_sort, subMenu); // Inflate sort submenu.
        MenuItem checkBox = menu.findItem(R.id.reverse_check); // Get reverse checkbox.
        checkBox.setChecked(handler.getReverse()); // Check reverse box if necessary.
        // Check the appropriate sort element.
        switch (handler.getSort()) {
            case "stars":
                subMenu.findItem(R.id.repo_sort_stars).setChecked(true);
                break;
            case "forks":
                subMenu.findItem(R.id.repo_sort_forks).setChecked(true);
                break;
            default:
                subMenu.findItem(R.id.repo_sort_updated).setChecked(true);
                break;
        }
        return true;
    }

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


    /**
     * Perform a search operation with the query entered in the input field, the reverse bit,
     * and the sort parameter. Do not perform a query if there are no queries or if the device isn't
     * connected to the internet .
     */
    private void search() {
        String query = inputField.getText().toString();
        if (query.equals(""))
            Toast.makeText(getApplicationContext(), "Empty query", Toast.LENGTH_SHORT).show();
        else if (!NetworkUtils.isNetworkAvailable(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            // Clear the previous list in order to avoid previous results from showing up.
            repoList.clear();
            // Build url with the query, sort and reverse parameters.
            String url = NetworkUtils.buildURL(query, handler.getSort(), handler.getReverse());
            Log.d(getLocalClassName(), "Performing search request with URL: " + url);
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
            return;
        }
        recyclerView.setAdapter(new RepoAdapter(repoList));
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Forges a Json request for volley with the given url but does not call it.
     * Success action is defined in the onSuccess method above.
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
