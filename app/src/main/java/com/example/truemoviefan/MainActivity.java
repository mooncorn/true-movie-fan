package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import Api.MovieApiClient;
import Model.Movie;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MainActivity";

    String movieIds[] = {"tt6443346","tt13433812", "tt21195490", "tt13443470", "tt9114286", "tt0468569"};

    MovieApiClient movieClient;

    TextView tvSearchBar;

    ImageView ivLogo, ivUser;

    RecyclerView rvCovers;

    List<Movie> movies;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    public void initialize() {
        tvSearchBar = findViewById(R.id.etSearchBar);
        rvCovers = findViewById(R.id.rvCovers);
        ivLogo = findViewById(R.id.ivLogo);
        ivUser = findViewById(R.id.ivUser);

        fAuth = FirebaseAuth.getInstance();

        tvSearchBar.setOnClickListener(view -> {
            fetchSearchMovies();
        });

        ivLogo.setOnClickListener(view -> {
            CoversAdapter adapter = new CoversAdapter(movies);
            rvCovers.setAdapter(adapter);
            rvCovers.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        });

        ivUser.setOnClickListener(view -> {
            if(fAuth.getCurrentUser() != null){
                startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                finish();
            }else {
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
            }
        });

        fetchFeedMovies();

        //logout
        //FirebaseAuth.getInstance().signOut();
    }

    private void fetchFeedMovies() {
        movies = new ArrayList<>();
        movieClient = new MovieApiClient(this);

        // Fetch information using the ImdbId
        for (int i = 0; i < movieIds.length ; i++) {
            movieClient.findMovie(movieIds[i], new MovieApiClient.MovieApiClientCallback<Movie>() {
                @Override
                public void success(Movie data) {
                    movies.add(data);

                    CoversAdapter adapter = new CoversAdapter(movies);
                    rvCovers.setAdapter(adapter);
                    rvCovers.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                }

                @Override
                public void error(String message) {
                    if (message != null) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });
        }
    }

    private void fetchSearchMovies() {
        movieClient = new MovieApiClient(this);

        movieClient.search(tvSearchBar.getText().toString(), new MovieApiClient.MovieApiClientCallback<List<Movie>>() {
            @Override
            public void success(List<Movie> data) {
                CoversAdapter adapter = new CoversAdapter(data);
                rvCovers.setAdapter(adapter);
                rvCovers.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            }

            @Override
            public void error(String message) {
                Log.d(TAG, message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                // TODO: App crashing when records are not found even when implementing this method
/*                if (message != null) {
                    Toast.makeText(MainActivity.this, "No movie was found under the description provided", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }
}