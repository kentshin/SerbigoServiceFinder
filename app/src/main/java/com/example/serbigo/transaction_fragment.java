package com.example.serbigo;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class transaction_fragment extends Fragment {
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");
    private RecyclerView transaction_recycler;
    private FirebaseAdapter3 adapter;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public transaction_fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View transact_view = inflater.inflate(R.layout.fragment_transaction_fragment, container, false);
        transaction_recycler = transact_view.findViewById(R.id.transaction_recyler);
        display_history();


        return transact_view;

    }


    public void display_history() {

        Query query = reference.whereEqualTo("status", 4).whereEqualTo("client_id", fAuth.getCurrentUser().getUid()).orderBy("sdate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<transactions> newoptions = new FirestoreRecyclerOptions.Builder<transactions>()
                .setLifecycleOwner(this)
                .setQuery(query, transactions.class)
                .build();

        adapter = new FirebaseAdapter3(newoptions);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);

    }






}
