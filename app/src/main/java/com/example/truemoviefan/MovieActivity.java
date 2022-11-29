package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.Movie;
import Model.Review;
import Api.MovieApiClient;

/**
 * Displays information of a movie. IMDB ID is received from intent.
 */
public class MovieActivity extends AppCompatActivity {
    public static final String IMDB_ID = "IMDB_ID";

    TextView txtTitle, txtPlot;
    ImageView ivPoster;
    RecyclerView rvReviews;
    Button btnPostReview, btnAddToWatchList;

    MovieApiClient movieClient;
    String imdbId;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        initialize();
    }

    private void initialize() {
        db = FirebaseFirestore.getInstance();

        txtTitle = findViewById(R.id.txtTitle);
        txtPlot = findViewById(R.id.txtPlot);
        ivPoster = findViewById(R.id.ivPoster);
        rvReviews = findViewById(R.id.rvReviews);
        btnPostReview = findViewById(R.id.btnPostReview);
        btnAddToWatchList = findViewById(R.id.btnAddToWatchList);
        btnPostReview.setOnClickListener(view -> goToPostReviewActivity());
        btnAddToWatchList.setOnClickListener(view -> addMovieToWatchList());

        movieClient = new MovieApiClient(this);

        // Receive id of title
        imdbId = getIntent().getStringExtra(IMDB_ID);

        // Fetch information about the title
        movieClient.findMovie(imdbId, new MovieApiClient.MovieApiClientCallback<Movie>() {
            @Override
            public void success(Movie data) {
                displayMovie(data);
            }

            @Override
            public void error(String message) {
                if (message != null) {
                    Toast.makeText(MovieActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                Intent i = new Intent(MovieActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Fetch reviews for that title
        db.collection("review")
                .whereEqualTo("imdbId", imdbId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Review> reviews = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String content = document.get("content").toString();
                            String avatar = document.get("avatar").toString();
                            String username = document.get("username").toString();
                            Review review = new Review(content, username, avatar);
                            reviews.add(review);
                        }

                        ReviewsAdapter adapter = new ReviewsAdapter(reviews);
                        rvReviews.setAdapter(adapter);
                        rvReviews.setLayoutManager(new LinearLayoutManager(MovieActivity.this));

                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                });
    }

    private void addMovieToWatchList() {
        // TODO: implement this
    }

    private void goToPostReviewActivity() {
        Intent i = new Intent(this, CreateReviewActivity.class);
        i.putExtra(MovieActivity.IMDB_ID, imdbId);
        startActivity(i);
    }

    private void displayMovie(Movie movie) {
        Picasso.with(this).load(movie.getPoster()).into(ivPoster);
        txtTitle.setText(movie.getTitle() + " (" + movie.getYear() + ")");
        txtPlot.setText(movie.getPlot());
    }
}