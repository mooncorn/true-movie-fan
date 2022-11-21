package Services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.truemoviefan.BuildConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Models.Movie;
import Models.MovieApiErrorResponse;

public class MovieApiClient {

    public static abstract class MovieApiClientCallback<T> {
        public abstract void success(T data);
        public abstract void error(String message);
    }

    RequestQueue queue;
    String baseUrl = "https://movie-database-alternative.p.rapidapi.com/";

    public MovieApiClient(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void findMovie(String id, MovieApiClientCallback<Movie> callback) {
        String url = baseUrl + "?r=json&i=" + id;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    // Check if response is successful
                    MovieApiErrorResponse res = MovieApiErrorResponse.deserialize(response.toString());
                    if (!res.wasSuccessful()) {
                        callback.error(res.getError());
                        return;
                    }

                    try {
                        callback.success(Movie.deserialize(response.toString()));
                    } catch (Exception e) {
                        callback.error("Could not map: " + response);
                    }
                },
                error -> callback.error(error.getMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                return MovieApiClient.getHeaders();
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void search(String term, MovieApiClientCallback<ArrayList<Movie>> callback) {
        try {
            String encodedUrl = URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
            String url = baseUrl + "?s=" + encodedUrl + "&r=json&page=1";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    response -> {
                        // Check if response is successful
                        MovieApiErrorResponse res = MovieApiErrorResponse.deserialize(response.toString());
                        if (!res.wasSuccessful()) {
                            callback.error(res.getError());
                            return;
                        }

                        try {
                            callback.success(Movie.deserializeMany(response.toString()));
                        } catch (Exception e) {
                            callback.error("Could not map: " + response);
                        }
                    },
                    error -> callback.error(error.getMessage())) {
                @Override
                public Map<String, String> getHeaders() {
                    return MovieApiClient.getHeaders();
                }
            };

            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Map<String, String> getHeaders() {
        Map<String, String>  params = new HashMap<>();
        params.put("X-RapidAPI-Key", BuildConfig.MOVIE_API_KEY);
        params.put("X-RapidAPI-Host", "movie-database-alternative.p.rapidapi.com");

        return params;
    }
}
