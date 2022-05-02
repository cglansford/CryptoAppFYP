package com.example.cryptoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cryptoapp.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setCompassEnabled(true);

        //Get user location
        getLocation();

        //Load map data and creates markers
        getMapData();

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        mMap.setMyLocationEnabled(true);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = new FusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                double lat = locationResult.getLastLocation().getLatitude();
                double lng = locationResult.getLastLocation().getLongitude();
                LatLng currentlocation = new LatLng(lat, lng);
                displayLocation(currentlocation);

            }
        }, null);
    }

    public void displayLocation(LatLng latlng) {
        if (mMap != null) {
            Geocoder coder = new Geocoder(this);
            try {
                List<Address> locations = coder.getFromLocation(latlng.latitude,
                        latlng.longitude, 1);
                if (locations != null) {
                    String add1 = locations.get(0).getAddressLine(0);
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Current location").snippet(add1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //Get locations for locations that accept crypto in Ireland and UK
    private void getMapData(){

        String url ="https://coinmap.org/api/v1/venues/?lon2=2&lon1=-8&lat1=51&lat2=60";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Extract data to List
                try {
                    JSONArray dataArray = response.getJSONArray("venues");
                    for(int i = 0; i< dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);

                        String name = dataObj.getString("name");
                        double lat = dataObj.getDouble("lat");
                        double lng = dataObj.getDouble("lon");
                        String cat = dataObj.getString("category");
                        LatLng test = new LatLng(lat, lng);
                        mMap.addMarker(new MarkerOptions().position(test).title(name).snippet(cat));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Failed to extract json data..", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            //To handle possible errors
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MapsActivity.this, "Failed to get the data..", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id==R.id.backButton){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        else if(id==R.id.portfolio){
            Intent intent = new Intent(this, Portfolio.class);
            startActivity(intent);
        }

        else if (id==R.id.news){
            Intent intent = new Intent(this, News.class);
            startActivity(intent);
        }

        else if(id==R.id.forum){
            Intent intent = new Intent(this, Forum.class);
            startActivity(intent);
        }

        else if(id == R.id.maps){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.fav){
            Intent intent = new Intent(this, FavList.class);
            startActivity(intent);
        }
        else if(id ==R.id.logout){

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
}