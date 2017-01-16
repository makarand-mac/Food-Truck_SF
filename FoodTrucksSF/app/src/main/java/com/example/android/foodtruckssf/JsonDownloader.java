package com.example.android.foodtruckssf;
// Created by Makarand Deshpande on 16-Jan-17.


import android.os.AsyncTask;
import android.util.Log;

import com.example.android.foodtruckssf.interfaces.OnDownloadComplete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonDownloader extends AsyncTask<String, Void, String> {
    private OnDownloadComplete downloadComplete;
    private String server_response;

    public JsonDownloader(OnDownloadComplete downloadComplete) {
        this.downloadComplete = downloadComplete;
    }

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(urlConnection.getInputStream());
                downloadComplete.OnDownloadSuccess(server_response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            downloadComplete.OnDownloadFailed(e);
        } catch (IOException e) {
            e.printStackTrace();
            downloadComplete.OnDownloadFailed(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("Response", "" + server_response);
    }


    // Converting InputStream to String
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}