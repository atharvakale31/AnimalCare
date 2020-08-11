package com.example.animalcare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpSectionFragment extends Fragment {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private TextView userLocationTv;
    private EditText desc;
    private List<Address> addressList;
    private Button getHelpBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase  firebaseDatabase;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private Location location;
    private ImageView animalImageView;
    private Bitmap animalBitmap;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference myDatabaseRef;
    private String uniqueId;
    private Spinner spinner;
    private Spinner spinner2;
    private String animalType, Pname="name",Pno="0",url,cityType;
    private Uri imageUri;

    private ProgressDialog progressDialog;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAZki8rck:APA91bGIdYfinRDbRf51zGXOfIdZlFFZsswRgjaCn3DqJF2WSwXlRo_oW-EHtO7MQ-jjJDeFlhzB_6nLx2Gayy6ht7p0M0oiGCc9N1fnKa-sRPbpCuuNCfFKUAE4NlDegoYpabMexzSS";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

   private String NOTIFICATION_TITLE;
   private String NOTIFICATION_MESSAGE;
   private String TOPIC;
    //AnimalHelpCase helpCase;


    public HelpSectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_section, container, false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("HEREIS","FALSE");
            getLiveLocation();
        }

        if(requestCode == 2 &&  grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED ){
            getImageFromCamera();
        }

    }

    public void getLiveLocation() {

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i("HEREIS","TRUE");
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }


        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location==null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Geocoder geocoder =new Geocoder(getContext());
        try {
            if(location != null) {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addressList.get(0) != null) {
                    Address address = addressList.get(0);
                    //Log.i("USERLOCATION", address.getThoroughfare() + " <->" + address.getSubThoroughfare());
                    userLocationTv.setText(address.getAddressLine(0));
                    Log.i("USERLOC", addressList.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("HEREIS","TRUE");
        Log.i("IMGURI","-> "+ imageUri.toString());

        if(requestCode == 1 && resultCode == RESULT_OK ) {

            try {
                animalBitmap = MediaStore.Images.Media.getBitmap(
                        getContext().getContentResolver(), imageUri);
                animalImageView.setImageBitmap(animalBitmap);
                Log.i("IMGURI","-> "+ imageUri.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void sendNotify(AnimalHelpCase helpCase){

        TOPIC ="/topics/animalhelp";// "/topics/userABC"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "ANIMAL HELP REQUIRED";//edtTitle.getText().toString();
        NOTIFICATION_MESSAGE = "This animal is injured , Please Help !";//edtMessage.getText().toString();

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            //if(helpCase.getUserName()!=null){
            Log.i("HELPSECTIONACTUSER",helpCase.getUserName());
            Log.i("HELPSECTIONACTANI",helpCase.getAnimalType());
            notifcationBody.put("userName",Pname);
            notifcationBody.put("mobileNo",Pno);
            notifcationBody.put("url",helpCase.getPhotourl());
            notifcationBody.put("animalType",helpCase.getAnimalType());
            notifcationBody.put("location",helpCase.getUserLocation());
            notifcationBody.put("lat",String.valueOf(helpCase.getLatitude()));
            notifcationBody.put("lng",String.valueOf(helpCase.getLongitude()));
            notifcationBody.put("description",String.valueOf(helpCase.getDesc()));
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("h:mm a dd-MMM-yyyy ");
            String formattedDate = df.format(c);
            notifcationBody.put("time", formattedDate);
            //}

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Toast.makeText(getContext(), "Request sent Successfully", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void getImageFromCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Log.i("IMGURI2","IMAGE URL "+ imageUri.toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
        Log.i("IMGURI2","IMAGE URL"+ imageUri.toString());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        desc=view.findViewById(R.id.desc);
        userLocationTv = view.findViewById(R.id.TvLocation);
        animalImageView = view.findViewById(R.id.imageViewAnimal);
        getHelpBtn = view.findViewById(R.id.BtnGetHelp);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        myDatabaseRef=FirebaseDatabase.getInstance().getReference("ProfileData").child(firebaseAuth.getCurrentUser().getUid());
        final ArrayList<String> animals = new ArrayList<>();
        animals.add("Select Animal");
        animals.add("Dog");
        animals.add("Cat");
        animals.add("Cow");
        animals.add("Snake");

        final ArrayList<String> City = new ArrayList<>();
        City.add("Select City");
        City.add("Bhilai");
        City.add("Durg");
        City.add("Pune");

        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,animals);
        ArrayAdapter<String> arrayAdapter2
                = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,City);


        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);

        spinner2 = view.findViewById(R.id.city_spinner);
        spinner2.setAdapter(arrayAdapter2);


        final ImageButton locationBtn = view.findViewById(R.id.BtnGetLocation);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }
                else{
                    getLiveLocation();
                }
            }
        });


        animalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                }
                else{
                    getImageFromCamera();
                }
            }
        });

        getHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animalType = spinner.getSelectedItem().toString();
                cityType = spinner2.getSelectedItem().toString();


                if(userLocationTv.getText().toString().equals("Location Details")){
                    Toast.makeText(getContext(), "Please update your Live Location !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(animalType.equals("Select Animal")){
                    Toast.makeText(getContext(), "Please select Animal Type !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cityType.equals("Select City")){
                    Toast.makeText(getContext(), "Please select City Type !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(animalBitmap==null){
                    Toast.makeText(getContext(), "Please capture the Image of injured Animal !", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!userLocationTv.getText().toString().equals("Locations Details") && !animalType.equals("Select Animal")&& !cityType.equals("Select city")) {

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Getting help");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    uniqueId = AnimalRescueUtil.generateAutoId();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    animalBitmap.compress(Bitmap.CompressFormat.JPEG,65, byteArrayOutputStream);
                    byte[] animalImageByteArray = byteArrayOutputStream.toByteArray();
                    storageReference = firebaseStorage.getReference("Animal Case Images").child(uniqueId);
                    UploadTask uploadTask = storageReference.putBytes(animalImageByteArray);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Toast.makeText(getContext(), "Image Uploading Successful !", Toast.LENGTH_SHORT).show();

                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            url=uri.toString();

                                            getProfileData(url);

                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(getContext(), "Image Uploading Failed !", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }


    public void uploadData(String url,String userName,String userNo){
        animalType = spinner.getSelectedItem().toString();
        cityType = spinner2.getSelectedItem().toString();

        if(!userLocationTv.getText().toString().equals("Location Details") && !userLocationTv.getText().toString().equals("")){
            if(location!=null && !animalType.equals("Select Animal") && !cityType.equals("Select City")) {
                //String UserName = firebaseAuth.getCurrentUser().getEmail();
                String userLocation = userLocationTv.getText().toString();
                String description=desc.getText().toString();
                double lat = location.getLatitude();
                double lng = location.getLongitude();
               final AnimalHelpCase helpCase = new AnimalHelpCase(userName,animalType,cityType,userLocation,lat,lng,url,false,description);
               helpCase.setUserNo(userNo);
               if(firebaseUser!=null)
                helpCase.setUserUid(firebaseUser.getUid());
                Log.d(TAG, "url: "+ url);
                db.collection("Cases").document("Topic").collection(cityType).document(uniqueId).set(helpCase).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            sendNotify(helpCase);
                            Toast.makeText(getContext(), "Case Created Succesfully", Toast.LENGTH_SHORT).show();
                            Log.i("HELPCASE","Case Created Succesfully");
                        }
                        else {
                            progressDialog.cancel();
                            Toast.makeText(getContext(), "Can't create a case", Toast.LENGTH_SHORT).show();
                            Log.i("HELPCASE","Can't create a case");
                        }

                    }
                });

            }
        }
    }

    public void getProfileData(final String url){
        db.collection("ProfileData")
                .document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists() && documentSnapshot.get("name")!=null) {
                            Pname = documentSnapshot.get("name").toString();
                            Pno = documentSnapshot.get("no").toString();
                            uploadData(url,Pname,Pno);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(progressDialog.isShowing())
                    progressDialog.cancel();
                Toast.makeText(getContext(), "failed sending request", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
