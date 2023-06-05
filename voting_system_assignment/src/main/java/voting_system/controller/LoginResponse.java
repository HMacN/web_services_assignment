package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * A class to represent a successful response to a login attempt.
 */
public class LoginResponse extends RepresentationModel<LoginResponse> {

    @JsonProperty("token")
    private String authorisationToken = ""; // The authorisation token being issued.

    /**
     * A constructor.
     *
     * @param authorisationToken    A String which is the authorisation token being issued.
     */
    public void setAuthorisationToken(String authorisationToken) {
        this.authorisationToken = authorisationToken;
    }
}
