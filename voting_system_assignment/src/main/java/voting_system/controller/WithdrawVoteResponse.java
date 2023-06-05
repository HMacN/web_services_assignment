package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * An object to represent a successful response to a member withdrawing their vote.
 */
public class WithdrawVoteResponse extends RepresentationModel<LoginResponse> {

    @JsonProperty("message")
    private String message = "Vote withdrawn successfully!";    // A message to return to the member.
}