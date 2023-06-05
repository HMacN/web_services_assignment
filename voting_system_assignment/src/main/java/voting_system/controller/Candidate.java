package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * A class to record details about individual candidates.
 */
public class Candidate extends RepresentationModel<Candidate>
{
    @JsonProperty("common_name")
    public final String COMMON_NAME;    // The common (English) name of the candidate.

    @JsonProperty("scientific_name")
    public final String SCIENTIFIC_NAME;    // The scientific (Latin) name of the candidate.

    @JsonProperty("description")
    public final String DESCRIPTION;    // A brief description of the candidate.

    /**
     * A constructor.
     *
     * @param commonName    A String which is the common (English) name of the candidate.
     * @param scientificName    A String which is the scientific (Latin) name of the candidate.
     * @param description   A String which is a brief description of the candidate.
     */
    public Candidate(String commonName, String scientificName, String description) {
        this.COMMON_NAME = commonName;
        this.SCIENTIFIC_NAME = scientificName;
        this.DESCRIPTION = description;
    }

    /**
     * A copy Constructor.
     *
     * @param candidate A Candidate object to be copied.
     */
    public Candidate(Candidate candidate) {
        this.COMMON_NAME = candidate.COMMON_NAME;
        this.SCIENTIFIC_NAME = candidate.SCIENTIFIC_NAME;
        this.DESCRIPTION = candidate.DESCRIPTION;
    }
}
