package com.example.serbigo;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

public class provider_info extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FirebaseFirestore fstorage;
    FirebaseUser firebaseUser;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    //notif
   // String registry_token;
    //String title = "SerbiGo Job Request";
    //String message = "You have 1 Job Request, Please open the App";

    private StorageReference mStorageRef;


    String provider_id = service_id.provider_id;
    String customer_id = service_id.client_id;
    long status = 1;
    private TextView fullname;
    private TextView address;
    private EditText comment;
    private TextView credentials;
    private RatingBar rating;
    private Button hire;
    private ImageView worker_photo;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_info);

        //for notif
        //apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        fstorage = FirebaseFirestore.getInstance();
        fullname = findViewById(R.id.service);
        address = findViewById(R.id.Status);
        credentials = findViewById(R.id.Credentials);
        comment = findViewById(R.id.comment);
        rating = findViewById(R.id.ratingBar);
        hire = findViewById(R.id.hire);
        worker_photo = findViewById(R.id.photo);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        provider_info();
        load_photo();




        hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final DocumentReference docRef = fstorage.collection( "transactions"). document();
                //DocumentReference service_provider_ref = fstorage.collection("provider").document(provider_id);
                //service_id.provider_id = service_provider_ref.getId();

                String remarks = comment.getText().toString();

                if (fullname.getText().toString().isEmpty() || address.getText().toString().isEmpty() || comment.getText().toString().isEmpty()) {

                    //Toast.makeText(provider_info.this, "Please put additional notes and information in order to specify the request", Toast.LENGTH_LONG).show();
                    comment.setError("Please put additional information in order to specify the request e.g. (time, details, etc.)");
                }else {


                    hire.setEnabled(false);
                    Map<String,Object> transaction = new HashMap<>();

                    transaction.put("provider_id", provider_id);
                    transaction.put("client_id", customer_id);
                    transaction.put("status", status);
                    transaction.put("sdate", Timestamp.now());
                    transaction.put("remarks", remarks);
                    transaction.put("total_fee", service_id.service_fee);

                    docRef.set(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                Toast.makeText(provider_info.this, "Request Successful!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();


                            }

                        }
                    });



                }





            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {



        MarkerOptions marker;
        mMap = googleMap;

        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.myhousepin);


        LatLng latlng2 = new LatLng(service_id.client_lat, service_id.client_lng);
        mMap.addMarker(new MarkerOptions().position(latlng2).title("My Address").icon(icon2));
        //.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_home));

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
           double lat = bundle.getDouble("lat");
            double lng = bundle.getDouble("lng");

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.workerpin);

            LatLng provider = new LatLng(lat, lng);
            marker = new MarkerOptions().position(provider).title("Service Provider Location").icon(icon);
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(provider, 14));



        }




    }


    public void provider_info() {


        DocumentReference docRef = fstorage.collection("provider").document(service_id.provider_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    //service_id.fullname = documentSnapshot.getString("first_name") +" "+ documentSnapshot.getString("last_name");
                    //service_id.address = documentSnapshot.getString("address");
                    //service_id.rate = documentSnapshot.getDouble("rate");

                    double rates = documentSnapshot.getDouble("rate");
                    fullname.setText( documentSnapshot.getString("first_name") +" "+ documentSnapshot.getString("last_name"));
                    address.setText(documentSnapshot.getString("address"));
                    credentials.setText(documentSnapshot.getString("credentials"));
                    rating.setRating(Float.parseFloat(rates + ""));


                }
            }
        });


    }













/*
    public void load_details (){

        float rates = Float.parseFloat(service_id.rate +"");
        fullname.setText(service_id.fullname);
        address.setText(service_id.address);
        rating.setRating(rates);

    }


*/

    void load_photo() {

        //for service provider photo
        String uid = provider_id;
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child( uid + ".jpeg");
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(worker_photo.getContext()).load(uri).into(worker_photo);

                } else {
                    //Glide.with(worker_photo.getContext()).load(R.drawable.picprofile).into(worker_photo);

                }
            }
        });



    }









}
