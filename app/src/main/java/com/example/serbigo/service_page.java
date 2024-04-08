package com.example.serbigo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class service_page extends AppCompatActivity {
        FirebaseAuth fAuth;
        FirebaseFirestore firestore;
        TextView s_id,description,limitation;
        TextView total_fee;
        ImageView logo_service;
        String getservice_id;
        Button request;
        Button compute;
        EditText pax;

    long perperson = 1;
    long totalpax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_page);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        logo_service = findViewById(R.id.logo_service);
        s_id = findViewById(R.id.service_id);

        description = findViewById(R.id.description);
        limitation = findViewById(R.id.limitation);
        request = findViewById(R.id.request);
        total_fee = findViewById(R.id.total_fee);
        s_id.setText(service_id.id);

        pax = findViewById(R.id.pax);
        compute= findViewById(R.id.compute);

        //populating imageview
        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
            int servicelogo = bundle.getInt("logo");
            logo_service.setImageResource(servicelogo);
            getservice_id = service_id.service;
        }


        if (getservice_id == "5" ){

            compute.setVisibility(View.VISIBLE);
            pax.setVisibility(View.VISIBLE);

            DocumentReference docRef = firestore.collection("services").document(getservice_id);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {


                        description.setText(documentSnapshot.getString("service_description"));
                        limitation.setText(documentSnapshot.getString("service_limitations"));
                        total_fee.setText(documentSnapshot.getLong("fee").toString() + ".00 " + " " + "Php");

                        service_id.service_fee = documentSnapshot.getLong("fee");

                        compute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (pax.getText().toString().isEmpty()) {

                                    totalpax = perperson * documentSnapshot.getLong("fee");
                                    total_fee.setText(totalpax + ".00");
                                    service_id.service_fee = totalpax;

                                } else {
                                    perperson = Long.parseLong(pax.getText().toString());
                                    totalpax = perperson * documentSnapshot.getLong("fee");
                                    total_fee.setText(totalpax + ".00");
                                    service_id.service_fee = totalpax;
                                }

                            }
                        });



                    }
                }
            });
            

        } else {


            DocumentReference docRef = firestore.collection("services").document(getservice_id);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        service_id.service_fee = documentSnapshot.getLong("fee");
                        description.setText(documentSnapshot.getString("service_description"));
                        limitation.setText(documentSnapshot.getString("service_limitations"));
                        total_fee.setText(documentSnapshot.getLong("fee").toString() + ".00 " + " " + "Php");

                    }
                }
            });


        }


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), worker_list.class));
                finish();
/*
                firestore = FirebaseFirestore.getInstance();
                double user_latitude;
                double user_logitude;

                //getting service provider information
               firestore.collection("service_provider")
                       .whereEqualTo("service", getservice_id)
                       .limit(3)
                       .whereEqualTo("status", "Active")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {


                           //for (QueryDocumentSnapshot document: task.getResult()) {
                           //   Toast.makeText(service_page.this,  " " + document.getData(), Toast.LENGTH_SHORT).show();
                           // }

                       } else  {
                           Toast.makeText(service_page.this, "No Service Provider Available", Toast.LENGTH_SHORT).show();
                       }

                   }
               });

*/

            }
        });
    }






    //distance calculation

/*
    private void getting_location (){

        DocumentReference docRef = firestore.collection("service_provider").document(getservice_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {


                    double lat = documentSnapshot.getGeoPoint("geolocation").getLatitude();
                   double longi  = documentSnapshot.getGeoPoint("geolocation").getLongitude();


                }
            }
        });

        //Calculating distance
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat1-lat2);
        double dLng = Math.toRadians(lng1-lng2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lat1)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

    }

*/




public void computepax() {






}



}
