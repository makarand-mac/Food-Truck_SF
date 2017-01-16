package com.example.android.foodtruckssf;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.example.android.foodtruckssf.Activities.MapsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton findFoodTrucks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Food Truck Button Initialization
        findFoodTrucks = (AppCompatButton) findViewById(R.id.find_food_truck);
        findFoodTrucks.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.find_food_truck)){

            // Display Maps Activity
             startActivity(new Intent(this, MapsActivity.class));
        }
    }
}
