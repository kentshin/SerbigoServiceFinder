package com.example.serbigo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class log_in extends AppCompatActivity {

    private boolean ispermission;
    FirebaseAuth fAuth;
    FirebaseFirestore fstorage;
    EditText phone,otp;
    Button verify;
    ProgressBar progressBar;
    TextView sending;
    CountryCodePicker codePicker;
    String verification;

    PhoneAuthProvider.ForceResendingToken Token;
    Boolean verificationProgress = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        fAuth = FirebaseAuth.getInstance();
        fstorage = FirebaseFirestore.getInstance();

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        progressBar = findViewById(R.id.progressBar);
        verify = findViewById(R.id.verify);
        sending = findViewById(R.id.sending);
        codePicker = findViewById(R.id.ccp);

        verify.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

               if (!verificationProgress) {

                         if (!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {

                            String phonenumber = "+" + codePicker.getSelectedCountryCode() + phone.getText().toString();
                            sending.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            requestOTP(phonenumber);

                        } else {
                            phone.setError("Phone Number is Not Valid");
                        }

               }else {

                  String userOTP = otp.getText().toString();
                   PhoneAuthCredential credential;

                        if (!userOTP.isEmpty() && userOTP.length() == 6) {
                            credential = PhoneAuthProvider.getCredential(verification,userOTP);
                            verifyAuth(credential);
                            verify.setEnabled(false);
                            verify.setText("Signing in...");



                        }
                        else {
                            otp.setError("Valid OTP is required");
                            verify.setText("Verify");
                        }


               }



            }
        });

    }

   @Override
   protected void onStart () {
        super.onStart();

        if(fAuth.getCurrentUser() != null) {
            progressBar.setVisibility(View.VISIBLE);
            sending.setText("Verifying Account...");
            sending.setVisibility(View.VISIBLE);
            checkUser();
            verify.setEnabled(false);
            verify.setText("Signing in...");

        }

       requestPermission();


   }






    private void checkUser() {

        final DocumentReference docRef = fstorage.collection("client").document(fAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {


                    CollectionReference number_of_reports = fstorage.collection("reported_client_logs");

                    final Query reports = number_of_reports.whereEqualTo("client_id", fAuth.getCurrentUser().getUid());
                   reports.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            long total_number_of_reports = queryDocumentSnapshots.size();

                            if (total_number_of_reports == 1) {

                                warning_dialog();

                            }else if (total_number_of_reports == 2) {

                                alert_dialog ();

                            }else {
                                //log in succesfully
                                dialogbox();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                service_id.client_id = fAuth.getCurrentUser().getUid();
                                finish();
                                //log in succesfully
                            }

                        }
                    });



                }else {
                    service_id.client_id = fAuth.getCurrentUser().getUid();
                    startActivity(new Intent(getApplicationContext(),map.class));
                    finish();
                }
            }
        });

    }






    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    checkUser();


                }else {
                    //Toast.makeText(log_in.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    otp.setError("Invalid Verification Code.");
                    verify.setEnabled(true);
                    verify.setText("Verify");
                }
            }
        });
    }








    private void requestOTP(String phonenumber) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    sending.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    otp.setVisibility(View.VISIBLE);
                    verification = s;
                    Token = forceResendingToken;
                    verify.setText("Verify");
                    verificationProgress = true;

                }

                @Override
                public void onCodeAutoRetrievalTimeOut(String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(log_in.this,"Cannot Create an Account" + e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });

        }




    private boolean requestPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ispermission = true;


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            ispermission = false;
                        }

                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return ispermission;
    }


    public void dialogbox () {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_sign_in);
        dialog.show();

    }


    public void alert_dialog () {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.show();

        Button Close =  dialog.findViewById(R.id.close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_in.this.finish();
                System.exit(0);
            }
        });

    }



    public void warning_dialog () {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.warning_dialog);
        dialog.show();

        Button Close =  dialog.findViewById(R.id.close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log in succesfully
                dialog.dismiss();
                //dialogbox();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                service_id.client_id = fAuth.getCurrentUser().getUid();
                finish();
                //log in succesfully
            }
        });

    }










}

