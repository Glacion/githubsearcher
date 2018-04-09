package com.glacion.githubsearcher;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * A singleton which wraps a Volley request queue.
 */
@SuppressLint("StaticFieldLeak")
class VolleyCore {
    private static VolleyCore core;
    private RequestQueue requestQueue;
    private static Context coreContext;

    /**
     * Since this is a singleton, only constructor is private.
     * @param context Required for Volley's caching.
     */
    private VolleyCore(Context context) {
        coreContext = context;
    }

    /**
     * Retrieves the RequestQueue, creates it if it is absent.
     * @return The request queue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(coreContext.getApplicationContext());
        return requestQueue;
    }

    /**
     * Retrieves the core, creates it if it is absent.
     * @return The Volley core
     */
    public static synchronized VolleyCore getCore(Context context){
        if (core == null) core = new VolleyCore(context);
        return core;
    }
}
