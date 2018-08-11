package in.oriange.iblebook.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.oriange.iblebook.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class View_Location_Map_Activity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks{
private Context context;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private String lat, lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = View_Location_Map_Activity.this;
        lat = "18.406129";
        lng = "76.552144";
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleM) {
        googleMap = googleM;
        init();
    }

    private void init() {
        MarkerOptions userMarker = new MarkerOptions();
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_mapmarker);
        LatLng userLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 50, 50, false);
        userMarker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        userMarker.snippet(getAddressFromLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
        userMarker.position(userLatLng);
        googleMap.addMarker(userMarker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .zoom(10).build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);

        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
        }

        return address;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
