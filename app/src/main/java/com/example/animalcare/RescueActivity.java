package com.example.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RescueActivity extends AppCompatActivity {

    Button nav,btnCancelRescue, btnRescueCompleted;
    private ImageButton btnCallHelpSeeker;
    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private String a, b, c, d = "0", e = "0", f;
    private String url,rescuerUid,rescueDocumentId,cityType;
    private boolean isAccepted;
    private boolean isCompleted;
    TextView name, phone, animal, loc,textViewDescription,textViewTime;
    ImageView animalImage;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);
        nav=findViewById(R.id.navigate);
        animalImage=findViewById(R.id.animalImage);
        btnCallHelpSeeker = findViewById(R.id.btnCallHelpSeeker);
        btnCancelRescue = findViewById(R.id.btnCancelRescue);
        btnRescueCompleted = findViewById(R.id.btnRescueCompleted);
        bundle = getIntent().getExtras();
        firebaseFirestore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.RuserName);
        phone = findViewById(R.id.Rphone);
        //latLng = findViewById(R.id.Rlatlng);
        loc = findViewById(R.id.Rlocation);
        animal = findViewById(R.id.Ranimal);
        textViewDescription = findViewById(R.id.textViewDesc);
        textViewTime = findViewById(R.id.textViewTime);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(RescueActivity.this);
        progressDialog.setTitle("Cancel Rescue");
        progressDialog.setMessage("Canceling your rescue help....");
        progressDialog.setCancelable(false);
        Toast.makeText(this, "Rescue Activity !", Toast.LENGTH_SHORT).show();


        if(bundle!=null){
            if(bundle.getString("FromWhere","NO").equals("NOTIF")) {
                a = bundle.getString("USERNAME", "UserName");
                b = bundle.getString("PHONENO", "Phone No");
                c = bundle.getString("ANIMALTYPE", "Animal Type");
                d = bundle.getString("LAT", "0");
                e = bundle.getString("LNG", "0");
                f = bundle.getString("Location", "Location?");
                url=bundle.getString("URL",null);
                cityType=bundle.getString("cityType",null);
                rescuerUid=bundle.getString("rescuerUid",null);
                rescueDocumentId=bundle.getString("rescueDocumentId",null);
                isAccepted = bundle.getBoolean("accepted",false);
                isCompleted = bundle.getBoolean("isCompleted",false);

                if(isAccepted && rescuerUid!=null && rescuerUid.equals(firebaseUser.getUid())
                        && !isCompleted){
                    btnCancelRescue.setVisibility(View.VISIBLE);
                    btnRescueCompleted.setVisibility(View.VISIBLE);
                }

                name.setText(a);
                phone.setText(b);
                loc.setText(f);
                animal.setText(c);
                textViewDescription.setText(bundle.getString("description","Help Description"));
                textViewTime.setText(bundle.getString("time","Help Time"));
                //latLng.setText("LAT: " + d + "\n" + "LNG: "+e);
                System.out.println("URL OF IMAGE "+url);
                Glide.with(getApplicationContext())
                        .load(url)
                        .error(new AnimalImageUtil().getAnimalImageResourceId(c))
                        .placeholder(R.drawable.loadingimages)
                        .into(animalImage);

                Toast.makeText(this, "Rescue Details Loaded", Toast.LENGTH_SHORT).show();
                nav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+d+","+e+"");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        startActivity(mapIntent);

                    }
                });

                btnCallHelpSeeker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        String uri = "tel:" + b.trim();
                        callIntent.setData(Uri.parse(uri));
                        startActivity(callIntent);
                    }
                });

                btnCancelRescue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(rescuerUid!=null && rescuerUid.equals(firebaseUser.getUid())
                                && rescueDocumentId!=null && cityType!=null && !isCompleted){
                            createAlertDialogDecline();
                        }
                    }
                });

                btnRescueCompleted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(rescuerUid!=null && rescuerUid.equals(firebaseUser.getUid())
                                && rescueDocumentId!=null && cityType!=null){
                            createAlertDialogCompleted();
                        }
                    }
                });

            }
        }
        Log.i("GETISHERE","OKOK");
    }

    public void completeRescue(){

        final ProgressDialog completedProgressDialog = new ProgressDialog(RescueActivity.this);
        completedProgressDialog.setTitle("Rescue Task Completed");
        completedProgressDialog.setMessage("Good job rescuing a animal :)");
        completedProgressDialog.setCancelable(false);
        completedProgressDialog.show();

        Map<String, Object> updateRescueData = new HashMap<>();
        updateRescueData.put("accepted", true);
        updateRescueData.put("rescueStatus", "Rescue Task Completed");
        updateRescueData.put("isCompleted", true);
        firebaseFirestore.collection("Cases")
                .document("Topic")
                .collection(cityType)
                .document(rescueDocumentId)
                .update(updateRescueData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (completedProgressDialog.isShowing())
                    completedProgressDialog.cancel();
                Toast.makeText(RescueActivity.this, "Rescue Completed, Good Job :)", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (completedProgressDialog.isShowing())
                    completedProgressDialog.cancel();
                Toast.makeText(RescueActivity.this, "Please try again by clicking completed button !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancelRescue(){

        progressDialog.show();

            Map<String, Object> updateRescueData = new HashMap<>();
            updateRescueData.put("accepted", false);
            updateRescueData.put("rescueStatus", "Waiting for rescue");
            updateRescueData.put("rescuerUid", "null");
        updateRescueData.put("isCompleted", false);
            firebaseFirestore.collection("Cases")
                    .document("Topic")
                    .collection(cityType)
                    .document(rescueDocumentId)
                    .update(updateRescueData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (progressDialog.isShowing())
                        progressDialog.cancel();
                    Toast.makeText(RescueActivity.this, "Rescue declined!", Toast.LENGTH_SHORT).show();
                    finish();
                    //startActivity(new Intent(RescueActivity.this,VolunteerHomeActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.cancel();
                    Toast.makeText(RescueActivity.this, "Failed to update rescue details", Toast.LENGTH_SHORT).show();
                }
            });
        }

    public void createAlertDialogCompleted(){
        new AlertDialog.Builder(RescueActivity.this)
                .setTitle("Completed rescue task")
                .setMessage("Is rescue task completed ?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        completeRescue();
                    }
                })
                .setNegativeButton("NO",null)
                .show();
    }

        public void createAlertDialogDecline(){
        new AlertDialog.Builder(RescueActivity.this)
                .setTitle("Decline help request")
                .setMessage("Do you want to cancel rescue ?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelRescue();
                    }
                })
                .setNegativeButton("NO",null)
                .show();
    }

    }

