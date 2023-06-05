package voting_system.controller;

import org.springframework.hateoas.RepresentationModel;

/**
 * A class which represents a successful response to opening the voting.
 */
public class OpenVotingResponse extends RepresentationModel<TallyResponse> {

    public final String message = "Voting is now allowed."; // A message to be returned to the client.
}