package in.oriange.iblebook.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.AutoCompleteLocation;
import in.oriange.iblebook.utilities.Utilities;

public class PickMapLoaction_Activity extends FragmentActivity
        implements OnMapReadyCallback, AutoCompleteLocation.AutoCompleteLocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    private LocationManager mLocationManager;
    public static final int REQUEST_LOCATION_CODE = 99;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    private GetAddressListPojo addressList;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    LatLng latLng1;
    private Context context;
    private GoogleMap mMap;
    private Button btn_save, btn_pick;
//    private ConstantData constantData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_loaction);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = PickMapLoaction_Activity.this;
//        constantData = ConstantData.getInstance();
        btn_save = findViewById(R.id.btn_save);
        btn_pick = findViewById(R.id.btn_pick);

        AutoCompleteLocation autoCompleteLocation =
                (AutoCompleteLocation) findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        init();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                bulidGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }

    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        //constantData.setLatitude(String.format("%.6f", location.getLatitude()));
        //constantData.setLongitude(String.format("%.6f", location.getLongitude()));
        //lastlocation = location;
        if (currentLocationmMarker != null) {
            currentLocationmMarker.remove();

        }
        // Log.d("lat = ",""+constantData.getLatitude());

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addMapMarker(latLng);

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void init() {
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bulidGoogleApiClient();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (latLng1 != null) {

//                    constantData.setLatitude(String.format("%.6f", latLng1.latitude));
//                    constantData.setLongitude(String.format("%.6f", latLng1.longitude));
                    try {
                        getAllAddress();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    public void getAllAddress() throws IOException {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());
        addressList = new GetAddressListPojo();
        addresses = geocoder.getFromLocation(latLng1.latitude, latLng1.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        addressList.setAddress_line_one(addresses.get(0).getAddressLine(0));
        addressList.setDistrict(addresses.get(0).getLocality());
        addressList.setCountry(addresses.get(0).getCountryName());
        addressList.setState(addresses.get(0).getAdminArea());
        addressList.setPincode(addresses.get(0).getPostalCode());

//        constantData.setAddressListPojo(addressList);

        Intent intent = getIntent();
        intent.putExtra("latitude", String.format("%.6f", latLng1.latitude));
        intent.putExtra("longitude", String.format("%.6f", latLng1.longitude));
        intent.putExtra("mapAddressDetails", addressList);
        setResult(RESULT_OK, intent);

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
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            AutoCompleteLocation ob = findViewById(R.id.autocomplete_location);
            ob.setHint(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
