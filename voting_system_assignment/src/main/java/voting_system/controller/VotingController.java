// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.

package voting_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import voting_system.service.VotingService;

import java.util.Map;

import static voting_system.VotingSystemApplication.*;

/**
 * A Class which handles the incoming HTTP requests, and matches them up with the relevant functions.
 */
@RestController
public class VotingController
{
    private final VotingService votingService;  // The VotingController depends on the VotingService, so it needs to keep a reference to it.

    private final ObjectMapper objectMapper;    // Used to parse objects into JSON Strings.

    // Make these "public static final", as the Spring @GetMapping annotation doesn't like non-final local variables or
    // collections.  This setup allows these paths to be used elsewhere in the program without the hassle of bundling
    // them up and passing them around.
    public static final String ORIGIN_PATH = "https://localhost:8443";
    public static final String MEMBER_LOGIN_SUB_PATH = "/member";
    public static final String MEMBER_LOGOUT_SUB_PATH = "/member/logout";
    public static final String MEMBER_VOTING_SUB_PATH = "/member/vote";
    public static final String MEMBER_WITHDRAW_VOTE_SUB_PATH = "/member/vote/withdraw";
    public static final String ADMIN_LOGIN_SUB_PATH = "/admin";
    public static final String ADMIN_LOGOUT_SUB_PATH = "/admin/logout";
    public static final String ADMIN_TALLY_SUB_PATH = "/admin/tally";
    public static final String ADMIN_OPEN_VOTING_SUB_PATH = "/admin/open_voting";
    public static final String ADMIN_CLOSE_VOTING_SUB_PATH = "/admin/close_voting";


    /**
     * The constructor.
     *
     * @param votingService A VotingService object to perform the business logic associated with the incoming requests.
     */
    public VotingController(VotingService votingService) {
        this.votingService = votingService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * It is hoped that the mappings for the incoming HTTP requests are both simple, and verbose, enough to be
     * effectively self-commenting.  Line comments will be used where appropriate to explain minor details.
     */

    @PostMapping(MEMBER_LOGIN_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse attemptMemberLogin(@RequestBody LoginAttempt loginAttempt, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, loginAttempt, ORIGIN_PATH + MEMBER_LOGIN_SUB_PATH, "POST");

        LoginResponse response = new LoginResponse();
        response.setAuthorisationToken(this.votingService.attemptMemberLogin(loginAttempt));
        response.add(Link.of(ORIGIN_PATH + MEMBER_LOGIN_SUB_PATH, IanaLinkRelations.SELF).withName("self"));
        response.add(Link.of(ORIGIN_PATH + MEMBER_LOGOUT_SUB_PATH, IanaLinkRelations.RELATED).withName("logout"));  // Pass other links for control buttons in the client.
        response.add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH, IanaLinkRelations.RELATED).withName("vote"));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @PutMapping(MEMBER_LOGOUT_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public LogoutResponse logoutMember(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers, @RequestBody Object body) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, body, ORIGIN_PATH + MEMBER_LOGOUT_SUB_PATH, "PUT");

        LogoutResponse response = new LogoutResponse();
        response.setMessage(this.votingService.logoutMember(token));
        response.add(Link.of(ORIGIN_PATH + MEMBER_LOGOUT_SUB_PATH, IanaLinkRelations.SELF));

