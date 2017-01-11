package dope.apps.dsfx3d.fruit.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 */

public class NetworkPipeline {
    private static NetworkPipeline networkPipeline;
    private RequestQueue requestQueue;
    private static Context context;

    private NetworkPipeline(Context context) {
        NetworkPipeline.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkPipeline getInstance(Context context) {
        if (networkPipeline==null) {
            networkPipeline = new NetworkPipeline(context);
        }
        return networkPipeline;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue==null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
