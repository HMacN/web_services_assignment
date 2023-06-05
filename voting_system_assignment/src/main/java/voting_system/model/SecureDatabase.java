package voting_system.model;

import org.springframework.http.HttpStatus;
import voting_system.controller.Ballot;
import voting_system.controller.Candidate;
import voting_system.controller.ToBeConvertedToHTTPResponseException;

import java.util.HashMap;

/**
 * A class which holds sensitive data that should go into a persistent, secure, database.  This is currently not
 * implemented, so an insecure, in-memory, solution has been used as a temporary replacement.
 */
public class SecureDatabase {
    private HashMap<String, Ballot> dataBase = new HashMap<>(); // The record of all ballots cast.
    private final String ADMIN_PASSWORD = "password123";    // A record of the admin password.

    private boolean votingOpen = false; // Whether or not the voting is open.

    // The records of the three candidates in the election.
    private final Candidate CANDIDATE_0 = new Candidate("Treecreeper", "Certhia Familiaris", "It is similar to other treecreepers, and has a curved bill, patterned brown upperparts, whitish underparts, and long stiff tail feathers which help it creep up tree trunks.");
    private final Candidate CANDIDATE_1 = new Candidate("Curlew",  "Numenius Arquata", "It is similar to other treecreepers, and has a curved bill, patterned brown upperparts, whitish underparts, and long stiff tail feathers which help it creep up tree trunks.");
    private final Candidate CANDIDATE_2 = new Candidate("Fulmar",  "Fulmarus Glacialis", "Fulmars come in one of two color morphs: a light one, with white head and body and gray wings and tail, and a dark one, which is uniformly gray.");

    /**
     * A getter for the current vote cast by a member.  Returns an empty ballot if the member has not voted.  Ballot
     * objects are copied here ot protect the integrity of the database.
     *
     * @param memberNumber  A String which is the number of the member to get the ballot for.
     * @return  A Ballot object which is the vote currently cast by the member.
     */
    public Ballot getCurrentBallot(String memberNumber) {
        Ballot emptyBallot = new Ballot(memberNumber, "");
        Ballot retrievedBallot = dataBase.getOrDefault(memberNumber, emptyBallot);
        return new Ballot(retrievedBallot); // Return copy to protect database instances.
    }

    /**
     * A function to get the details of all three candidates.  Candidate objects are copied here to protect the
     * integrity of the database.
     *
     * @return  A Candidate[] array which contains copies of all candidates in the election.
     */
    public Candidate[] getCandidateDetails() {
        Candidate[] candidates = new Candidate[3];
        // return copies of the objects to protect the database instances.
        candidates[0] = new Candidate(this.CANDIDATE_0);
        candidates[1] = new Candidate(this.CANDIDATE_1);
        candidates[2] = new Candidate(this.CANDIDATE_2);
        return candidates;
    }

    /**
     * A function to allow a member to cast a vote.  Removes any pre-existing vote to avoid duplication.
     *
     * @param memberNumber  A String which is the membership number of the voter.
     * @param candidateName A String which is the (case-insensitive) name of the candidate to be voted for.
     *
     * @throws ToBeConvertedToHTTPResponseException  An exception to be converted into an HTTP response message.
     */
    public void castVote(String memberNumber, String candidateName) throws ToBeConvertedToHTTPResponseException {
        checkIfVotingStillOpen();

        if (!candidateName.equalsIgnoreCase(this.CANDIDATE_0.COMMON_NAME) &&
                !candidateName.equalsIgnoreCase(this.CANDIDATE_1.COMMON_NAME) &&
                !candidateName.equalsIgnoreCase(this.CANDIDATE_2.COMMON_NAME)) {
            throw new ToBeConvertedToHTTPResponseException("Selected candidate does not exist!", HttpStatus.NOT_FOUND);
        }

        this.withdrawVote(memberNumber);  // Get rid of any old ballot.

        dataBase.put(memberNumber, new Ballot(memberNumber, candidateName));    // Cast new candidate.
    }

    /**
     * A function to allow members to withdraw their votes.
     *
     * @param memberNumber A String which is the membership number of the voter.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    public void withdrawVote(String memberNumber) throws ToBeConvertedToHTTPResponseException {
        checkIfVotingStillOpen();

        dataBase.remove(memberNumber);
    }

    /**
     * A function to check that a given admin password matches the currently known admin password.
     *
     * @param adminPass A String which is the password provided by the admin.
     * @return  A boolean which indicates if the provided password was valid.
     */
    public boolean adminLoginDetailsMatchDatabaseValues(String adminPass) {
        return adminPass.equals(this.ADMIN_PASSWORD);
    }

    /**
     * A function which allows the votes cast so far to be tallied.]
     *
     * @return  A String which contains a record of the votes cast for each candidate in JSON format.
     */
    public String tallyVotes() {
        int votesFor0 = 0;
        int votesFor1 = 0;
        int votesFor2 = 0;

        // Check through the whole database.
        for (Ballot ballot : this.dataBase.values()) {
            String onBallot = ballot.getCandidate();

            if (onBallot.equalsIgnoreCase(CANDIDATE_0.COMMON_NAME)) {
                votesFor0++;
            }
            else if (onBallot.equalsIgnoreCase(CANDIDATE_1.COMMON_NAME)) {
                votesFor1++;
            }
            else if (onBallot.equalsIgnoreCase(CANDIDATE_2.COMMON_NAME)) {
                votesFor2++;
            }
        }

        // Convert the data to a JSON string.
        return "{\"" + CANDIDATE_0.COMMON_NAME + "\":\"" + votesFor0 + "\",\"" + CANDIDATE_1.COMMON_NAME + "\":\"" + votesFor1 + "\",\"" + CANDIDATE_2.COMMON_NAME + "\":\"" + votesFor2 + "\"}";
    }

    /**
     * Opens the election for voting.
     */
    public void setVotingOpen() {
        this.votingOpen = true;
    }

    /**
     * Closes the election for voting.
     */
    public void setVotingClosed() {
        this.votingOpen = false;
    }

    /**
     * Checks if the election is open for voting.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    private void checkIfVotingStillOpen() throws ToBeConvertedToHTTPResponseException {
        if (!this.votingOpen) {
            throw new ToBeConvertedToHTTPResponseException("Voting is currently not open!", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
        }
    }
}
