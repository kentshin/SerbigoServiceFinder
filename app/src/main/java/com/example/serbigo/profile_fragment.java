package com.example.serbigo;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class profile_fragment extends Fragment implements OnMapReadyCallback {


    TextView name, email, address1, contact;
    Button save_address;
    ImageView client_image;
    double lat, lng;
    double lati,longit;

    int Take_Image_Code = 10001;

    private Marker newlocationmarker;


    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();;
    String uid = fAuth.getCurrentUser().getUid();

    private StorageReference mStorageRef;


    public profile_fragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View profileview = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        FirebaseUser user = fAuth.getCurrentUser();

        //putting a map on a fragment
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SupportMapFragment fragment = new SupportMapFragment();
        transaction.add(R.id.map , fragment);
        transaction.commit();
        fragment.getMapAsync(this);


        name = profileview.findViewById(R.id.name);
        address1 = profileview.findViewById(R.id.address);
        contact= profileview.findViewById(R.id.contact);
        email = profileview.findViewById(R.id.email);
        save_address = profileview.findViewById(R.id.save);
        client_image = profileview.findViewById(R.id.imageView);

        get_details ();
        upload();
        save();


        if (user.getPhotoUrl() != null) {

            Glide.with(this).load(user.getPhotoUrl()).into(client_image);
        }else {
            client_image.setImageResource(R.drawable.client_photo);
        }

        return profileview;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap;
        mMap = googleMap;

       /*
     LatLng location = new LatLng (14.2624, 121.4570);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        //mMap.addMarker(new MarkerOptions().position(location).title("Current Address"));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title("Current Address");
        newlocationmarker = mMap.addMarker(markerOptions);
        */

        final DocumentReference docRef = firestore.collection("client").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {

                    lat = snapshot.getGeoPoint("geo_location").getLatitude();
                    lng = snapshot.getGeoPoint("geo_location").getLongitude();

                    LatLng location = new LatLng (lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(location);
                    markerOptions.title("Current Address");
                    newlocationmarker = mMap.addMarker(markerOptions);

                }
            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {

                if (newlocationmarker != null) {
                    newlocationmarker.remove();

                }


                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                lati = latitude;
                longit = longitude;

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

                        address1.setText(knownName + ", " + city + ", " + province + ", " + country);


                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(knownName + ", " + city + ", " + province + ", " + country);
                        newlocationmarker = mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));



                        }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            });





    }






    public void get_details () {

        DocumentReference docRef = firestore.collection("client").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String fname = (documentSnapshot.getString("first_name")) + " " + (documentSnapshot.getString("last_name"));

                    name.setText(fname);

                    contact.setText(fAuth.getCurrentUser().getPhoneNumber());
                    email.setText(documentSnapshot.getString("email_address"));
                    address1.setText(documentSnapshot.getString("home_address"));

                }
            }
        });



    }







    public void save(){



        save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_address = address1.getText().toString();
                GeoPoint geo = new GeoPoint(lati,longit);

                FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
                fstorage.collection("client")
                        .document(fAuth.getCurrentUser().getUid()).update("home_address", new_address );

                fstorage.collection("client")
                        .document(fAuth.getCurrentUser().getUid()).update("geo_location", geo );

                Toast.makeText(getActivity(), "Current Address Successfully Change", Toast.LENGTH_SHORT).show();


            }
        });


    }





    //for photo

        public void upload() {

        client_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCamera();

            }
        });

        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Take_Image_Code) {

            if(resultCode == RESULT_OK) {

                Bitmap bitmap  = (Bitmap) data.getExtras().get("data");
                client_image.setImageBitmap(bitmap);
                handleUpload(bitmap);



            }
        }

    }


    //handles the photo to the uid of the user
    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_image);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(uid + ".jpeg");

        mStorageRef.putBytes(output_image.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                getImageUrl(mStorageRef);
            }
        });



    }

    //getting the storage reference for the firebase storage
    private void getImageUrl(StorageReference mStorageRef) {
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                setUserProfile(uri);


            }
        });

    }

    //for updating and fetching profile image fetching
    private void setUserProfile(Uri uri) {
        FirebaseUser user = fAuth.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Profile Photo Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }


//permission for camera asking
    private void askCamera(){

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Take_Image_Code);
        }else {

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, Take_Image_Code);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == Take_Image_Code) {
          if(grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

              Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(camera_intent, Take_Image_Code);

          }else {

              Toast.makeText(getActivity(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
          }

      }

    }
}
