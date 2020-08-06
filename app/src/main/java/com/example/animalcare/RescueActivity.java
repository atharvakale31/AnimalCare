package com.example.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

public class RescueActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button rescueLogOut,nav;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String a,b,c,d="0",e="0",f,url;
    TextView name,phone,animal,latLng,loc;
    ImageView animalImage;
    GoogleMap mMap;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    LocationManager locationManager;
    LocationListener locationListener;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);
        nav=findViewById(R.id.navigate);
        animalImage=findViewById(R.id.animalImage);
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng yourlocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(yourlocation).title("Your Position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourlocation));
                Log.i("Location",location.toString());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        rescueLogOut = findViewById(R.id.BtnRescueLogout);
        name = findViewById(R.id.RuserName);
        phone = findViewById(R.id.Rphone);
        latLng = findViewById(R.id.Rlatlng);
        loc = findViewById(R.id.Rlocation);
        animal = findViewById(R.id.Ranimal);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Toast.makeText(this, "Rescue Activity !", Toast.LENGTH_SHORT).show();

        rescueLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Toast.makeText(RescueActivity.this, "LogOut Successfull", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(RescueActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });




        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            //Log.i("OKISHERE","HOLA");
            if(bundle.getString("FromWhere","NO").equals("NOTIF")) {
                a = bundle.getString("USERNAME", "UserName");
                b = bundle.getString("PHONENO", "Phone No");
                c = bundle.getString("ANIMALTYPE", "Animal Type ?");
                d = bundle.getString("LAT", "Lati");
                e = bundle.getString("LNG", "Longi");
                f = bundle.getString("Location", "Location?");
                url=bundle.getString("URL",null);
                //Log.i("GETFINALHERE", a + " " + b + " " + c + " " + d + " " + e + " " + f);
                name.setText(a);
                phone.setText(b);
                loc.setText(f);
                animal.setText(c);
                latLng.setText(d + "," +e);
                System.out.println("URL OF IMAGE "+url);
                Glide.with(this)
                        .load(url)
                                .into(animalImage);
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                mapFragment.getMapAsync(this);
                Toast.makeText(this, "Rescue Details Loaded", Toast.LENGTH_SHORT).show();
                nav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+d+","+e+"");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        startActivity(mapIntent);
                        //"google.navigation:q=Taronga+Zoo,+Sydney+Australia"

                    }
                });






            }
        }
        Log.i("GETISHERE","OKOK");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            mMap = googleMap;


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng yourlocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(yourlocation).title("Your Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(yourlocation));
            double lat =Double.parseDouble(d);
            double lon=Double.parseDouble(e);
            Toast.makeText(this, "IN MAP", Toast.LENGTH_SHORT).show();
            // Add a marker in Sydney and move the camera
            LatLng rescue=new LatLng(lat,lon);
           mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rescue,13));

        }
        


    }
}
