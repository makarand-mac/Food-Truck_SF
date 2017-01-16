package com.example.android.foodtruckssf.Model;
// Created by Makarand Deshpande on 16-Jan-17.


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class FoodTruckMarker implements ClusterItem {
    private LatLng mPosition;
    private JsonFoodTruck truck;

    public FoodTruckMarker(LatLng mPosition, JsonFoodTruck Truck) {
        this.mPosition = mPosition;
        this.truck = Truck;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public JsonFoodTruck getTruck() {
        return truck;
    }
}
