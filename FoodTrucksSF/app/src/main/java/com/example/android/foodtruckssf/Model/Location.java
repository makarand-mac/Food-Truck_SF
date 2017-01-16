package com.example.android.foodtruckssf.Model;
// Created by Makarand Deshpande on 16-Jan-17.


import java.io.Serializable;

public class Location implements Serializable
{
    private String type;

    private String[] coordinates;

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String[] getCoordinates ()
    {
        return coordinates;
    }

    public void setCoordinates (String[] coordinates)
    {
        this.coordinates = coordinates;
    }
}
