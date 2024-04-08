package com.example.serbigo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAdapter3 extends FirestoreRecyclerAdapter<transactions, FirebaseAdapter3.transholder> {

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();



    public FirebaseAdapter3(@NonNull FirestoreRecyclerOptions<transactions> options) {
        super(options);


    }

    @Override
    protected void onBindViewHolder(@NonNull final FirebaseAdapter3.transholder holder, int position, @NonNull transactions model) {

        //getting the service rate history
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        String transaction_id = snapshot.getId();

        final DocumentReference docRef = fstorage.collection("feedback").document(transaction_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {
                    String stars = snapshot.getDouble("service_rating").toString();
                    holder.rate.setText(stars);

                }

            }
        });

        //for cost history
        holder.cost.setText(model.getTotal_fee() + ".00 Php");


        //populating from the transaction collection
        holder.service_date.setText(model.getSdate().toDate().toString());

        DocumentReference provider_ref_id = fstorage.collection("provider").document(model.getProvider_id());

        provider_ref_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {
                    String service_fn = snapshot.getString("first_name") + " " + snapshot.getString("last_name");
                    String service = snapshot.getString("service");

                    holder.provider_name.setText(service_fn);

                    if (service.equals("1")) {
                        holder.service_name.setText("Plumbing");
                    }else if (service.equals("2")) {
                        holder.service_name.setText("Carpentry");
                    }else if (service.equals("3")) {
                        holder.service_name.setText("Home Paint");

                    }else if (service.equals("4")) {
                        holder.service_name.setText("Laundry");

                    }else if (service.equals("5")) {
                        holder.service_name.setText("Electrical Services");

                    }else if (service.equals("6")) {
                        holder.service_name.setText("Nail Cleaning");

                    }else if (service.equals("7")) {
                        holder.service_name.setText("T.v. Repair");

                    }else if (service.equals("9")) {
                        holder.service_name.setText("Refregirator Repair");

                    }else if (service == "9") {
                        holder.service_name.setText("Washing Machine Repair");

                    }


                }

                }
        });



    }


    @NonNull
    @Override
    public FirebaseAdapter3.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history, parent, false);

        return new FirebaseAdapter3.transholder(view);
    }






    public class transholder extends RecyclerView.ViewHolder{

        TextView service_date;
        TextView service_name;
        TextView provider_name;
        TextView cost;
        TextView rate;

        public transholder(@NonNull View itemView) {
            super(itemView);

            service_name = itemView.findViewById(R.id.service_name);
            provider_name = itemView.findViewById(R.id.provider_name);
            service_date = itemView.findViewById(R.id.date);
            cost = itemView.findViewById(R.id.cost);
           rate = itemView.findViewById(R.id.rating);

        }
    }
}
