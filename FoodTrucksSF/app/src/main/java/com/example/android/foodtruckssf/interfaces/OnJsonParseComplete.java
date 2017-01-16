package com.example.android.foodtruckssf.interfaces;
// Created by Makarand Deshpande on 16-Jan-17.


import com.example.android.foodtruckssf.Model.JsonFoodTruck;

import java.util.ArrayList;

public interface OnJsonParseComplete {
    boolean OnJsonParseSuccess(ArrayList<JsonFoodTruck> foodTruckArrayList);
    void OnJsonParseFailed(Exception e);
}
