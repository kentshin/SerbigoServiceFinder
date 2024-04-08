package com.example.serbigo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.google.type.Date;

import java.lang.reflect.Array;
import java.time.chrono.JapaneseDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class worker_list extends AppCompatActivity implements FirebaseAdapter.OnClick{
    String  getservice_id = service_id.service;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("provider");
    private RecyclerView workerlists;
    private FirebaseAdapter adapter;
    private TextView comment_view;
    private TextView header;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);




        header =  findViewById(R.id.header);
        comment_view =  findViewById(R.id.comment);
        workerlists = findViewById(R.id.recycleview_list);
        dispaly_workers();

        //Toast.makeText(worker_list.this, service_id.workercount + "Sorry! There are no available Service Providers at the moment.", Toast.LENGTH_LONG).show();





    }



        public void dispaly_workers () {

            Query query = reference.whereEqualTo("service", getservice_id)
                    .whereEqualTo("status", 1)
                    .orderBy("rate", Query.Direction.DESCENDING).limit(3);

            FirestoreRecyclerOptions<service_provider> newoptions = new FirestoreRecyclerOptions.Builder<service_provider>()
                    .setLifecycleOwner(this)
                    .setQuery(query, service_provider.class)
                    .build();


                adapter = new FirebaseAdapter(newoptions, this);
                //workerlists.setHasFixedSize(true);
                workerlists.setLayoutManager(new LinearLayoutManager(this));
                workerlists.setAdapter(adapter);
                adapter.setOnclick(this);


    }








    //first click
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        int number = position + 1;

        service_id.provider_id = snapshot.getId();
        //provider_info();
        client_address();

        DocumentReference docRef = fstorage.collection("geo_location").document(service_id.provider_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    GeoPoint location = documentSnapshot.getGeoPoint("location");
                    double lat =  location.getLatitude();
                    double lng =location.getLongitude();

                    Intent intent = new Intent (worker_list.this, provider_info.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng",lng);
                    startActivity(intent);
                }
            }
        });

    }








    //second click
    @Override
    public void onItemClick2(DocumentSnapshot snaphot, int position) {

        header.setVisibility(View.VISIBLE);
        comment_view.setVisibility(View.VISIBLE);

        service_id.provider_id = snaphot.getId();
        CollectionReference load_comments = fstorage.collection("feedback");
        final Query query = load_comments.whereEqualTo("provider_id", service_id.provider_id );

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String data = "";
                    String comments;
                    String rate;

                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                        comments = documentSnapshot.get("comments").toString();
                        rate = documentSnapshot.get("service_rating").toString();
                        data +=  "Service Rating: " + " " +rate+ "\n" + "Comment: " + " " + comments + " " + "\n" +"\n";

                    }
                        comment_view.setText(data);
                }
            }
        });

    }





    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }





    public void client_address() {
        DocumentReference docRef = fstorage.collection("client").document(service_id.client_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    GeoPoint location = documentSnapshot.getGeoPoint("geo_location");
                    double lat =  location.getLatitude();
                    double lng =location.getLongitude();

                    service_id.client_lat=lat;
                    service_id.client_lng=lng;

                }
            }
        });

    }







}




