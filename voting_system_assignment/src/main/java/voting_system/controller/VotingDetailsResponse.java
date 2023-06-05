package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * A class to hold the details of the election that can be revealed to an individual member.
 */
public class VotingDetailsResponse  extends RepresentationModel<LoginResponse> {

    @JsonProperty("candidates")
    private Candidate[] candidates; // An array of Candidate objects, which is the list of candidates that can be voted for.

    @JsonProperty("current_ballot")
    private Ballot ballot;  // A Ballot object which is the current ballot cast by this member.

    /**
     * A Constructor.
     *
     * @param candidates    A Candidate[] array, which is the list of candidates that can be voted for.
     * @param ballot    // A Ballot object which is the current ballot cast by this member.
     */
    public VotingDetailsResponse(Candidate[] candidates, Ballot ballot) {
        this.candidates = candidates;
        this.ballot = ballot;
    }
}