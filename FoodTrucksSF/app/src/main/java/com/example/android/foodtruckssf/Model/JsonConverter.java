package com.example.android.foodtruckssf.Model;
// Created by Makarand Deshpande on 16-Jan-17.


import com.example.android.foodtruckssf.JsonDownloader;
import com.example.android.foodtruckssf.interfaces.OnDownloadComplete;
import com.example.android.foodtruckssf.interfaces.OnJsonParseComplete;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class JsonConverter implements OnDownloadComplete {
    private String JSONFileURL = "https://data.sfgov.org/resource/6a9r-agq8.json";
    private OnJsonParseComplete onJsonParseComplete;

    public JsonConverter(OnJsonParseComplete onJsonParseComplete) {
        this.onJsonParseComplete = onJsonParseComplete;
    }

    public void convertJsonToJava(){
        new JsonDownloader(this).execute(JSONFileURL);
    }

    @Override
    public boolean OnDownloadSuccess(String Response) {
        try{
            Gson gson = new Gson();
            JsonFoodTruck[] wrapper = gson.fromJson(Response, JsonFoodTruck[].class);
            ArrayList<JsonFoodTruck> foodTrucks = new ArrayList<JsonFoodTruck>(Arrays.asList(wrapper));
            return onJsonParseComplete.OnJsonParseSuccess(foodTrucks);
        }catch (Exception e){
            e.printStackTrace();
            onJsonParseComplete.OnJsonParseFailed(e);
        }
        return false;
    }

    @Override
    public void OnDownloadFailed(Exception e) {
        onJsonParseComplete.OnJsonParseFailed(e);
    }
}
