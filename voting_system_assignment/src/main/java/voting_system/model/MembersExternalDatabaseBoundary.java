package voting_system.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import voting_system.controller.LoginAttempt;

/**
 * A boundary class to handle all interactions with the external database of member details.  This exists to help
 * compartmentalise the code.
 */
public class MembersExternalDatabaseBoundary
{
    private final LoginAttempt LOGIN_ATTEMPT_DETAILS;   // The user details to check in the database.
    private final ResponseEntity<String> EXTERNAL_DATABASE_RESPONSE;    // The response from the database.
    private final String EXTERNAL_DATABASE_URL = "https://pmaier.eu.pythonanywhere.com/sawb/member/";   // The URL that the database can be found at.

    private final boolean ALL_LOGIN_DETAILS_GIVEN;  // A bool to record if all the needed login details were given.
    private final boolean SUCCESSFUL_RESPONSE_FROM_DATABASE;    // A bool to record if the database responded successfully.
    private final boolean ALL_LOGIN_DETAILS_MATCH_DATABASE_VALUES;  // A bool to record if the database values matched the login attempt values.

    /**
     * The constructor.  This class is set up to only handle a single login attempt, future login attempts will require
     * new instances of the class.  If the web service was scaled up then this class may need to be modified to be reusable between multiple member login attempts.
     *
     * @param loginAttemptDetails   A LoginAttempt object which contains the details provided by the user attempting to log in.
     */
    public MembersExternalDatabaseBoundary(LoginAttempt loginAttemptDetails) {
        this.LOGIN_ATTEMPT_DETAILS = loginAttemptDetails;
        this.EXTERNAL_DATABASE_RESPONSE = getExternalDatabaseResponse();
        this.ALL_LOGIN_DETAILS_GIVEN = checkAllLoginDetailsGiven();

        // Don't check with database if not all details given.
        if (this.ALL_LOGIN_DETAILS_GIVEN) {
            this.SUCCESSFUL_RESPONSE_FROM_DATABASE = successfulResponseFromExternalDatabase();
        } else {
            this.SUCCESSFUL_RESPONSE_FROM_DATABASE = false;
        }

        // Don't check details match is database didn't respond.
        if (this.SUCCESSFUL_RESPONSE_FROM_DATABASE) {
            this.ALL_LOGIN_DETAILS_MATCH_DATABASE_VALUES = checkAllLoginDetailsMatch();
        } else {
            this.ALL_LOGIN_DETAILS_MATCH_DATABASE_VALUES = false;
        }
    }

    /**
     * A function to check if all the user details needed to log in have been provided.
     *
     * @return  A boolean which indicates if all the data needed has been provided.
     */
    private boolean checkAllLoginDetailsGiven()
    {
        return !this.LOGIN_ATTEMPT_DETAILS.user_number.equals("") &&
                !this.LOGIN_ATTEMPT_DETAILS.user_name.equals("");
    }

    /**
     * A function which checks if the database responded with HTTP response code 200 (OK).  If not then this returns
     * false.
     *
     * @return  A boolean which indicates whether or not the database responded correctly to the request.
     */
    private boolean successfulResponseFromExternalDatabase() {
        return this.EXTERNAL_DATABASE_RESPONSE.getStatusCode().equals(HttpStatus.OK);
    }

    /**
     * The function which actually queries the database for member login details.  Responds with an illegal JSON value
     * for body, and HttpStatus.NOT_FOUND, if the database does not respond properly.
     *
     * @return  A ResponseEntity object which contains the response from the database.
     */
    private ResponseEntity<String> getExternalDatabaseResponse()
    {
        String membersDatabaseURL = this.EXTERNAL_DATABASE_URL + this.LOGIN_ATTEMPT_DETAILS.user_number;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.getForEntity(membersDatabaseURL, String.class);
        } catch (Exception exception) {
            // If the database doesn't respond properly, then create a "fault code" return value.
            // The "body" value should cause an exception if anything tries to read it as a JSON value.  This will help
            // detect the fault code value being used by accident.
            response = new ResponseEntity<>("XXX", HttpStatus.NOT_FOUND);
        }

        return response;
    }

    /**
     * Checks all the relevant user details provided match the ones received from the database.
     *
     * @return  A boolean which indicates whether or not the provided details match the ones in the database.
     */
    private boolean checkAllLoginDetailsMatch()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String nameRecord;
        String numberRecord;
        String ageRecord;
        String regionRecord;

        boolean namesMatch = false;
        boolean numbersMatch = false;
        boolean agesMatch = false;
        boolean regionsMatch = false;

        try
        {
            JsonNode node = objectMapper.readTree(this.EXTERNAL_DATABASE_RESPONSE.getBody());

            // Required fields.  These are always present in the database.
            nameRecord = node.get("member").get("name").asText("");
            namesMatch = nameRecord.equalsIgnoreCase(this.LOGIN_ATTEMPT_DETAILS.user_name);

            numberRecord = node.get("member").get("number").asText("");
            numbersMatch = numberRecord.equals(this.LOGIN_ATTEMPT_DETAILS.user_number);

            // Optional field.
            if (node.get("member").has("age")) {
                ageRecord = node.get("member").get("age").asText("");
                agesMatch = ageRecord.equals(this.LOGIN_ATTEMPT_DETAILS.user_age);
            } else {
                agesMatch = true;
            }

            // Optional field.
            if (node.get("member").has("region")) {
                regionRecord = node.get("member").get("region").asText("");
                regionsMatch = regionRecord.equalsIgnoreCase(this.LOGIN_ATTEMPT_DETAILS.user_region);
            } else {
                regionsMatch = true;
            }
        }
        catch (JsonProcessingException e)
        {
            // If the values can't be read then return false and catch the exception.
            System.out.println("JSON PROCESSING EXCEPTION: " + e);
            e.printStackTrace();
            return false;
        }

        // Return true if all values match.
        return namesMatch && numbersMatch && agesMatch && regionsMatch;
    }

    /**
     * Getter for whether or not all the relevant login details were provided.
     *
     * @return A boolean which indicates whether or not all the relevant login details were provided.
     */
    public boolean allLoginDetailsGiven()
    {
        return ALL_LOGIN_DETAILS_GIVEN;
    }

    /**
     * Getter for whether or not the database could be successfully queried about the login attempt.
     *
     * @return  A boolean which indicates whether or not the database could be successfully queried about the login attempt.
     */
    public boolean successfulResponseFromDatabase()
    {
        return SUCCESSFUL_RESPONSE_FROM_DATABASE;
    }

    /**
     * Getter for whether or not the database values match the ones provided as part of the login attempt.
     *
     * @return  A boolean which indicates whether or not the database values match the ones provided as part of the login attempt.
     */
    public boolean loginDetailsMatchDatabaseValues()
    {
        return ALL_LOGIN_DETAILS_MATCH_DATABASE_VALUES;
    }
}

