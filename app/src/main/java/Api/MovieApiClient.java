package Api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.truemoviefan.BuildConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Movie;
import Model.MovieApiErrorResponse;

public class MovieApiClient {

    public static abstract class MovieApiClientCallback<T> {
        public abstract void success(T data);
        public abstract void error(String message);
    }

    public static final String TAG = "MovieApiClient";
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
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        MovieApiErrorResponse errorResponse = mapper.readValue(response.toString(), MovieApiErrorResponse.class);

                        if (!errorResponse.wasSuccessful()) {
                            callback.error(errorResponse.getError());
                            return;
                        }

                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }


                    try {
                        Movie movie = mapper.readValue(response.toString(), Movie.class);

                        callback.success(movie);

                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        callback.error(e.getMessage());
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

    public void search(String term, MovieApiClientCallback<List<Movie>> callback) {
        try {
            String encodedUrl = URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
            String url = baseUrl + "?s=" + encodedUrl + "&r=json&page=1";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    response -> {
                        ObjectMapper mapper = new ObjectMapper();

                        String json = response.toString();
                        int start = json.indexOf("[");
                        int end = json.indexOf("]");
                        String jsonArray = json.substring(start, end + 1);

                        try {
                            MovieApiErrorResponse errorResponse = mapper.readValue(jsonArray, MovieApiErrorResponse.class);

                            if (!errorResponse.wasSuccessful()) {
                                callback.error(errorResponse.getError());
                                return;
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }


                        try {
                            List<Movie> movieList = mapper.readValue(jsonArray, new TypeReference<List<Movie>>() {});

                            callback.success(movieList);

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                            callback.error(e.getMessage());
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
