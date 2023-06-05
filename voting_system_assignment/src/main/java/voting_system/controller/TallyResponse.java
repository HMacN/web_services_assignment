package voting_system.controller;

import org.springframework.hateoas.RepresentationModel;

/**
 * A class to represent a response to an admin requesting a tally of the votes cast so far.
 */
public class TallyResponse extends RepresentationModel<TallyResponse> {

    public final String tally_data; // A String which is the tally data in JSON format.


    /**
     * A Constructor.
     *
     * @param tally_data    A String which is the tally data in JSON format.
     */
    public TallyResponse(String tally_data) {
        this.tally_data = tally_data;
    }
}
