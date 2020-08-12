package com.example.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class RescueSignUpActivity extends AppCompatActivity {

    private Spinner spinnerOrganization;
    String volunteerName;
    String volunteerPhone ;
    String volunteerEmail ;
    ArrayList<String> spinnerArrayList;
    private Button btnVolunteerSignUp;
    private ArrayList<String> orgNames;
    private ArrayList<String> orgUniqueCodes;
    private EditText editTextVolunteerName, editTextVolunteerPhone, editTextVolunteerEmail, editTextVolunteerOrgCode;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_sign_up);

        spinnerOrganization = findViewById(R.id.spinnerOrganization);
        btnVolunteerSignUp = findViewById(R.id.btnVolunteerSignUp);
        editTextVolunteerName = findViewById(R.id.editTextVolunteerName);
        editTextVolunteerPhone = findViewById(R.id.editTextVolunteerPhone);
        editTextVolunteerEmail = findViewById(R.id.editTextVolunteerEmail);
        editTextVolunteerOrgCode = findViewById(R.id.editTextVolunteerOrgCode);
        orgNames = new ArrayList<>();
        orgUniqueCodes = new ArrayList<>();
        setUpOrgNames();
        setUpSpinnerOrganization();

        btnVolunteerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkVolunteerOrgCode();
            }
        });

    }

    public void checkVolunteerOrgCode(){

        volunteerName = editTextVolunteerName.getText().toString().trim();
        volunteerPhone = editTextVolunteerPhone.getText().toString().trim();
        volunteerEmail = editTextVolunteerEmail.getText().toString().trim();
        final String volunteerOrgCode = editTextVolunteerOrgCode.getText().toString().trim();
        int volunteerOrgPos = spinnerOrganization.getSelectedItemPosition();

        if(TextUtils.isEmpty(volunteerName)){
            editTextVolunteerName.setError("Please enter your name !");
            editTextVolunteerName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(volunteerPhone)){
            editTextVolunteerPhone.setError("Please enter your Phone No. !");
            editTextVolunteerPhone.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(volunteerOrgCode) || volunteerOrgCode.length()<6){
            editTextVolunteerOrgCode.setError("Please enter correct Organization Unique Code !");
            editTextVolunteerOrgCode.requestFocus();
            return;
        }
        if(volunteerOrgPos==0){
            Snackbar snackbar = Snackbar.make(spinnerOrganization,"Please select your organization",Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.show();
            return;
        }

        if(!checkPhoneNo(volunteerPhone)){
            Snackbar snackbar = Snackbar.make(spinnerOrganization,"Please enter 10 digit Phone Number",Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.show();
            return;
        }
        if(orgUniqueCodes.get(volunteerOrgPos).equals(volunteerOrgCode)){
            startPhoneVerification(volunteerOrgPos);
        }
        else{
            String msg = "Organization code not correct :(";
            Snackbar snackbar = Snackbar.make(spinnerOrganization,msg,Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.show();
        }
    }

    public void startPhoneVerification(int volunteerOrgPos){
        Intent intent = new Intent(RescueSignUpActivity.this,PhoneVerificationActivity.class);
        intent.putExtra("phoneNo",volunteerPhone);
        intent.putExtra("email",volunteerEmail);
        intent.putExtra("name",volunteerName);
        intent.putExtra("isSignUp",true);
        intent.putExtra("volunteerOrganization",spinnerArrayList.get(volunteerOrgPos));
        startActivity(intent);
    }

    public void retrieveUniqueCodes(int volunteerOrgPos,final String volunteerOrgCode){

        firebaseFirestore.collection("RescueOrganization")
                .document(orgNames.get(volunteerOrgPos))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null && documentSnapshot.exists()) {
                    String orgUniqueCode = documentSnapshot.getString("uniqueCode");
                    assert orgUniqueCode != null;
                    String msg = "";
                    if(orgUniqueCode.equals(volunteerOrgCode)){
                        msg = "Success";
                    }else{
                        msg = "Organization code not correct :(";
                    }
                    Snackbar snackbar = Snackbar.make(spinnerOrganization,msg,Snackbar.LENGTH_LONG);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    snackbar.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ERROR404", Objects.requireNonNull(e.toString()));
                Snackbar snackbar = Snackbar.make(spinnerOrganization,"Some error occurred, try again :)",Snackbar.LENGTH_LONG);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackbar.show();
            }
        });
    }

    public boolean checkPhoneNo(String phoneNo){

        return phoneNo.matches("[0-9]{10}");
    }

    public void setUpSpinnerOrganization(){

        spinnerArrayList = new ArrayList<>();
        spinnerArrayList.add("Select your organization");
        spinnerArrayList.add("PFA Durg");
        spinnerArrayList.add("PFA Bhilai");
        spinnerArrayList.add("Pune NGO");
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(RescueSignUpActivity.this,android.R.layout.simple_spinner_dropdown_item,spinnerArrayList);
        spinnerOrganization.setAdapter(arrayAdapter);
    }

    public void setUpOrgNames(){
        orgNames.add("Select your organization");
        orgNames.add("PFA_DURG");
        orgNames.add("PFA_BHILAI");
        orgNames.add("PUNE_NGO");

        orgUniqueCodes.add("");
        orgUniqueCodes.add("ay8p3m");
        orgUniqueCodes.add("qn6z7j");
        orgUniqueCodes.add("vt71rb");
    }
}