package com.example.android.foodtruckssf.Activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.foodtruckssf.Model.FoodTruckMarker;
import com.example.android.foodtruckssf.Model.JsonConverter;
import com.example.android.foodtruckssf.Model.JsonFoodTruck;
import com.example.android.foodtruckssf.R;
import com.example.android.foodtruckssf.interfaces.OnJsonParseComplete;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ClusterManager.OnClusterItemClickListener<FoodTruckMarker>, View.OnClickListener, OnJsonParseComplete {

    private GoogleMap mMap;
    private ArrayList<JsonFoodTruck> foodTruckArrayList;
    private ArrayList<Marker> markerArrayList;
    private ClusterManager<FoodTruckMarker> mClusterManager;
    private ArrayList<String> ApplicantNames;
    AppCompatAutoCompleteTextView SearchEdit;
    AppCompatButton SearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get Json Data
        new JsonConverter(this).convertJsonToJava();

        // Google API Client Connection
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        // Search Bar Initialization
        SearchButton = (AppCompatButton) findViewById(R.id.maps_search_button);
        SearchEdit = (AppCompatAutoCompleteTextView) findViewById(R.id.maps_search_edit);

        // Set On Click Listener On Search Button
        SearchButton.setOnClickListener(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initialize Maps, MarkerList, ClusterManager
        mMap = googleMap;
        markerArrayList = new ArrayList<>();
        mClusterManager = new ClusterManager<FoodTruckMarker>(this, mMap);

        // Cluster Manager Event Handlers
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(this);

        // Add a marker in San fransisco and move the camera
        LatLng sanFransisco = new LatLng(37.7748713162388, -122.398531708276);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sanFransisco, 16f));
    }

    public void addListOfMarkersToCluster(){
        // Loop Through Food Truck List And Add Markers To The Map
        for (int i = 0; i < foodTruckArrayList.size(); i++){
            try {
                // Ignore First Item
                if(i == 0)
                    continue;
                JsonFoodTruck instance = foodTruckArrayList.get(i);
                double lat = Double.parseDouble(instance.getLatitude());
                double lon = Double.parseDouble(instance.getLongitude());
                LatLng Pos = new LatLng(lat, lon);

                // Add Details To Marker Object And Add Tag For Later Use
                addMarkersToCluster(new FoodTruckMarker(Pos, instance));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        mClusterManager.cluster();
    }

    public void addMarkersToCluster(FoodTruckMarker marker){
        // Add Food Truck Marker To Map
        mClusterManager.addItem(marker);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Display Toast Message To Notify User
        Toast.makeText(this, "Connection To Google Suspended, Please Check Internet Connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Display Toast Message To Notify User
        Toast.makeText(this, "Connection To Google Failed, Please Check Internet Connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // Close Application
        this.finishAffinity();
    }

    @Override
    public boolean onClusterItemClick(FoodTruckMarker marker) {
        return showVendorInfo(marker.getTruck(), false);
    }

    public boolean showVendorInfo(JsonFoodTruck instance, boolean title){
        // On Marker Clicked Display Dialog With Vendors Info
        AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.food_truck_info_window);


        // Initialize TextView To Display Info
        TextView applicantName = (TextView) dialog.findViewById(R.id.info_window_applicant_name);
        TextView applicantAddress = (TextView) dialog.findViewById(R.id.info_window_address);
        TextView serves_info = (TextView) dialog.findViewById(R.id.info_window_serves);
        TextView open_hours = (TextView) dialog.findViewById(R.id.info_window_open_hours);


        // Set Food Truck Info To Dialog Layout
        try{
            applicantName.setText("Vendor Name : " + instance.getApplicant());
            applicantAddress.setText("Address : " + instance.getAddress());
            open_hours.setText("Open Hours : " + instance.getDayshours());

            // Prepare String
            serves_info.setText("Serves : " + instance.getFooditems());

            // Change Dialog Window Manager Parameters
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            // Show Dialog
            dialog.show();

            // Turn On Vibrator (For 100 Milliseconds)
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            // Vibrate for 100 milliseconds
            v.vibrate(100);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return !title;
    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.maps_search_button)){
            if(SearchButton.getText().equals("Clear")){
                // Clear Filters And Show All Food Trucks
                mClusterManager.clearItems();
                addListOfMarkersToCluster();
                SearchEdit.setText("");
                SearchButton.setText("FIND");
                return;
            }

            try{
                // User Clicked Search Button
                for(JsonFoodTruck trucks : foodTruckArrayList){
                    // Look for Food Truck Names In Database
                    if(trucks.getApplicant().toLowerCase().equals(SearchEdit.getText().toString().toLowerCase())){

                        // Clear All Objects
                        mClusterManager.clearItems();

                        // Add Food Truck Object
                        LatLng Pos = new LatLng(Double.valueOf(trucks.getLatitude()), Double.valueOf(trucks.getLongitude()));
                        mClusterManager.addItem(new FoodTruckMarker(Pos, trucks));

                        // Animate Camera To Location
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Pos,16f));

                        // Hide Keyboard
                        View view = this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        // Show Vendor Info Window
                        showVendorInfo(trucks, true);

                        // Clear SearchBox
                        SearchEdit.setText("");

                        // Set Find Button Text To Clear
                        SearchButton.setText("Clear");

                        // Set Cluster Data Set
                        mClusterManager.cluster();
                        return;
                    }
                }
            }catch (NullPointerException e){
                // foodTruckArrayList Not Yet Initialized
                Toast.makeText(this, "Still Downloading Data", Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                // Some Other Error
                e.printStackTrace();
            }


            // Use Geocoder API to obtain co ordinates by using Location Name
            Geocoder geocoder = new Geocoder(this);
            try {
                // Get LatLng By Location Name
                List<Address> addresses = geocoder.getFromLocationName(SearchEdit.getText().toString(),1);
                LatLng Pos = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                // Animate Camera To Location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Pos,16f));

                // Hide Keyboard
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                // Clear SearchBox
                SearchEdit.setText("");

            } catch (Exception e) {
                Toast.makeText(this, "Couldn't find any such location", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean OnJsonParseSuccess(ArrayList<JsonFoodTruck> foodTruckArrayList) {
        if(mMap != null){
            this.foodTruckArrayList = foodTruckArrayList;
            ApplicantNames = new ArrayList<>();
            for(JsonFoodTruck truck : foodTruckArrayList){

                // Check If Name Already Exists In Database
                boolean foundEntry = false;
                for(String Names : ApplicantNames){
                    if(Names.equals(truck.getApplicant())){
                        foundEntry = true;
                        break;
                    }
                }
                if(!foundEntry)
                    ApplicantNames.add(truck.getApplicant());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Add Markers Tu Cluster
                    addListOfMarkersToCluster();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, ApplicantNames);
                    //Set the number of characters the user must type before the drop down list is shown
                    SearchEdit.setThreshold(1);
                    //Set the adapter
                    SearchEdit.setAdapter(adapter);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void OnJsonParseFailed(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Error While Getting Data, Please Try Again Later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
