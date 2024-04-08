package com.example.serbigo;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.value.ReferenceValue;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class myrequest_fragment extends Fragment implements FirebaseAdapter2.OnClick {

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");
    private RecyclerView transaction_recycler;
    private FirebaseAdapter2 adapter;
    private FirebaseAuth fAuth;

    //for this class
    String trans_id;

    String reported_transaction;
    String reported_by;
    String reported_client;
    String reported_provider_id;
    String reportreason;


    public myrequest_fragment(){
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View transact_view = inflater.inflate(R.layout.fragment_myrequest_fragment, container, false);


        transaction_recycler = transact_view.findViewById(R.id.transaction_recyler);
        display_trasactions();

        return transact_view;
    }





    public void display_trasactions() {


        Query query = reference.whereLessThan("status", 4).whereEqualTo("client_id", service_id.client_id);
        FirestoreRecyclerOptions<transactions> newoptions = new FirestoreRecyclerOptions.Builder<transactions>()
                .setLifecycleOwner(this)
                .setQuery(query, transactions.class)
                .build();

        adapter = new FirebaseAdapter2(newoptions, this);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);

    }



public void date(){

}

//first click
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

        FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

        service_id.transaction_id = snapshot.getId();
        long s_status = (long) snapshot.get("status");

        if(s_status == 2) {

            fstorage.collection("transactions")
                    .document(service_id.transaction_id).update("status",3);

        }else if (s_status == 3)
        //Toast.makeText(getActivity(), service_id.transaction_id, Toast.LENGTH_SHORT).show();
        dialogbox();

    }



    //second click
    @Override
    public void onItemClick2(DocumentSnapshot snaphot, int position) {

        FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
        reported_transaction = snaphot.getId();
        reported_by = snaphot.get("client_id").toString();
        reported_provider_id = snaphot.get("provider_id").toString();
        trans_id = snaphot.getId();

        report_dialogbox();


    }



    //third click
    @Override
     public void onItemClick3(DocumentSnapshot snaphot, int position) {

        Intent intent = new Intent (getActivity(), call2.class);
        startActivity(intent);

    }






    public void dialogbox () {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.rating_dialogbox);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        final RatingBar rateReview = (RatingBar)dialog.findViewById(R.id.ratingsBar);
        final EditText review = (EditText)dialog.findViewById(R.id.reviewED);
        Button cancel_rate = (Button)dialog.findViewById(R.id.cancel);
        Button submit_rate = (Button)dialog.findViewById(R.id.submit);

        cancel_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit_rate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (review.getText().toString().isEmpty() || rateReview.getRating() == 0.0 ) {

                    review.setError("Please give star rating and feedback comments to the service. Thank you!");

                } else {


                    final DocumentReference docRef = fstorage.collection("feedback").document(service_id.transaction_id);
                    //getting provider_id
                    final DocumentReference get_provider_id = fstorage.collection("transactions").document(service_id.transaction_id);
                    get_provider_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(final DocumentSnapshot snapshot) {
                            if (snapshot.exists()) {

                                final String get_id = snapshot.getString("provider_id");

                                //service_id.provider_id = get_id.getId();
                                Toast.makeText(getActivity(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show();

                                // inserting
                                if (rateReview.getRating() == 0.0) {

                                    Toast.makeText(getActivity(), "Please Rate the Service. Thank You", Toast.LENGTH_SHORT).show();
                                } else {

                                    Map<String, Object> feedback = new HashMap<>();

                                    String transaction_review = review.getText().toString();
                                    final double transaction_rate = (double) rateReview.getRating();

                                    feedback.put("provider_id", get_id);
                                    feedback.put("client_id", service_id.client_id);
                                    feedback.put("service_rating", transaction_rate);
                                    feedback.put("comments", transaction_review);

                                    docRef.set(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                //updating status pf transaction
                                                FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
                                                fstorage.collection("transactions")
                                                        .document(service_id.transaction_id).update("status", 4);

                                                fstorage.collection("provider")
                                                        .document(service_id.provider_id).update("status", 1);

                                                dialog.dismiss();

                                            }

                                        }
                                    });

                                    //updating rate
                                    DocumentReference newrate = fstorage.collection("provider").document(get_id);
                                    newrate.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot snapshot) {

                                            double old_rating = snapshot.getDouble("rate");
                                            double new_rating = (old_rating + transaction_rate) / 2;

                                            fstorage.collection("provider")
                                                    .document(get_id).update("rate", new_rating);
                                        }
                                    });
                                    //end of updating rate


                                }
                                //end of inserting


                            }
                        }
                    });
                    //


                }//end of if condition error handling
            }
        });// end of onlick submit rate

    }






    public void report_dialogbox () {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.report_dialogbox);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        Button report = (Button)dialog.findViewById(R.id.yes);
        Button cancel = (Button)dialog.findViewById(R.id.no);

        final EditText report_reason = (EditText)dialog.findViewById(R.id.report_reason);


        //cancel
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //report
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (report_reason.getText().toString().isEmpty()) {
                    report_reason.setError("Please state the complaint. Thank you!");

                }else {


                    reportreason = report_reason.getText().toString();

                    final DocumentReference docRef = fstorage.collection("reported_worker_logs").document(reported_transaction);

                    if (reportreason.isEmpty()) {
                        report_reason.setError("Please state the reason why you are reporting this Customer");
                    } else {

                        Map<String, Object> reported_worker_logs = new HashMap<>();


                        reported_worker_logs.put("reported_by", reported_by);
                        reported_worker_logs.put("provider_id", reported_provider_id);
                        reported_worker_logs.put("reported_date", Timestamp.now());
                        reported_worker_logs.put("report_reason", reportreason);

                        docRef.set(reported_worker_logs).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(getContext(), "Report Created Successfully!", Toast.LENGTH_SHORT).show();

                                    fstorage.collection("transactions")
                                            .document(trans_id).update("status", 5);

                                    //fstorage.collection("provider").document(reported_by).update("status",1);

                                    dialog.dismiss();

                                }
                            }
                        });

                    }
                }// end of if statement error handling

            }
        });


    }





}
