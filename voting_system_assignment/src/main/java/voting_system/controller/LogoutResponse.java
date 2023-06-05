package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * A class which represents a successful logout attempt.
 */
public class LogoutResponse extends RepresentationModel<LogoutResponse> {

    @JsonProperty("message")
    private String message = "";    // A message to be returned to the client.

    /**
     * A constructor.
     *
     * @param message       // A String which is a message to be returned to the client.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
