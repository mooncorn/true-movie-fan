package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Api.MovieApiClient;
import Model.Movie;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivMovie1;
    TextView tvTitleMovie1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

/*        btnMovie = findViewById(R.id.btn);
        etMovieId = findViewById(R.id.etMovieId);
        btnMovie.setOnClickListener(view -> {
            Intent i = new Intent(this, MovieActivity.class);
            i.putExtra(MovieActivity.IMDB_ID, etMovieId.getText().toString());
            startActivity(i);
        });*/
    }

    public void initialize() {
        ivMovie1 = findViewById(R.id.ivMovie1);
        tvTitleMovie1 = findViewById(R.id.tvTitleMovie1);

        // Set OnClickListener
        ivMovie1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivMovie1: callMovieActivity(); break;
        }
    }

    private void callMovieActivity() {
        Intent i = new Intent(this, MovieActivity.class);
        i.putExtra(MovieActivity.IMDB_ID, tvTitleMovie1.getText().toString());
        startActivity(i);
    }
}