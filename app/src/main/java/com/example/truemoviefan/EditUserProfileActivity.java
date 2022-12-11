package com.example.truemoviefan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditUserProfileActivity extends AppCompatActivity {

    EditText edEditUserName, edEditFullName, edEditUserEmail, edEditPassword;
    ImageView imEditAccount, imLogoEditUserProfile;
    Button btnSave, btnCancel, btnEditIm;

    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    String userId;

    //To upload the image file into Firebase database
    FirebaseStorage storage;
    StorageReference storageReference,sRef;

    ActivityResultLauncher actRes;
    Uri filepath;
    ProgressDialog prDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        initialize();
    }

    private void initialize() {
        edEditUserName = findViewById(R.id.edEditUserName);
        edEditFullName = findViewById(R.id.edEditFullName);
        edEditUserEmail = findViewById(R.id.edEditUserEmail);
        edEditPassword = findViewById(R.id.edEditPassword);
        imEditAccount = findViewById(R.id.imEditAccount);
        imLogoEditUserProfile = findViewById(R.id.imLogoEditUserProfile);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnEditIm = findViewById(R.id.btnEditIm);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        userId = fAuth.getCurrentUser().getUid();

        //Initialize the FireBase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Display the current user's information.
        DocumentReference documentReference = db.collection("user").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                edEditUserName.setText(value.getString("username"));
                edEditFullName.setText(value.getString("fullname"));
                edEditUserEmail.setText(value.getString("email"));
                edEditPassword.setText(value.getString("password"));

                //Fetching user's photo
                String img_url = value.getString("photo");
                Picasso.with(EditUserProfileActivity.this)
                        .load(img_url)
                        .into(imEditAccount);
            }
        });

        actRes = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Display the selected picture from the device
                        if (result.getResultCode() == RESULT_OK && result.getData() != null){

                            filepath = result.getData().getData();
                            try{

                                Bitmap bitmap = MediaStore.Images.Media.getBitmap
                                        (getContentResolver(), filepath);

                                imEditAccount.setImageBitmap(bitmap);


                            }catch (Exception e) {

                                Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }
        );


        btnEditIm.setOnClickListener(view -> {

            // Browsing a photo
            Intent intent = new Intent();

            // find an image on a device
            intent.setType("image/*");

            //Take a photo
            intent.setAction(Intent.ACTION_GET_CONTENT);
            actRes.launch(Intent.createChooser(intent, "Please, select a photo"));

            uploadPhoto();
        });

        btnSave.setOnClickListener(view -> {
            if(edEditUserName.getText().toString().isEmpty() || edEditFullName.getText().toString().isEmpty()
                    || edEditUserEmail.getText().toString().isEmpty() || edEditPassword.getText().toString().isEmpty()){
                Toast.makeText(this, "One or Many fields are empty.", Toast.LENGTH_LONG).show();
            }

            final String email = edEditUserEmail.getText().toString();
            final String passw = edEditPassword.getText().toString();

            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    user.updatePassword(passw).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            DocumentReference docReference = db.collection("user").document(user.getUid());
                            Map<String, Object> updateUser = new HashMap<>();
                            updateUser.put("username",edEditUserName.getText().toString());
                            updateUser.put("fullname", edEditFullName.getText().toString());
                            updateUser.put("email", email);
                            updateUser.put("password", passw);
                            //updateUser.put("photo", url);
                            docReference.update(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditUserProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                            });
                        }
                    });
                    //Toast.makeText(EditUserProfileActivity.this, "Email is changed", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        btnCancel.setOnClickListener(view -> {
            startActivity(new Intent(this,UserProfileActivity.class));
        });

        imLogoEditUserProfile.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
        });
    }

    private void uploadPhoto() {
        if(filepath != null){
            prDialog = new ProgressDialog(this);
            prDialog.setTitle("Uploading the photo in progress...");
            prDialog.show();

            //Store the image in store of FireBase (UUID -> create a random image name)
            sRef = storageReference.child("image/" + UUID.randomUUID());

            //Upload the file then show its status (Success or Failure)
            sRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    prDialog.dismiss();

                    Toast.makeText(EditUserProfileActivity.this, "The photo has been uploaded successfully!...",
                            Toast.LENGTH_LONG).show();

                    sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            DocumentReference docReference = db.collection("user").document(user.getUid());
                            Map<String, Object> updateUser = new HashMap<>();
                            updateUser.put("photo", "https://www.nicepng.com/png/detail/6-65582_white-small-dog-small-dog-png.png");
                            //updateUser.put("photo", "https://www.nicepng.com/png/detail/76-762284_boss-baby-junior-novelization-boss-baby-movie.png");
                            //updateUser.put("photo", url);
                            docReference.update(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditUserProfileActivity.this, "Profile Photo updated", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),EditUserProfileActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                   });
                }
            });
        }
    }
}
