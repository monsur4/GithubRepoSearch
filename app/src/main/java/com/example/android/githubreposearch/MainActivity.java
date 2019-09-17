package com.example.android.githubreposearch;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.github.com/search/repositories";
    private static final String LOG_TAG = "Main Activity logs";
    private EditText searchInput;
    public Button  searchButton;
    private TextView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = findViewById(R.id.editText);
        searchButton = findViewById(R.id.button);
        searchResult = findViewById(R.id.textview);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GithubSearchAsyncTask githubSearchAsyncTask = new GithubSearchAsyncTask();
                githubSearchAsyncTask.execute();
            }
        });
    }

    private URL createUrl() {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("q", searchInput.getText().toString())
                .build();
        URL url;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Could not create URL", exception);
            return null;
        }
        return url;
    }

    private String makeHttpRequest(URL url){
        String jsonResponse = "";
        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + Integer.toString(urlConnection.getResponseCode()));
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private class GithubSearchAsyncTask extends AsyncTask<URL, Void, String>{
        final URL url = createUrl();
        @Override
        protected String doInBackground(URL... urls) {
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (Exception e){
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            JSONObject jsonObject;
            StringBuilder str = new StringBuilder();
            try {
                jsonObject =  new JSONObject(jsonResponse);
                JSONArray jsonArray = jsonObject.getJSONArray("items");

                for (int i=0; i<10; i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    String fullName = item.getString("full_name");
                    String description = item.getString("description");
                    str.append( "(" + (i+1) + ") " + "Full Name: " + fullName + "\n" + "Description: " + description +"\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!jsonResponse.isEmpty()){
                searchResult.setText(str.toString());
            }else{
                searchResult.setText("Something went wrong");
            }
        }
    }
}
