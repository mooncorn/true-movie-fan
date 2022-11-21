package Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private String imdbID;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Year")
    private int year;
    @JsonProperty("Plot")
    private String plot;
    @JsonProperty("Poster")
    private String poster;

    public Movie() {

    }

    public Movie(String imdbID, String title, int year, String plot, String poster) {
        this.imdbID = imdbID;
        this.title = title;
        this.year = year;
        this.plot = plot;
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "imdbID='" + imdbID + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", plot='" + plot + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }

    public static Movie deserialize(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, Movie.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Movie> deserializeMany(String json) {
        int start = json.indexOf("[");
        int end = json.indexOf("]");
        String jsonArray = json.substring(start, end + 1);

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(jsonArray, new TypeReference<ArrayList<Movie>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