        logOutgoing(response, HttpStatus.NO_CONTENT);
        return response;
    }

    @GetMapping(MEMBER_VOTING_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public VotingDetailsResponse getVotingDetails(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, "None", ORIGIN_PATH + MEMBER_VOTING_SUB_PATH, "GET");

        // Get needed components.
        Candidate[] candidates = this.votingService.getCandidates(token);
        voting_system.controller.Ballot ballot = this.votingService.getBallot(token);

        // Create response.
        VotingDetailsResponse response = new VotingDetailsResponse(candidates, ballot);
        response.add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH, IanaLinkRelations.SELF).withName("self"));

        // Get candidate links and add them to both the response and the individual candidates for easy client parsing.
        for (int i = 0; i < candidates.length; i++) {
            candidates[i].add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH + "/" + candidates[i].COMMON_NAME.toLowerCase(), IanaLinkRelations.SELF).withName("candidate_" + i));
            response.add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH + "/" + candidates[i].COMMON_NAME.toLowerCase(), IanaLinkRelations.RELATED).withName("candidate_" + i));
        }

        // Get ballot link and add it to both the response and the ballot for easy client parsing.
        ballot.add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH + MEMBER_WITHDRAW_VOTE_SUB_PATH, IanaLinkRelations.SELF).withName("withdraw"));
        response.add(Link.of(ORIGIN_PATH + MEMBER_WITHDRAW_VOTE_SUB_PATH, IanaLinkRelations.RELATED).withName("withdraw"));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @PutMapping(MEMBER_VOTING_SUB_PATH + "/{candidate}")
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CastVoteResponse castVoteResponse(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers, @PathVariable String candidate) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, "None", ORIGIN_PATH + MEMBER_VOTING_SUB_PATH + "/" + candidate, "PUT");

        this.votingService.castVote(token, candidate);
        CastVoteResponse response = new CastVoteResponse();
        response.add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH, IanaLinkRelations.SELF).withName("self"));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @DeleteMapping(MEMBER_WITHDRAW_VOTE_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public WithdrawVoteResponse withdrawVoteResponse (@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, "None", ORIGIN_PATH + MEMBER_WITHDRAW_VOTE_SUB_PATH, "DELETE");

        this.votingService.withdrawVote(token);
        WithdrawVoteResponse response = new WithdrawVoteResponse();
        response.add(Link.of(ORIGIN_PATH + MEMBER_VOTING_SUB_PATH, IanaLinkRelations.SELF).withName("self"));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @PutMapping(ADMIN_LOGIN_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginResponse(@RequestBody AdminLoginAttempt loginAttempt, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, loginAttempt, ORIGIN_PATH + ADMIN_LOGIN_SUB_PATH, "PUT");

        LoginResponse response = new LoginResponse();
        response.setAuthorisationToken(this.votingService.attemptAdminLogin(loginAttempt));
        response.add(Link.of(ORIGIN_PATH + ADMIN_LOGIN_SUB_PATH, IanaLinkRelations.SELF).withName("self"));
        // Add links for functions called by buttons in the client.
        response.add(Link.of(ORIGIN_PATH + ADMIN_LOGOUT_SUB_PATH, IanaLinkRelations.RELATED).withName("logout"));
        response.add(Link.of(ORIGIN_PATH + ADMIN_TALLY_SUB_PATH, IanaLinkRelations.RELATED).withName("tally"));
        response.add(Link.of(ORIGIN_PATH + ADMIN_OPEN_VOTING_SUB_PATH, IanaLinkRelations.RELATED).withName("open_voting"));
        response.add(Link.of(ORIGIN_PATH + ADMIN_CLOSE_VOTING_SUB_PATH, IanaLinkRelations.RELATED).withName("close_voting"));


        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @PutMapping(ADMIN_LOGOUT_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public LogoutResponse logoutResponse(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers, @RequestBody Object body) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, body, ORIGIN_PATH + ADMIN_LOGOUT_SUB_PATH, "PUT");

        LogoutResponse response = new LogoutResponse();
        response.setMessage(this.votingService.logoutAdmin(token));
        response.add(Link.of(ORIGIN_PATH + ADMIN_LOGOUT_SUB_PATH, IanaLinkRelations.SELF));

        logOutgoing(response, HttpStatus.NO_CONTENT);
        return response;
    }

    @GetMapping(ADMIN_TALLY_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public TallyResponse tallyResponse(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, "None", ORIGIN_PATH + ADMIN_TALLY_SUB_PATH, "GET");

        TallyResponse response = new TallyResponse(this.votingService.tallyVotes(token));
        response.add(Link.of(ORIGIN_PATH + ADMIN_LOGOUT_SUB_PATH, IanaLinkRelations.SELF));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @PutMapping(ADMIN_OPEN_VOTING_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public OpenVotingResponse openVotingResponse(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, "None", ORIGIN_PATH + ADMIN_OPEN_VOTING_SUB_PATH, "PUT");

        this.votingService.openVoting(token);
        OpenVotingResponse response = new OpenVotingResponse();
        response.add(Link.of(ORIGIN_PATH + ADMIN_OPEN_VOTING_SUB_PATH, IanaLinkRelations.SELF));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }

    @PutMapping(ADMIN_CLOSE_VOTING_SUB_PATH)
    @CrossOrigin(origins = ORIGIN_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CloseVotingResponse closeVotingResponse(@RequestHeader("authorisation") String token, @RequestHeader Map<String, String> headers) throws ToBeConvertedToHTTPResponseException {
        logIncoming(headers, "None", ORIGIN_PATH + ADMIN_CLOSE_VOTING_SUB_PATH, "PUT");

        this.votingService.closeVoting(token);
        CloseVotingResponse response = new CloseVotingResponse();
        response.add(Link.of(ORIGIN_PATH + ADMIN_CLOSE_VOTING_SUB_PATH, IanaLinkRelations.SELF));

        logOutgoing(response, HttpStatus.OK);
        return response;
    }


    /**
     * This class tells Spring how to handle the ToBeConvertedToHTTPResponseException objects that get thrown by the service.
     */
    @ControllerAdvice
    public class clientExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(value = ToBeConvertedToHTTPResponseException.class)
        public ResponseEntity<String> handleException(ToBeConvertedToHTTPResponseException exception) {
            // Generate a ResponseEntity which describes the problem to the client, and which has the correct status code.
            ResponseEntity<String> response = new ResponseEntity<>(convertObjectToJson(exception.MESSAGE), exception.STATUS);
            logOutgoing(response, HttpStatus.valueOf(response.getStatusCodeValue()));
            return response;
        }
    }

    /**
     * A function for logging the (successful) incoming HTTP requests.
     *
     * @param headers   An Object which is the headers associated with the HTTP request.
     * @param body      An Object which is the body associated with the HTTP request.
     * @param url       A String which is the URL associated with the HTTP request.
     * @param reqCode   A String which is the HTTP request code.
     */
    private void logIncoming(Object headers, Object body, String url, String reqCode) {
        cOut("");
        cOut(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> HTTP REQUEST >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        cOut("URL PATH: " + url);
        cOut("REQUEST CODE: " + reqCode);
        cOut("REQUEST HEADERS: \n" + tidyJson(convertObjectToJson(headers)));
        cOut("REQUEST BODY: \n" + tidyJson(convertObjectToJson(body)));
        cOut(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    /**
     * A function for the logging of outgoing HTTP server responses.
     *
     * @param response  An Object which is the outgoing server response.
     * @param httpStatus    An HttpStatusCode which is the response code associated with this response.
     */
    private void logOutgoing(Object response, HttpStatus httpStatus) {

        cOut("");
        cOut("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< SERVER RESPONSE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        cOut("HTTP STATUS: " + httpStatus.value() + ", " + httpStatus.getReasonPhrase());
        cOut("OUTGOING OBJECT: \n" + tidyJson(convertObjectToJson(response)));
        cOut("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    /**
     * A function to convert Objects to JSON-formatted strings.
     *
     * @param obj   An Object to be converted to a JSON string.
     * @return  A String which is the given object in JSON format.
     */
    private String convertObjectToJson(Object obj) {
        String str = "";

        try {
            str = objectMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            cLog("COULD NOT PARSE OBJECT INTO JSON");
            cLog("PARSE EXCEPTION: " + e);
            e.printStackTrace();
        }

        return str;
    }
}