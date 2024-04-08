package com.example.serbigo;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseAdapter extends FirestoreRecyclerAdapter<service_provider, FirebaseAdapter.listholder> {
    FirebaseAuth FbAuth;
    FirebaseFirestore fstorage;
    String userId;
    private OnClick onclick;

    private StorageReference mStorageRef;


    public FirebaseAdapter(@NonNull FirestoreRecyclerOptions<service_provider> options, OnClick onClick) {
        super(options);

        this.onclick = onClick;

    }




    @Override
    protected void onBindViewHolder(@NonNull final listholder holder, int position, @NonNull service_provider model) {




        float ratings = Float.parseFloat(model.getRate() + "");
        holder.fullname.setText(model.getFirst_name() + " " + model.getLast_name());
        holder.address.setText(model.getAddress());
        holder.rating.setRating(ratings);

        //for service provider photo
        String uid = getSnapshots().getSnapshot(position).getId();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child( uid + ".jpeg");
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(holder.worker_photo.getContext()).load(uri).into(holder.worker_photo);

                }
            }
        });


    }
    @NonNull
    @Override
    public listholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);

        return new listholder(view);
    }






    public class listholder extends RecyclerView.ViewHolder  {
      // private ImageView profile;
      private TextView fullname;
      private TextView address;
      private TextView comment;
      private RatingBar rating;
      private ImageView worker_photo;
      private Button view_comment;


      public listholder(@NonNull View itemView) {
          super(itemView);
          worker_photo = itemView.findViewById(R.id.worker_photo);
          fullname = itemView.findViewById(R.id.service);
          address = itemView.findViewById(R.id.Status);
          comment = itemView.findViewById(R.id.comment);
          rating = itemView.findViewById(R.id.ratingBar);
          view_comment = itemView.findViewById(R.id.view_comment);
         // itemView.setOnClickListener(this);





          itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  int postion = getAdapterPosition();
                  if (postion != RecyclerView.NO_POSITION && onclick != null){

                      onclick.onItemClick(getSnapshots().getSnapshot(postion), postion);


                  }

              }
          });



          view_comment.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  int postion = getAdapterPosition();
                  if (postion != RecyclerView.NO_POSITION && onclick != null){

                      onclick.onItemClick2(getSnapshots().getSnapshot(postion), postion);

                  }

              }
          });



      }

  }




  public interface OnClick {

        void onItemClick(DocumentSnapshot snapshot, int position);
        void onItemClick2(DocumentSnapshot snaphot, int position);


  }



public void setOnclick (OnClick onclick) {
      this.onclick = onclick;


}




}
