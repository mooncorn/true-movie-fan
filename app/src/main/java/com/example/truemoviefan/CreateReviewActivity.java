package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * Display a form so that a user can create a review for the given movie ID.
 */
public class CreateReviewActivity extends AppCompatActivity {

    EditText tvReviewContent;
    Button btnCancel, btnPost;

    String imdbID;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        initialize();
    }

    private void initialize() {
        db = FirebaseFirestore.getInstance();

        // Receive imdbID
        imdbID = getIntent().getStringExtra(MovieActivity.IMDB_ID);

        tvReviewContent = findViewById(R.id.etReviewContent);
        btnCancel = findViewById(R.id.btnCancel);
        btnPost = findViewById(R.id.btnPost);

        btnCancel.setOnClickListener(view -> goBackToMoviePage());
        btnPost.setOnClickListener(view -> postReview());
    }

    private void goBackToMoviePage() {
        Intent i = new Intent(this, MovieActivity.class);
        i.putExtra(MovieActivity.IMDB_ID, imdbID);
        startActivity(i);
    }

    private void postReview() {
        String content = tvReviewContent.getText().toString();
        // TODO: get information about current user

        Map<String, Object> review = new HashMap<>();
        review.put("content", content);
        review.put("imdbId", imdbID);
        review.put("username", "dasior");
        review.put("avatar", "https://openseauserdata.com/files/404b572aba61cab8f62d77786f341993.jpg");

        // Add a new document with a generated ID
        db.collection("review")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Log.d("CreateReviewActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                    goBackToMoviePage();
                })
                .addOnFailureListener(e -> {
                    Log.w("CreateReviewActivity", "Error adding document", e);
                    Toast.makeText(CreateReviewActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                    goBackToMoviePage();
                });
    }
}