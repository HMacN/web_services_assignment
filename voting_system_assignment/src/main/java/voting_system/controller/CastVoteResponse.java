package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import voting_system.controller.LoginResponse;

/**
 * A container object to represent a successful response to a member casting a vote.
 */
public class CastVoteResponse extends RepresentationModel<LoginResponse> {

    @JsonProperty("message")
    private String message = "Vote cast successfully!";
}
