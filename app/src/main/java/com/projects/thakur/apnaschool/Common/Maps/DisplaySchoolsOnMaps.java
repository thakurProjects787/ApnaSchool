package com.projects.thakur.apnaschool.Common.Maps;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.R;

public class DisplaySchoolsOnMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String schoolsDetails,mapStatus;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_schools_on_maps);

        mapStatus = getIntent().getStringExtra("EXTRA_SCHOOL_ON_MAP_SESSION_ID");

        schoolsDetails =  new Logger().getDataFromlocationFile("locations.txt",context);

        if(schoolsDetails.isEmpty()){
            finish();
        }


        // format
        //  lat + "#" + lng + "#" + name + "#" + mapDisplayLine + "%";
        //schoolsDetails = "28.387437" + "#" + "76.963223" + "#" + "Dummy" + "#" + "Others details" + "%";


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

        String[] allDevice = schoolsDetails.split("%");
        for (String eachDevice: allDevice) {
            if(!eachDevice.isEmpty()) {

                Double latitude = Double.parseDouble(eachDevice.split("#")[0]);
                Double longitude = Double.parseDouble(eachDevice.split("#")[1]);
                String title=eachDevice.split("#")[2];
                String details=eachDevice.split("#")[3];

                Marker deviceOnMap= mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(details));
                deviceOnMap.showInfoWindow();

            }
        }//for

        //Animating the camera
        LatLng hp = new LatLng(31.1048, 77.1734);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hp));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
    }
}
