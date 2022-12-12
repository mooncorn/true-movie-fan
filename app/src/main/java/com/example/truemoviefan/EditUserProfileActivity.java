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
    StorageReference storageReference;

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

                            // Update the current user's information
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

        btnEditIm.setOnClickListener(view -> {

            //OpenGallery
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent,1000);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //imEditAccount.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Upload image to Firebase storage
        StorageReference fileRef = storageReference.child("users/" + userId + "Profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(EditUserProfileActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(EditUserProfileActivity.this).load(uri).into(imEditAccount);
                        Map<String, Object> updateUser = new HashMap<>();
                        updateUser.put("photo", uri.toString());

                        // Update the current user's photo
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        db.collection("user").document(user.getUid()).update(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(EditUserProfileActivity.this,"Upload Successfully: " + uri.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditUserProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
