package com.example.serbigo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static androidx.core.content.ContextCompat.startActivity;


public class FirebaseAdapter2 extends FirestoreRecyclerAdapter<transactions, FirebaseAdapter2.transholder> {
    private OnClick onclick;
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");

    private StorageReference mStorageRef;


    public FirebaseAdapter2(@NonNull FirestoreRecyclerOptions<transactions> options, OnClick onclick) {
        super(options);

        this.onclick = onclick;
    }

    @Override
    protected void onBindViewHolder(@NonNull final transholder holder, int position, @NonNull final transactions model) {

        service_id.provider_id = model.getProvider_id();
        long stat = model.getStatus();

        //for service provider photo
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child( model.getProvider_id() + ".jpeg");
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(holder.worker_img.getContext()).load(uri).into(holder.worker_img);

                }
            }
        });
        //for service provider photo




        if (stat == 1) {

            holder.status_button.setText("Requesting...");
            //holder.status_button.setBackgroundColor(Color.parseColor("#E7CE82"));
            holder.status_button.setClickable(false);
        } else if (stat == 2) {

            holder.status_button.setText("Confirm Service?");
            holder.status_button.setBackgroundColor(Color.parseColor("#8CDD81"));
            holder.status_button.setClickable(true);

        } else if (stat == 3) {

            holder.status_button.setText("On Hire - Job Done?");
            holder.status_button.setBackgroundColor(Color.parseColor("#63AB62"));
            holder.status_button.setClickable(true);
        }

       DocumentReference provider_ref_id =  fstorage.collection("provider").document(model.getProvider_id());

        provider_ref_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {
                 String service_fn = snapshot.getString("first_name") + " " +  snapshot.getString("last_name");
                 String service = snapshot.getString("service");

                   if (service.equals("1"))
                   {
                       holder.service_name.setText("Plumbing");
                   }else if (service.equals("2")) {
                       holder.service_name.setText("Carpentry");
                   }else if (service.equals("3")) {
                       holder.service_name.setText("Home Paint");

                   }else if (service.equals("4")) {
                       holder.service_name.setText("Laundry");

                   }else if (service.equals("6")) {
                       holder.service_name.setText("Electrical Services");

                   }else if (service.equals("5")) {
                       holder.service_name.setText("Nail Cleaning");

                   }else if (service.equals("7")) {
                       holder.service_name.setText("T.v. Repair");

                   }else if (service.equals("9")) {
                       holder.service_name.setText("Refregirator Repair");

                   }else if (service == "9") {
                       holder.service_name.setText("Washing Machine Repair");

                   }
                   holder.provider_name.setText(service_fn);
                   holder.total_fee.setText(model.getTotal_fee() +" "+ "Php");
                }

            }
        });

        holder.sdate.setText(model.getSdate().toDate().toString());



    }



    @NonNull
    @Override
    public FirebaseAdapter2.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transactions, parent, false);

        return new FirebaseAdapter2.transholder(view);
    }



    public class transholder extends RecyclerView.ViewHolder {

        TextView service_name;
        TextView provider_name;
        TextView total_fee;
        TextView sdate;
        Button status_button;
        ImageView worker_img;
        ImageButton report_button;
        ImageButton call;


        public transholder(@NonNull View itemView) {
            super(itemView);


            service_name = itemView.findViewById(R.id.service_name);
            provider_name = itemView.findViewById(R.id.service_provider_name);
            total_fee = itemView.findViewById(R.id.total_fee);
            sdate= itemView.findViewById(R.id.date);
            status_button = itemView.findViewById(R.id.service_status);
            worker_img = itemView.findViewById(R.id.worker_photo);
            report_button = itemView.findViewById(R.id.report_button);
            call = itemView.findViewById(R.id.call2);


           status_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });



           report_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick2(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });


            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick3(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });




        }
    }







    public interface OnClick {

        void onItemClick(DocumentSnapshot snapshot, int position);
        void onItemClick2(DocumentSnapshot snaphot, int position);
        void onItemClick3(DocumentSnapshot snaphot, int position);

    }


    public void setOnclick (FirebaseAdapter2.OnClick onclick) {
        this.onclick = onclick;


    }



}
