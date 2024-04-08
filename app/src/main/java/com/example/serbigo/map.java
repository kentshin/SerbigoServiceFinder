
package com.example.serbigo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

        public class map extends FragmentActivity implements OnMapReadyCallback,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                com.google.android.gms.location.LocationListener {


            private GoogleMap mMap;

            private GoogleApiClient googleApiClient;
            private Location location;
            private LocationManager locationManager;
            private LocationRequest locationRequest;
            private com.google.android.gms.location.LocationListener listener;
            private long update_interval = 2000;
            private long fastest_interval = 2000;
            private LocationManager locationManager2;
            private LatLng latLng;
            private boolean ispermission;

            private Marker newlocationmarker;
            double lat;
            double longi;
            FirebaseAuth FbAuth;
            FirebaseFirestore fstorage;
            String userId;
            private EditText search;
            EditText fname,lname,email;
            Button sign_up;




            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_map);
                search = (EditText)findViewById(R.id.search);


                if (requestPermission()) {


                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);


                    googleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


                    checklocation();
                }


                save_client();


            }

            private boolean checklocation() {


                if (!isLocationEnabled()) {

                    showAlert();

                }

                return isLocationEnabled();

            }




            private void showAlert() {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Enable Location Services?")
                        .setMessage("Your Location Settings is Off. Please enable Location Services to locate your current address")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                                Intent myintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myintent);

                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        });
                dialog.show();

            }



            private boolean isLocationEnabled() {

                locationManager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                return locationManager2.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager2.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }





            private boolean requestPermission() {
                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                ispermission = true;


                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    ispermission = false;
                                }

                            }


                            @Override
                            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

                return ispermission;
            }




            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (latLng!=null) {

                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F));
                }


            }

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                //checking permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                startLocationUpdate();

                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                if(location == null) {
                    startLocationUpdate();

                }else
                {
                    Toast.makeText(this, "Getting Location...", Toast.LENGTH_SHORT).show();
                }

            }




            private void startLocationUpdate() {

                locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(update_interval)
                        .setFastestInterval(fastest_interval);


                //checking permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            }



            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            public void onLocationChanged(Location location) {

                if (newlocationmarker != null) {
                    newlocationmarker.remove();
                }

                //String msg = "Updated Location: " +
                //Double.toString(location.getLatitude()) + " \n" +
                // Double.toString(location.getLongitude());
                //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));


                if (googleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);


                }

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                lat = latitude;
                longi = longitude;

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        String province = addresses.get(0).getSubAdminArea();

                        //marker postition
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(address);
                        newlocationmarker = mMap.addMarker(markerOptions);
                        //

                        search.setText(knownName + ", " + city + ", " + province + ", " + country);

                    } else {
                        search.setText("Getting Your Address");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }





    protected void onStart() {
        super.onStart();

        if(googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    protected void onStop() {
        super.onStop();

        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }




private void save_client (){

    fname = findViewById(R.id.fname);
    lname = findViewById(R.id.lname);
    email = findViewById(R.id.email);
    sign_up = findViewById(R.id.sign_up);
    FbAuth = FirebaseAuth.getInstance();
    fstorage = FirebaseFirestore.getInstance();
    userId = FbAuth.getCurrentUser().getUid();

    final DocumentReference docRef = fstorage.collection( "client"). document(userId);

    sign_up.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(fname.getText().toString().isEmpty() || lname.getText().toString().isEmpty( ) || email.getText().toString().isEmpty( ) || search.getText().toString().isEmpty( ) ) {

                Toast.makeText(map.this, "All fields are Required.", Toast.LENGTH_SHORT ).show();

            } else {

                service_id.client_lat = lat;
                service_id.client_lng = longi;

                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();
                String email_add = email.getText().toString();
                String home_address = search.getText().toString();

                Map<String,Object> client = new HashMap<>();
                Map<String,Object> geo_location = new HashMap<>();

                client.put("first_name", firstname );
                client.put("last_name", lastname );
                client.put("email_address", email_add);
                client.put("home_address", home_address );

                GeoPoint geo = new GeoPoint(lat,longi);
                client.put("geo_location", geo);


                docRef.set(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sign_up.setEnabled(false);
                            sign_up.setText("Signing in...");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }else {
                            Toast.makeText(map.this, "Client Details not Inserted", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }




        }




    });

    }










}
