package com.glacion.githubsearcher;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * A singleton which wraps a Volley request queue.
 */
class VolleyCore {
    private static VolleyCore core;
    private RequestQueue requestQueue;
    private final File cacheDir;

    /**
     * Since this is a singleton, only constructor is private.
     */
    private VolleyCore(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    private RequestQueue makeRequestQueue() {
        Cache cache = new DiskBasedCache(cacheDir, 1024*1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        return requestQueue;
    }
    /**
     * Retrieves the RequestQueue, creates it if it is absent.
     * @return The request queue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = makeRequestQueue();
        return requestQueue;
    }

    /**
     * Retrieves the core, creates it if it is absent.
     * @return The Volley core
     */
    public static synchronized VolleyCore getCore(File cacheDir){
        if (core == null) core = new VolleyCore(cacheDir);
        return core;
    }
}
