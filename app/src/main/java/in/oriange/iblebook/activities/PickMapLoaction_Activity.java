package in.oriange.iblebook.activities;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.AutoCompleteLocation;

public class PickMapLoaction_Activity extends FragmentActivity
        implements OnMapReadyCallback, AutoCompleteLocation.AutoCompleteLocationListener  {


    private GoogleMap mMap;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_loaction);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AutoCompleteLocation autoCompleteLocation =
                (AutoCompleteLocation) findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override public void onTextClear() {
        mMap.clear();
    }

    @Override public void onItemSelected(Place selectedPlace) {
        addMapMarker(selectedPlace.getLatLng());
    }

    private void addMapMarker(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
}
