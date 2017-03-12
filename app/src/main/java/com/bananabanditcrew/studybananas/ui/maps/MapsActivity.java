package com.bananabanditcrew.studybananas.ui.maps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bananabanditcrew.studybananas.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // As far as I know this method is useless so I will leave it commented for now
        mMap = googleMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
