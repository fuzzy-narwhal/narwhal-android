package com.urbaniteboston.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ListActivity
{
    public static final String TAG = "UB";
    public static final String ID_EXTRA = "com.urbaniteboston.app.posts_detail";
    private HttpClient client;
    ArrayAdapter<String> postAdapter;
    ArrayList<String> postArray;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        client = new DefaultHttpClient();

        ListView noteListView = getListView();

        postArray = new ArrayList<String>();
        postAdapter = new ArrayAdapter<String>(this, R.layout.post_layout, postArray);
        setListAdapter(postAdapter);

        /*
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.i(TAG, "Selected note: " + position);

                Intent i = new Intent(getApplicationContext(), FeedbackViewActivity.class);
                i.putExtra(ID_EXTRA, ((TextView)view).getText().toString() );
                startActivity(i);
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.refresh:
                new RefreshPostListTask().execute();
                return true;
            case R.id.preferences:
                startActivity(new Intent(this, EditPreferences.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String refreshNoteList() {
        String url = "http://urbaniteboston/posts.json";
        HttpGet httpGet = new HttpGet(url);

        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = client.execute(httpGet, responseHandler);
            Log.i(TAG, responseBody);
            return(responseBody);
        } catch( Throwable t ) {
            Log.e(TAG, "refreshNoteList exception",t );
            Toast.makeText(this, "Refresh failed " + t.toString(), Toast.LENGTH_LONG);
            return null;
        }
    }

    public void parseResponse(String response) {
        if( response != null ) {
            try {
                Log.i(TAG, "Parsing JSON");
                postArray.clear();
                JSONArray jsonArray = new JSONArray(response);
                Log.i(TAG, "Parsed " + jsonArray.length() + " entries");
                for( int i=0; i<jsonArray.length(); i++ ) {
                    JSONObject noteRecord = jsonArray.getJSONObject(i);
                    JSONObject note = noteRecord.getJSONObject("note");
                    String noteDescription = note.getString("description");
                    Log.i(TAG, noteDescription );
                    postArray.add(noteDescription);
                }

                postAdapter.notifyDataSetChanged();
            } catch( JSONException j ) {
                Log.e(TAG, "JSON Exception", j );
                Toast.makeText(this, "JSON Parsing error " + j.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    private class RefreshPostListTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(Void... voids) {

            return refreshNoteList();
        }

        protected void onPostExecute(String serverResponse) {
            progressDialog.dismiss();
            parseResponse( serverResponse );
        }
    }

}
