package Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieApiErrorResponse {
    @JsonProperty("Response")
    private boolean response;
    @JsonProperty("Error")
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean wasSuccessful() { return response; }

    public static MovieApiErrorResponse deserialize(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, MovieApiErrorResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
