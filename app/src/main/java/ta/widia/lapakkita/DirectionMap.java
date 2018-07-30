package ta.widia.lapakkita;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import ta.widia.lapakkita.util.RetrofitMapsJarak;
import ta.widia.lapakkita.util.jarak.Example;

public class DirectionMap extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;

    private static final String TAG = "Lokasi";

    Location lastLocation;
    Marker currLocationMarker;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    LatLng lokasi_asal;
    LatLng lokasi_tujuan;

    //TextView ShowDistanceDuration;
    Polyline line;
    String tipe_rute;
    private int PROXIMITY_RADIUS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setTitle("Destinasi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //cek permission di android M
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //cek apakah google play services terinstall/ada
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(DirectionMap.this, "Maaf, Googla Play Services tidak tersedia diperangkat anda", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private synchronized void buildGoogleApiClient() {
        Log.d(TAG, "mulai deteksi lokasi");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final String nama_objek = getIntent().getStringExtra("key_nama_tujuan");
        String lokasi_lat = getIntent().getStringExtra("key_lat_tujuan");
        String lokasi_long = getIntent().getStringExtra("key_long_tujuan");

        double lok_lat = Double.valueOf(lokasi_lat);
        double lok_long= Double.valueOf(lokasi_long);

        // tambahkan marker dan aktifkan pindah kamera
        lokasi_tujuan = new LatLng(lok_lat, lok_long);
        //lokasi_tujuan = new LatLng(1.660664, 101.437487);
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(lokasi_tujuan);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));*/

        mMap.addMarker(new MarkerOptions()
                .position(lokasi_tujuan)
                .title(nama_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();

        /*mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.maps_info_window, null);
                TextView nama = v.findViewById(R.id.txt_nama);
                ImageView imageView = v.findViewById(R.id.foto_lokasi);
                nama.setText(nama_objek);
                Log.d("URL Gambar", gambar_objek);
                Glide.with(getApplicationContext()).load(gambar_objek).into(imageView);
                return  v;
            }
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();*/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi_tujuan));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(DirectionMap.this, "Maaf, koneksi terganggu.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        lastLocation = location;

        //ambil lokasi tengah (center map)
        String lokasi_lat = getIntent().getStringExtra("key_lat_tujuan");
        String lokasi_long = getIntent().getStringExtra("key_long_tujuan");

        double lat1, lon1, lat2, lon2;
        lat1 = Double.valueOf(lokasi_lat);
        lon1 = Double.valueOf(lokasi_long);

        lat2 = location.getLatitude();
        lon2 = location.getLongitude();

        lokasi_asal = new LatLng(lat2, lon2);

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        LatLng lokasi_asal_tengah = new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi_asal_tengah));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //build_retrofit_and_get_response_nearby("restaurant");
        build_retrofit_and_get_response_jarak("driving");

        //stop location updates
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
    }

    private void build_retrofit_and_get_response_jarak(String type) {
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMapsJarak service = retrofit.create(RetrofitMapsJarak.class);

        Call<Example> call = service.getDistanceDuration("metric", lokasi_asal.latitude + "," + lokasi_asal.longitude, lokasi_tujuan.latitude + "," + lokasi_tujuan.longitude, type);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {
                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    if(response.isSuccess()){
                        // This loop will go through all the results and add marker on each location.
                        for (int i = 0; i < response.body().getRoutes().size(); i++) {
                            String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                            String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();

                            final Snackbar snackBar = Snackbar.make(findViewById(R.id.map), "Jarak ke lokasi tujuan: " + distance + ", estimasi waktu: " + time, Snackbar.LENGTH_INDEFINITE);
                            snackBar.setAction("Tutup", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackBar.dismiss();
                                }
                            });
                            snackBar.show();

                            //ShowDistanceDuration.setText("Jarak dari lokasi anda\nke tempat tujuan adalah:" + distance + ", Durasi:" + time);
                            String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> list = decodePoly(encodedString);
                            line = mMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .width(15)
                                    .color(Color.BLUE)
                                    .geodesic(true)
                            );
                            if(i > 0) {
                                Toast.makeText(DirectionMap.this, "Maaf, kami tidak dapat menemukan rute perjalanan anda", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
