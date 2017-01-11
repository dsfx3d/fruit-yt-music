package dope.apps.dsfx3d.fruit.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 */

public class RequestHandler {

    public static StringRequest getTrackSearchRequest(String trackPrefix, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ApiEndpoint.SEARCH_TRACK+trackPrefix.replace(" ","+")+"&limit=10&fields=title,artist_name,popularity";
        Log.v("__getTrackRequest","request url: "+url);
        return new StringRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static StringRequest getArtistSearchRequest(String trackPrefix, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ApiEndpoint.SEARCH_ARTIST+trackPrefix.replace(" ","+")+"&limit=10&fields=artist_name,popularity";
        Log.v("__getTrackRequest","request url: "+url);
        return new StringRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static StringRequest getYTDSearchRequest(String prefix, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ApiEndpoint.YTD_BASE+prefix.replace(" ","+");
        Log.v("__getYTDSearchReq", "request_url: "+url);
        return  new StringRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header = new HashMap<>();
                header.put("X-Mashape-Key","1p84VvzFtWmshrkAe22rHq6CwIRzp1PvBFWjsnaZ8sNPfhBfLG");
                return header;
            }
        };
    }

    public static StringRequest getYTDDownloadRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Log.v("__getYTDDownloadReq","request_url: "+url);
        return  new StringRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header = new HashMap<>();
                header.put("X-Mashape-Key","1p84VvzFtWmshrkAe22rHq6CwIRzp1PvBFWjsnaZ8sNPfhBfLG");
                return header;
            }
        };
    }
}
