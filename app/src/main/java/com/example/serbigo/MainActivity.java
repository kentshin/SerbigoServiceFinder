package com.example.serbigo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;


    FirebaseAuth FbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();


    StorageReference mStorageRef;


    //for app call
    private static final String APP_KEY = "3798d145-41a1-40ad-bcc1-2be8c7bae3e0";
    private static final String APP_SECRET = "IiOdpcJ5/0W72T6M0sSyvA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private SinchClient sinchClient;
    //private TextView callState;


    private String callerId;
    private String recipientId;

    private Dialog dialog;

    private MediaPlayer incoming_call_sound;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        incoming_call_sound = MediaPlayer.create(MainActivity.this, R.raw.incoming_call_alarm);

        bottomNavigationView=findViewById(R.id.bot_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_frag, new home_fragment()).commit();

        getbadgenotif();
        voice_call_function();

    }


    private  BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment frag = null;

            switch  (menuItem.getItemId()) {
                case R.id.navigation_home:
                    frag = new home_fragment();

                    break;

                case R.id.request:
                    frag = new myrequest_fragment();

                    break;

                case R.id.history:
                    frag = new transaction_fragment();
                    break;

                case R.id.profile:
                    frag = new profile_fragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container_frag, frag).commit();

            return true;

        }



    };






    void get_token(){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        //Toast.makeText(getContext(), token, Toast.LENGTH_SHORT).show();


                        FbAuth = FirebaseAuth.getInstance();
                        fstorage = FirebaseFirestore.getInstance();
                        String userId = FbAuth.getCurrentUser().getUid();

                        final DocumentReference docRef = fstorage.collection( "notification"). document(userId);

                        Map<String,Object> notification = new HashMap<>();


                        notification.put("registry_token", token );

                        docRef.set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplication(), "token save", Toast.LENGTH_SHORT).show();

                            }
                        });




                    }
                });

    }



    void getbadgenotif(){

       CollectionReference reference = fstorage.collection("transactions");
       Query query = reference.whereEqualTo("client_id", FbAuth.getCurrentUser().getUid()).whereLessThan("status", 4);

       query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()) {
                   int badgenotif = 0;
                   for (QueryDocumentSnapshot document : task.getResult()) {
                     badgenotif = badgenotif + 1;
                      BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.request);
                      badge.setNumber(badgenotif);
                   }

               }else {

               }
           }
       });


    }



    void voice_call_function() {

        callerId = FbAuth.getUid().toString();
        recipientId = service_id.provider_id;

        //permission asking
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }


        //implementation of sinch
        sinchClient = Sinch.getSinchClientBuilder()
                .context(MainActivity.this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

    }



    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
            Toast.makeText(MainActivity.this, "Call Ended!", Toast.LENGTH_LONG).show();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            incoming_call_sound.stop();
            dialog.dismiss();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {

            Toast.makeText(MainActivity.this, "Call connected", Toast.LENGTH_LONG).show();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            incoming_call_sound.stop();
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            Toast.makeText(MainActivity.this, "Ringing...", Toast.LENGTH_LONG).show();


        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Toast.makeText(MainActivity.this, "incoming call", Toast.LENGTH_LONG).show();
            dialogbox_call_alert ();
            incoming_call_sound.start();
            incoming_call_sound.setLooping(true);


        }
    }








    public void dialogbox_call_alert () {

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.call_alert);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

            final TextView text = (TextView) dialog.findViewById(R.id.text_lbl);
            final ImageButton end_call = (ImageButton) dialog.findViewById(R.id.end_calling);
            final ImageButton answer_call = (ImageButton) dialog.findViewById(R.id.answer_calling);
            final Chronometer call_timer = (Chronometer) dialog.findViewById(R.id.timer);
            final ImageView photo = (ImageView) dialog.findViewById(R.id.client_photo_call);


            answer_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (call == null) {
                        dialog.dismiss();
                        incoming_call_sound.stop();

                    } else {

                        call.answer();
                        call.addCallListener(new SinchCallListener());
                        answer_call.setVisibility(View.INVISIBLE);
                        end_call.setVisibility(View.VISIBLE);
                        text.setVisibility(View.INVISIBLE);

                        call_timer.setVisibility(View.VISIBLE);
                        call_timer.setBase(SystemClock.elapsedRealtime());
                        call_timer.start();

                        incoming_call_sound.stop();


                    }
                }
            });


            end_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (call == null) {
                        dialog.dismiss();

                    } else {
                        call_timer.setBase(SystemClock.elapsedRealtime());
                        call_timer.start();
                        call.hangup();
                        dialog.dismiss();
                    }
                }

            });


            String uid = service_id.provider_id;
            //Toast.makeText(getContext(), uid, Toast.LENGTH_LONG).show();

            mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(uid + ".jpeg");
            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null) {

                        Glide.with(photo.getContext()).load(uri).into(photo);
                    }

                }
            });


        }







}
