package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMovie = findViewById(R.id.btnMovie);
        btnMovie.setOnClickListener(view -> {
            Intent i = new Intent(this, MovieActivity.class);
            i.putExtra(MovieActivity.IMDB_ID, "tt4154796");
            startActivity(i);
        });
    }
}