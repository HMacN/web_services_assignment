package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * A class to record individual ballots.  This functions like an individual paper ballot.
 */
public class Ballot extends RepresentationModel<Ballot> {

    @JsonProperty("member_number")
    private String memberNumber = "";   // The membership number of the voter.

    @JsonProperty("candidate")
    private String candidate = "";  // The candidate voted for.

    /**
     * A constructor.
     *
     * @param memberNumber  A String which is the membership number of the person casting the ballot.
     * @param candidate A String which is the common name of the candidate being voted for.
     */
    public Ballot(String memberNumber, String candidate) {
        this.memberNumber = memberNumber;
        this.candidate = candidate;
    }

    /**
     * A Copy Constructor to allow easy copying.
     *
     * @param ballot    A Ballot object to make a copy of.
     */
    public Ballot(Ballot ballot) {
        this.memberNumber = ballot.memberNumber;
        this.candidate = ballot.candidate;
    }

    /**
     * A getter for the membership number of the voter.
     *
     * @return  A String which is the membership number of the voter.
     */
    public String getMemberNumber() {
        return memberNumber;
    }

    /**
     * A getter for the candidate being voted for.
     *
     * @return  A String which is the candidate being voted for.
     */
    public String getCandidate() {
        return candidate;
    }

    /**
     * An override of the equals() method.  Equality is now compared by the values of the two contained String values.
     *
     * @param other An Object to check equality with.
     * @return  A boolean indicating if this object is equal to the given object.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other.getClass() != this.getClass())
            return false;

        // Cast to Ballot.
        Ballot otherBallot = (Ballot) other;

        if (!this.candidate.equals(otherBallot.getCandidate()) || !this.memberNumber.equals(otherBallot.getMemberNumber()))
            return false;

        return true;
    }
}
