package voting_system.controller;

import org.springframework.hateoas.RepresentationModel;

/**
 * A class to represent a successful response to an admin closing the voting.
 */
public class CloseVotingResponse extends RepresentationModel<TallyResponse> {

    public final String message = "Voting is now closed.";
}
