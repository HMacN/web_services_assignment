// Implementation of the business logic, living in the service sub-package.
// Discoverable for auto-configuration, thanks to the @Component annotation.

package voting_system.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import voting_system.controller.*;
import voting_system.model.*;

import static voting_system.VotingSystemApplication.cLog;

@Component
public class VotingServiceImpl implements voting_system.service.VotingService {
    private final SecurityTokenUnit securityTokenUnit;
    private final SecureDatabase secureDatabase;

    private final String NOT_ALL_LOGIN_DETAILS_PROVIDED = "Please enter values for all membership details.";
    private final String COULD_NOT_FIND_LOGIN_DETAILS = "Sorry, we could not find your details.  Please check that your membership number has been entered correctly.";
    private final String LOGIN_DETAILS_DO_NOT_MATCH_RECORDS = "Sorry, your details do not match the ones on record.  Please check that you have entered all of your details correctly.";
    private final String UNKNOWN_LOGIN_PROBLEM = "Sorry, there was an unexpected issue logging you in.  Please try again later.";
    private final String LOGIN_TOKEN_INVALID = "Your session has expired.  Please log in again if you wish to continue.";
    private final String USER_LOGGED_OUT = "You have been logged out.  Have a nice day.";
    private final String ADMIN_NOT_ALL_DETAILS = "Please provide your admin password.";

    public VotingServiceImpl() {
        this.securityTokenUnit = new SecurityTokenUnit();
        this.secureDatabase = new SecureDatabase();
    }

    /**
     * Revokes an issued security token.
     *
     * @param token A String which is a token to be revoked.
     * @return  A String which is to be returned to the client on token revocation.
     * @throws ToBeConvertedToHTTPResponseException
     */
    @Override
    public String logoutMember(String token) throws ToBeConvertedToHTTPResponseException {
        // Confirm that the token is valid, and return a suitable response if not.
        checkMemberAuthorisation(token);

        this.securityTokenUnit.revokeToken(token);
        return USER_LOGGED_OUT;
    }

