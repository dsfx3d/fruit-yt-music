package dope.apps.dsfx3d.fruit;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import dope.apps.dsfx3d.fruit.customUI.FontFitEditText;
import dope.apps.dsfx3d.fruit.entities.YTD.YTDResult;
import dope.apps.dsfx3d.fruit.entities.track.Data;
import dope.apps.dsfx3d.fruit.entities.track.MGTrack;
import dope.apps.dsfx3d.fruit.network.NetworkPipeline;
import dope.apps.dsfx3d.fruit.network.RequestHandler;
import dope.apps.dsfx3d.fruit.utils.UpdateManager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Rebound Api
    private Spring reboundSpring;

    //UI
    private RelativeLayout loadingIndicatorView, starLayout;
    private AVLoadingIndicatorView pacmanIndicatorView;
    private FontFitEditText searchBar;
    private Button searchButton;
    private TextView footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getLayoutElements();
        addLayoutListeners();
        UpdateManager.check4Update(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                searchBtnClicked();
                break;
        }
    }

    private void init() {
        reboundSpring = SpringSystem.create().createSpring();
        reboundSpring.addListener(springListener);
    }

    private void getLayoutElements() {
        loadingIndicatorView = (RelativeLayout) findViewById(R.id.loading_indicator);
        searchBar = (FontFitEditText) findViewById(R.id.search_bar);
        searchButton = (Button) findViewById(R.id.search_btn);
        pacmanIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.pacman);
        starLayout = (RelativeLayout) findViewById(R.id.star_layout);
        footerView = (TextView) findViewById(R.id.footer);
    }

    private void addLayoutListeners() {
        searchButton.setOnClickListener(this);
        searchBar.setOnEditorActionListener(searchBarIMEListener);
    }

    private void searchBtnClicked() {
        String searchQuery = searchBar.getText().toString();
        if(searchQuery.length()<2) return;

        reboundSpring.setVelocity(5);

        if(loadingIndicatorView.getVisibility()==View.GONE) {
            Log.v("__searchBtnClicked","Request query: "+searchQuery);
            startSearchingUI();
            startSearchingYTD(searchQuery);
        } else {
            stopSearchingUI();
            stopSearchingYTD();
        }
    }

    /*Changes in UI when search operation begins*/
    private void startSearchingUI() {
        loadingIndicatorView.setVisibility(View.VISIBLE);
        pacmanIndicatorView.setVisibility(View.VISIBLE);
        searchBar.setEnabled(false);
        searchButton.setBackgroundColor(getResources().getColor(R.color.t_black));
        searchButton.setText(getString(R.string.stop_sniffing));
        starLayout.setVisibility(View.GONE);
        footerView.setVisibility(View.GONE);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    /*Changes in UI when search operation ends*/
    private void stopSearchingUI() {
        loadingIndicatorView.setVisibility(View.GONE);
        pacmanIndicatorView.setVisibility(View.INVISIBLE);
        searchBar.setEnabled(true);
        searchButton.setBackgroundColor(getResources().getColor(R.color.even_darker_chat_green));
        searchButton.setText(getString(R.string.sniff_internet));
        starLayout.setVisibility(View.VISIBLE);
        footerView.setVisibility(View.VISIBLE);
    }

    private void startSearchingYTD(String searchQuery) {
        Request request = RequestHandler.getYTDSearchRequest(searchQuery, searchYTDRequestListener, requestErrorListener);
        request.setTag(SEARCH_YTD);
        NetworkPipeline.getInstance(this).addToRequestQueue(request);
    }

    private void stopSearchingYTD() {
        NetworkPipeline.getInstance(this).getRequestQueue().cancelAll(SEARCH_YTD);
    }

    /*For future use with bezerk mode*/
    private void startSearchingMusicGraph(String searchQuery) {
        Request request = RequestHandler.getTrackSearchRequest(searchQuery, searchTrackRequestListener, requestErrorListener);
        request.setTag(SEARCH_TRACK);
        NetworkPipeline.getInstance(this).addToRequestQueue(request);
    }
    /*For future use with bezerk mode*/
    private void stopSearchingMusicGraph() {
        NetworkPipeline.getInstance(this).getRequestQueue().cancelAll(SEARCH_TRACK);
    }

    private void downloadMpeg(URL url) {
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.toString()));
        request.setTitle("Downloading..");
        request.setDescription(searchBar.getText().toString());
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, searchBar.getText().toString()+".mp3");
        manager.enqueue(request);
    }

    private void showDownloadFromBrowserDialog(final URL url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Well this is embarrassing..")
                .setMessage(R.string.third_party_dialog_msg)
                .setPositiveButton("Open browser to download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())));
                    }
                });
        builder.show();
    }

    public void showDownloadUpdateDialog(String message, final String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Updates Available")
                .setMessage(message)
                .setPositiveButton("Download Updates", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)));
                    }
                });
        builder.show();
    }

/** Layout Listeners **//** Layout Listeners **//** Layout Listeners **//** Layout Listeners **//** Layout Listeners **//** Layout Listeners **//** Layout Listeners **/
    private SimpleSpringListener springListener = new SimpleSpringListener() {
        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = 1f - (value * 0.5f);
            searchButton.setScaleX(scale);
            searchButton.setScaleY(scale);
        }

    };


    private TextView.OnEditorActionListener searchBarIMEListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    searchBtnClicked();
            }
            return false;
        }
    };

/** Volley Request Listeners **//** Volley Request Listeners **//** Volley Request Listeners **//** Volley Request Listeners **//** Volley Request Listeners **//** Volley Request Listeners **//** Volley Request Listeners **/
    private String SEARCH_YTD="ytd_tag";
    private String SEARCH_TRACK="track_tag";

    private Response.Listener<String> searchTrackRequestListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.v("__searchTrackRequest",response);
            stopSearchingMusicGraph();
            Gson gson = new Gson();
            MGTrack trackMin = gson.fromJson(response, MGTrack.class);
            Data[] data = trackMin.data;
            if(trackMin.status.code==0 && trackMin.pagination.count>0) {
                int popIndex=0;
                for (int i=0;i<data.length;i++) {
                    Log.v("__searchTrackRequest", data[i].artist_name + " - " + data[i].title + " - " + data[i].popularity);
                    if(data[i].popularity>data[popIndex].popularity) popIndex=i;
                }
                searchBar.setText(trackMin.data[popIndex].title.toLowerCase()+" - "+trackMin.data[popIndex].artist_name.toLowerCase());
            }
        }
    };

    private Response.Listener<String> searchYTDRequestListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.v("__searchYTDReqListener",response);
            Gson gson = new Gson();
            YTDResult ytdResult = gson.fromJson(response, YTDResult.class);
            final YTDResult.Data data = ytdResult.data[0];

            try {
                new FindUrlMimeTypeTask(new URL(data.link)).execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            stopSearchingMusicGraph();
            error.printStackTrace();
        }
    };
/** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **//** **/

    class FindUrlMimeTypeTask extends AsyncTask<Void,Void,String> {
        URL url;
        public FindUrlMimeTypeTask(URL url) {
            this.url=url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String contentType="";
            try {
                URLConnection urlConnection = url.openConnection();
                contentType = urlConnection.getContentType();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return contentType;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("__downloadUrl", "content-type: "+s);
            stopSearchingUI();
            switch (s) {
                case "audio/mpeg":  downloadMpeg(url);  break;
                default:    showDownloadFromBrowserDialog(url); break;
            }
        }
    }
}
