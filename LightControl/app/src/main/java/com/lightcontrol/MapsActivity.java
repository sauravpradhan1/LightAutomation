package com.lightcontrol;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng nepal = new LatLng(28.3949, 84.1240);
        mMap.addMarker(new MarkerOptions().position(nepal).title("Marker in 0,0"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nepal));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(point));

        JSONObject js = new JSONObject();
        try {
            js.put("latitude", point.latitude);
            js.put("longitude", point.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainActivity.sendMessage("location", js,getApplicationContext());
    }
}
