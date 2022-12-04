package com.example.truemoviefan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Api.MovieApiClient;
import Model.Movie;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView[] listOfImageViews;
    int posters[] = {R.id.ivMovie1, R.id.ivMovie2, R.id.ivMovie3, R.id.ivMovie4, R.id.ivMovie5, R.id.ivMovie6};

    TextView[] listOfTitles;
    int titles[] = {R.id.tvTitleMovie1, R.id.tvTitleMovie2, R.id.tvTitleMovie3, R.id.tvTitleMovie4, R.id.tvTitleMovie5, R.id.tvTitleMovie6};

    TextView[] listOfRatings;
    int ratings[] = {R.id.tvRatingMovie1, R.id.tvRatingMovie2, R.id.tvRatingMovie3, R.id.tvRatingMovie4, R.id.tvRatingMovie5, R.id.tvRatingMovie6};

    String ImdbIds[] = {"tt6443346","tt13433812", "tt21195490", "tt13443470", "tt9114286", "tt0468569"};

    MovieApiClient movieClient;
    String imdbID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    public void initialize() {
        listOfImageViews = new ImageView[posters.length];
        for (int i=0 ; i < posters.length ; i++) {
            listOfImageViews[i] = findViewById(posters[i]);
        }

        listOfTitles = new TextView[titles.length];
        for (int i=0 ; i < titles.length ; i++) {
            listOfTitles[i] = findViewById(titles[i]);
        }

        listOfRatings = new TextView[ratings.length];
        for (int i=0 ; i < ratings.length ; i++) {
            listOfRatings[i] = findViewById(ratings[i]);
        }

        loadFeedPage();

        // Set OnClickListener
        for (int i=0 ; i < posters.length ; i++) {
            listOfImageViews[i].setOnClickListener(this);
        }
    }

    private void loadFeedPage() {
        movieClient = new MovieApiClient(this);

        // Fetch information using the ImdbId
        for (int i=0 ; i < ImdbIds.length ; i++) {
            ImageView ivMovie = listOfImageViews[i];
            TextView tvTitleMovie = listOfTitles[i];
            TextView tvRating = listOfRatings[i];

            movieClient.findMovie(ImdbIds[i], new MovieApiClient.MovieApiClientCallback<Movie>() {
                @Override
                public void success(Movie data) {
                    imdbID = data.getImdbID();
                    Picasso.with(MainActivity.this).load(data.getPoster()).into(ivMovie);
                    tvTitleMovie.setText(data.getTitle() + " (" + data.getYear() + ")");
                    tvRating.setText(data.getYear());
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivMovie1:
                callMovieActivity(ImdbIds[0]);
                break;
            case R.id.ivMovie2:
                callMovieActivity(ImdbIds[1]);
                break;
            case R.id.ivMovie3:
                callMovieActivity(ImdbIds[2]);
                break;
            case R.id.ivMovie4:
                callMovieActivity(ImdbIds[3]);
                break;
            case R.id.ivMovie5:
                callMovieActivity(ImdbIds[4]);
                break;
            case R.id.ivMovie6:
                callMovieActivity(ImdbIds[5]);
                break;
        }
    }

    private void callMovieActivity(String imdbID) {
        Intent i = new Intent(this, MovieActivity.class);
        i.putExtra(MovieActivity.IMDB_ID, imdbID);
        startActivity(i);
    }
}