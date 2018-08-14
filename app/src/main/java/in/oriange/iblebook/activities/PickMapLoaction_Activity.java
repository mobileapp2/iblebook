package in.oriange.iblebook.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.AutoCompleteLocation;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.Utilities;

public class PickMapLoaction_Activity extends FragmentActivity
        implements OnMapReadyCallback, AutoCompleteLocation.AutoCompleteLocationListener {

    private Context context;
    private GoogleMap mMap;
    private Button btn_save;
    LatLng latLng1;
    private ConstantData constantData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_loaction);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = PickMapLoaction_Activity.this;
        constantData = ConstantData.getInstance();
        btn_save = findViewById(R.id.btn_save);

        AutoCompleteLocation autoCompleteLocation =
                (AutoCompleteLocation) findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        init();
    }

    private void init() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (latLng1 != null) {
                    constantData.setLatitude(String.format("%.6f", latLng1.latitude));
                    constantData.setLongitude(String.format("%.6f", latLng1.longitude));
                    finish();
                } else {
                    Utilities.showAlertDialog(context, "Alert", "Please search and pick a location", false);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMapMarker(latLng);
            }
        });

    }

    @Override
    public void onTextClear() {
        mMap.clear();
    }

    @Override
    public void onItemSelected(Place selectedPlace) {
        addMapMarker(selectedPlace.getLatLng());
    }

    private void addMapMarker(LatLng latLng) {
        latLng1 = latLng;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
}
