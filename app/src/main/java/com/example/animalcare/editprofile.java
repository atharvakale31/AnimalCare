package com.example.animalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class editprofile extends AppCompatActivity {

    EditText nameT,aboutT;
    TextView contactT;
    Button save;
    FirebaseDatabase mydatabase;
    StorageReference store;
    ProgressBar mprogressbar;
    profileData a;
    Uri selectedImage;
    CircleImageView image;
    DatabaseReference dr;
    profileData records;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    private byte[] byteArray;
    private  int isHome=0;
    private FirebaseFirestore firestore;

    FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();


    public void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent,1);
    }
    CircleImageView imagedef;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
                //upload();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        firestore = FirebaseFirestore.getInstance();
        isHome = getIntent().getIntExtra("home",0);
        Log.i("ISHOME","->"+isHome);
        imagedef=(CircleImageView) findViewById(R.id.profileImage);
        imagedef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else
                {
                    getPhoto();
                    //upload();
                }
            }
        });

        nameT =findViewById(R.id.nametxt);
        aboutT=findViewById(R.id.abouttxt);
        contactT=findViewById(R.id.contactno);

        Intent profileDataIntent = getIntent();
        nameT.setText(profileDataIntent.getStringExtra("name"));
        aboutT.setText(profileDataIntent.getStringExtra("about"));
        contactT.setText(profileDataIntent.getStringExtra("contact"));
        byteArray = profileDataIntent.getByteArrayExtra("image");
        bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        if(bitmap!=null)
            imagedef.setImageBitmap(bitmap);
        save=findViewById(R.id.Savebtn);
        mydatabase=FirebaseDatabase.getInstance();



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(editprofile.this);
                progressDialog.setMessage("Upadting your profile, Please Wait :)");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String name = nameT.getText().toString();
                String about = aboutT.getText().toString();
                String no = contactT.getText().toString();
                //url();

                store = FirebaseStorage.getInstance().getReference("ProfileImg");
                //upload();
                records = new profileData(name, about, no);



                if (!name.isEmpty() && !about.isEmpty() && !no.isEmpty() && bitmap != null) {
//                    if(bitmap==null){
//                        Toast.makeText(editprofile.this,"Please Select a Image",Toast.LENGTH_LONG).show();
//                        progressDialog.cancel();
//                        return;
//                    }else {
                    //editProfileData(records);
                    upload(records);

                } else {
                    Toast.makeText(editprofile.this, "Fill all Details", Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                }

            }


        });


    }

    public void editProfileData(profileData records){

        firestore.collection("ProfileData").document(u.getUid()).set(records).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.cancel();
                Toast.makeText(editprofile.this, "Successfully Saved", Toast.LENGTH_LONG).show();
                if(isHome ==0) {
                    //Intent i = new Intent(editprofile.this , HomeActivity.class);
                    finish();
                    //startActivity(i);
                }else  if(isHome==1){
                    Intent i = new Intent(editprofile.this , HomeActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(editprofile.this, "Unsuccessfull,Please Try Again !", Toast.LENGTH_LONG).show();
            }
        });
    }

 private void upload(final profileData profileData)
 {
     if(byteArray!=null && byteArray.length>0) {
         final StorageReference ref = store.child(u.getUid());
         Log.i("IMGURI", "->" + selectedImage);
         ref.putBytes(byteArray)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                         ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 String downloadUrl = uri.toString();
                                 profileData.setImageUrl(downloadUrl);
                                 editProfileData(profileData);
                                 Toast.makeText(editprofile.this, "Photo uploaded", Toast.LENGTH_LONG).show();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(editprofile.this, "Erro uploading photo", Toast.LENGTH_LONG).show();
                                 progressDialog.cancel();
                             }
                         });

                         Log.i("yessss", "onSuccess");

                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception exception) {
                         Toast.makeText(editprofile.this, "Error while Uploading Photo,Please Try Again", Toast.LENGTH_LONG).show();
                         progressDialog.cancel();
                         Log.i("NOOOOO", "onFailure: ");
                     }
                 });
     }
     else {
         Toast.makeText(editprofile.this, "Please choose a photo", Toast.LENGTH_LONG).show();
     }
 }

    public byte[] getImageBitmapByteArray(Bitmap bitmapProfileImage){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data !=null)
        {
            selectedImage=data.getData();

            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                image = findViewById(R.id.profileImage);
                image.setImageBitmap(bitmap);
                byteArray = getImageBitmapByteArray(bitmap);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