    /**
     * Let authorised members get the details of all candidates in the election.
     *
     * @param token The security token of the member.
     * @return  A String which contains the details of the candidates in the election in JSON format.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public Candidate[] getCandidates(String token) throws ToBeConvertedToHTTPResponseException {
        // Confirm that the token is valid, and return a suitable response if not.
        checkMemberAuthorisation(token);

        return this.secureDatabase.getCandidateDetails();
    }

    /**
     * Logs a member in using the details provided, and issues a security token.
     *
     * @param loginAttempt  A LoginAttempt object which contains the details of the member trying to log in.
     * @return  A String which is the issued security token.
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public String attemptMemberLogin(LoginAttempt loginAttempt) throws ToBeConvertedToHTTPResponseException {
        MembersExternalDatabaseBoundary externalDatabase = new MembersExternalDatabaseBoundary(loginAttempt);

        if (!externalDatabase.allLoginDetailsGiven()) {
            throw new ToBeConvertedToHTTPResponseException(convertToJsonObj(NOT_ALL_LOGIN_DETAILS_PROVIDED), HttpStatus.BAD_REQUEST);
        }

        if (!externalDatabase.successfulResponseFromDatabase()) {
            throw new ToBeConvertedToHTTPResponseException(convertToJsonObj(COULD_NOT_FIND_LOGIN_DETAILS), HttpStatus.NOT_FOUND);
        }

        if (!externalDatabase.loginDetailsMatchDatabaseValues()) {
            throw new ToBeConvertedToHTTPResponseException(convertToJsonObj(LOGIN_DETAILS_DO_NOT_MATCH_RECORDS), HttpStatus.UNAUTHORIZED);
        }

        String securityToken = securityTokenUnit.issueMemberSecurityToken(loginAttempt);
        if (securityToken.isEmpty()) {
            throw new ToBeConvertedToHTTPResponseException(convertToJsonObj(UNKNOWN_LOGIN_PROBLEM), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return securityToken;
    }

    /**
     * Allows an authorised member to retrieve their own ballot.
     *
     * @param token A String which contains the members security token.
     * @return  A Ballot object which contains the members vote details.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public Ballot getBallot(String token) throws ToBeConvertedToHTTPResponseException {
        checkMemberAuthorisation(token);

        String memberNumber = this.securityTokenUnit.getMemberNumberForToken(token);
        return this.secureDatabase.getCurrentBallot(memberNumber);
    }

    /**
     * Allows an authorised member to cast their vote.
     *
     * @param token A String which is the members security token.
     * @param candidateName A String which is the name of the candidate being voted for.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public void castVote(String token, String candidateName) throws ToBeConvertedToHTTPResponseException {
        checkMemberAuthorisation(token);

        String memberNumber = this.securityTokenUnit.getMemberNumberForToken(token);
        this.secureDatabase.castVote(memberNumber, candidateName);
    }

    /**
     * Allows an authorised member to withdraw their vote.
     *
     * @param token A String which is the members security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public void withdrawVote(String token) throws ToBeConvertedToHTTPResponseException {
        checkMemberAuthorisation(token);

        String memberNumber = this.securityTokenUnit.getMemberNumberForToken(token);
        this.secureDatabase.withdrawVote(memberNumber);
    }

    /**
     * Allows an administrator to login and be issued a security token.
     *
     * @param loginAttempt  A LoginAttempt object which contains the details of the admin logging in.
     * @return  A String which is the security token for this admin.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public String attemptAdminLogin(AdminLoginAttempt loginAttempt) throws ToBeConvertedToHTTPResponseException {
        if (loginAttempt.getAdminPass().equals("") || loginAttempt.getAdminPass() == null) {
            throw new ToBeConvertedToHTTPResponseException(ADMIN_NOT_ALL_DETAILS, HttpStatus.BAD_REQUEST);
        }

        if (!secureDatabase.adminLoginDetailsMatchDatabaseValues(loginAttempt.getAdminPass())) {
            throw new ToBeConvertedToHTTPResponseException(convertToJsonObj(LOGIN_DETAILS_DO_NOT_MATCH_RECORDS), HttpStatus.UNAUTHORIZED);
        }

        String securityToken = securityTokenUnit.issueAdminSecurityToken(loginAttempt.getAdminPass());
        if (securityToken.isEmpty()) {
            throw new ToBeConvertedToHTTPResponseException(convertToJsonObj(UNKNOWN_LOGIN_PROBLEM), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return securityToken;
    }

    /**
     * Allows an admin to be logged out, and to revoke their security token.
     *
     * @param token A String which is the security token of the admin.
     * @return  A String which is a message to be delivered to the user on token revocation.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public String logoutAdmin(String token) throws ToBeConvertedToHTTPResponseException {
        // Confirm that the token is valid, and return a suitable response if not.
        checkAdminAuthorisation(token);

        this.securityTokenUnit.revokeToken(token);
        return USER_LOGGED_OUT;
    }

    /**
     * Allows admins to tally the votes currently cast in the election.
     *
     * @param token A String which is the security token of the admin.
     * @return  A String which contains the tally of the votes cast for different candidates in JSON format.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public String tallyVotes(String token) throws ToBeConvertedToHTTPResponseException {
        checkAdminAuthorisation(token);

        return this.secureDatabase.tallyVotes();
    }

    /**
     * Allows an admin to open voting in the election.
     *
     * @param token A String which is the admins security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public void openVoting(String token) throws ToBeConvertedToHTTPResponseException {
        checkAdminAuthorisation(token);

        this.secureDatabase.setVotingOpen();
    }

    /**
     * Allows an admin to close voting in the election.
     *
     * @param token  A String which is the admins security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    @Override
    public void closeVoting(String token) throws ToBeConvertedToHTTPResponseException {
        checkAdminAuthorisation(token);

        this.secureDatabase.setVotingClosed();
    }

    /**
     * Takes a normal string and formats it so that it can be parsed into a JSON object.  This object will have one
     * parameter, "message", whose value will be the given string.
     *
     * @param str   A String to be converted to JSON format.
     * @return  A String in JSON format.
     */
    private String convertToJsonObj(String str) {
        return this.convertToJsonObj("message", str);
    }

    /**
     * Takes a normal string and formats it so that it can be parsed into a JSON object.  This object will have one
     * parameter, whose name will be the one provided, and whose value will be the given string.
     *
     * @param tag   A String which will be JSON parameter name.
     * @param str   A String which will be the JSON parameter value.
     * @return  A String in JSON format.
     */
    private String convertToJsonObj(String tag, String str) {
        return "{" + convertToJsonComponent(tag, str) + "}";
    }

    /**
     * Takes a normal string and formats it so that it can be parsed into a JSON parameter, whose name will be the one
     * provided, and whose value will be the given string.
     *
     * @param tag   A String which will be JSON parameter name.
     * @param str   A String which will be the JSON parameter value.
     * @return  A String in JSON parameter format.
     */
    private String convertToJsonComponent(String tag, String str) {
        return "\"" + tag + "\":\"" + str + "\"";
    }

    /**
     * Checks the authorisation af a members security token, and throws an error if it fails.
     *
     * @param token A String which is the members security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    private void checkMemberAuthorisation(String token) throws ToBeConvertedToHTTPResponseException {
        if (!this.securityTokenUnit.checkMemberToken(token)) {
            cLog("TOKEN NOT VALID");
            throw new ToBeConvertedToHTTPResponseException(LOGIN_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Checks the authorisation af an admin security token, and throws an error if it fails.
     *
     * @param token A String which is the admins security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */    private void checkAdminAuthorisation(String token) throws ToBeConvertedToHTTPResponseException {
        if (!this.securityTokenUnit.checkAdminToken(token)) {
            cLog("TOKEN NOT VALID");
            throw new ToBeConvertedToHTTPResponseException(LOGIN_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        }
    }
}
