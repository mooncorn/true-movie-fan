package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnMovie;
    EditText etMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMovie = findViewById(R.id.btnMovie);
        etMovieId = findViewById(R.id.etMovieId);
        btnMovie.setOnClickListener(view -> {
            Intent i = new Intent(this, MovieActivity.class);
            i.putExtra(MovieActivity.IMDB_ID, etMovieId.getText().toString());
            startActivity(i);
        });
    }
}