package com.example.serbigo;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;


public class home_fragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    TextView name, address, contact;
    private CardView card1,card2,card3,card4,card5,card6,card7,card8,card9;


    public home_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View profileview = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        name = profileview.findViewById(R.id.customer_name);
        address = profileview.findViewById(R.id.mark);
        contact= profileview.findViewById(R.id.contact);



        DocumentReference docRef = firestore.collection("client").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    name.setText(documentSnapshot.getString("first_name"));
                    address.setText(documentSnapshot.getString("home_address"));
                    contact.setText(fAuth.getCurrentUser().getPhoneNumber());
                }
            }
        });


        card1 = (CardView) profileview.findViewById(R.id.card1);
        card2 = (CardView) profileview.findViewById(R.id.card2);
        card3 = (CardView) profileview.findViewById(R.id.card3);
        card4 = (CardView) profileview.findViewById(R.id.card4);
        card5 = (CardView) profileview.findViewById(R.id.card5);
        card6 = (CardView) profileview.findViewById(R.id.card6);
        card7 = (CardView) profileview.findViewById(R.id.card7);
        card8 = (CardView)profileview.findViewById(R.id.card8);
        card9 = (CardView) profileview.findViewById(R.id.card9);



        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "1";
                service_id.id = "PLUMBING";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.plumbing);
                startActivity(servicepage);


            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "2";
                service_id.id = "CARPENTRY";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.carpentry);
                startActivity(servicepage);

            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "3";
                service_id.id = "HOME PAINT";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.homepaint);
                startActivity(servicepage);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "4";
                service_id.id = "LAUNDRY";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.wash);
                startActivity(servicepage);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "5";
                service_id.id = "MANI/PEDICURE";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.manicure2);
                startActivity(servicepage);



            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service_id.id = "ELECTRICAL";
                service_id.service = "6";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.electrical4);
                startActivity(servicepage);


            }
        });

        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "7";
                service_id.id = "TV REPAIR";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.tv_repair);
                startActivity(servicepage);


            }
        });

        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "8";
                service_id.id = "REFRIGERATOR REPAIR";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.ref_repair);
                startActivity(servicepage);

            }
        });

        card9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_id.service = "9";
                service_id.id = "WASHING MACHINE REPAIR";
                Intent servicepage = new Intent(getActivity(),service_page.class);
                servicepage.putExtra("logo", R.drawable.laundry4);
                startActivity(servicepage);

            }
        });









        return profileview;




    }





}
