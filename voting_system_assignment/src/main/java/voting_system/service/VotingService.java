// An interface to the business logic, living in the service sub-package.

package voting_system.service;

import voting_system.controller.*;

/**
 * An interface for the voting service.  This allows new back-ends to be substituted in to the system without breaking
 * the mappings in the Controller.
 */
public interface VotingService
{
    /**
     * Revoke a members security token.
     *
     * @param token A String which is a token to be revoked.
     * @return  A String which is a message to the user on token revocation.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    String logoutMember(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Logs a member in using the details provided, and issues a security token.
     *
     * @param loginAttempt  A LoginAttempt object which contains the details of the member trying to log in.
     * @return  A String which is the issued security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    String attemptMemberLogin(LoginAttempt loginAttempt) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an authorised member to cast their vote.
     *
     * @param token A String which is the members security token.
     * @param candidateName A String which is the name of the candidate being voted for.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    void castVote(String token, String candidateName) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an authorised member to withdraw their vote.
     *
     * @param token A String which is the members security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    void withdrawVote(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Let authorised members get the details of all candidates in the election.
     *
     * @param token The security token of the member.
     * @return  A String which contains the details of the candidates in the election in JSON format.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    Candidate[] getCandidates(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an authorised member to retrieve their own ballot.
     *
     * @param token A String which contains the members security token.
     * @return  A Ballot object which contains the members vote details.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    Ballot getBallot(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an administrator to login and be issued a security token.
     *
     * @param loginAttempt  A LoginAttempt object which contains the details of the admin logging in.
     * @return  A String which is the security token for this admin.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    String attemptAdminLogin(AdminLoginAttempt loginAttempt) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an admin to be logged out, and to revoke their security token.
     *
     * @param token A String which is the security token of the admin.
     * @return  A String which is a message to be delivered to the user on token revocation.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    String logoutAdmin(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows admins to tally the votes currently cast in the election.
     *
     * @param token A String which is the security token of the admin.
     * @return  A String which contains the tally of the votes cast for different candidates in JSON format.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    String tallyVotes(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an admin to open voting in the election.
     *
     * @param token A String which is the admins security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    void openVoting(String token) throws ToBeConvertedToHTTPResponseException;

    /**
     * Allows an admin to close voting in the election.
     *
     * @param token  A String which is the admins security token.
     *
     * @throws ToBeConvertedToHTTPResponseException An exception to be converted into an HTTP response message.
     */
    void closeVoting(String token) throws ToBeConvertedToHTTPResponseException;
}
