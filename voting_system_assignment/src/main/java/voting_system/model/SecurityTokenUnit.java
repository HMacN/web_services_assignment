package voting_system.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import voting_system.controller.LoginAttempt;

import java.util.LinkedList;

import static voting_system.VotingSystemApplication.cLog;

/**
 * A class to handle the issuing and validation of secure access tokens to users.
 */
public class SecurityTokenUnit {
    private final String SECRET_KEY = "EsUuvIbpdCCpZSO5QYzIbA"; // A secret key for encryption purposes.  TODO - This should be migrated to a secure persistent database if one becomes available.
    private final String PREFIX = "Bearer ";    // A prefix to add to the key to make it correctly formatted for transmitting in HTTP headers.
    private final String ISSUER_DETAILS = "SWAB_VOTING_SYSTEM"; // The name of the token issuer.
    private LinkedList<String> blackList = new LinkedList<>();  // A list of tokens whose holders have logged out.  These are stored until they time out.
    private final Algorithm algorithm = Algorithm.HMAC256(this.SECRET_KEY); // The algorithm used to encrypt the tokens.
    private final long TICKET_LIFESPAN_MILLIS = 30_000; // The lifespan of the tokens in milliseconds.

    /**
     * A function to allow the issuing of security tokens to members.
     *
     * @param loginAttempt  A LoginAttempt object which holds the details of the member to issue a token to.
     * @return  A String which is the issued security token.
     */
    public String issueMemberSecurityToken(LoginAttempt loginAttempt) {
        tidyBlackList();

        String token = "";
        String currentTime = String.valueOf(System.currentTimeMillis());

        try {
            token = JWT.create()
                    .withIssuer(ISSUER_DETAILS)
                    .withClaim("member", true)
                    .withClaim("admin", false)
                    .withClaim("number", loginAttempt.user_number)
                    .withClaim("issue_time", currentTime)
                    .sign(this.algorithm);
        }
        catch (JWTCreationException e) {
            cLog("TOKEN GENERATION EXCEPTION: " + e);
            //e.printStackTrace();
        }

        return this.PREFIX + token;
    }

    /**
     * Allows the revoking of tokens before their expiry time by adding them to a blacklist.
     *
     * @param token A String which is the security token to be revoked.
     */
    public void revokeToken(String token) {
        cLog("REVOKING TOKEN " + token);
        tidyBlackList();
        this.blackList.add(token);
    }

    /**
     * A function to check if a members token is currently valid.  Checks the token contents to ensure that it is still
     * within its expiry time, belongs to a member, and is not on the blacklist.
     *
     * @param token A String which is a security token to check.
     * @return  A boolean indicating if the given token is valid.
     */
    public boolean checkMemberToken(String token) {
        tidyBlackList();

        if (tokenIsOnBlacklist(token)) {
            cLog("TOKEN FOUND TO BE ON BLACKLIST");
            return false;
        }

        cLog("TOKEN NOT ON BLACKLIST");

        return memberTokenIsValid(token);
    }

    /**
     * A function which checks that a members token is still within its expiry time and that it belongs to a member.
     *
     * @param token A String which is the token to be checked.
     * @return  A boolean which indicates if the token is valid.
     */
    private boolean memberTokenIsValid(String token) {
        token = trimPrefix(token);

        try {
            JWTVerifier verifier = JWT.require(this.algorithm)
                    .withIssuer(ISSUER_DETAILS)
                    .withClaim("member", true)
                    .build();

            verifier.verify(token);
        }
        catch (JWTVerificationException e) {
            cLog("JWT MEMBER VERIFICATION FAILURE: " + e);
            //e.printStackTrace();
            return false;
        }

        cLog("TOKEN IS VERIFIED MEMBER");

        return tokenHasNotTimedOut(token);
    }

    /**
     * A function which checks if a token is still within its expiry time.
     *
     * @param token A String which is the token to be checked.
     * @return  A boolean which indicates if the token is within its expiry time.
     */
    private boolean tokenHasNotTimedOut(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm)
                .build()
                .verify(token);
        String issueTime = decodedJWT.getClaim("issue_time").asString();

        long currentTime = System.currentTimeMillis();
        long tokenMaxAge = Long.parseLong(issueTime) + this.TICKET_LIFESPAN_MILLIS;
        long tokenMinAge = currentTime - (2 * this.TICKET_LIFESPAN_MILLIS);

        cLog("MAX AGE:      " + tokenMaxAge);
        cLog("CURRENT TIME: " + currentTime);
        cLog("MIN AGE:      " + tokenMinAge);


        // Check minimum age as well as max age, in case a very old token is recycled
        // (i.e. from before the last time the system clock was at zero).
        return currentTime <= tokenMaxAge &&
                currentTime >= tokenMinAge;
    }

    /**
     * Trims the HTTP Header prefix from a given token.
     *
     * @param untrimmedToken    A String which is the token to remove the prefix from.
     * @return  A String which is the token without its prefix.
     */
    private String trimPrefix(String untrimmedToken) {
        return untrimmedToken.replace(PREFIX, "");
    }

    /**
     * Checks if the given token is on the blacklist of revoked tokens.
     *
     * @param untrimmedToken    A String which is the token to check.
     * @return  A boolean which indicates whether or not the token was found on the blacklist.
     */
    private boolean tokenIsOnBlacklist(String untrimmedToken) {
        return (this.blackList.contains(untrimmedToken));
    }

    /**
     * A function to clear old tokens out of the blacklist.  Tokens are reassessed for validity.  If they are still
     * valid, they stay on the blacklist to stop them being used.  if they are now invalid (they have timed out), then
     * they are removed from the blacklist.
     */
    private void tidyBlackList() {
        LinkedList<String> tokensToRemove = new LinkedList<>();

        for (String blackListedToken : blackList) {
            if (!memberTokenIsValid(blackListedToken) && !adminTokenIsValid(blackListedToken)) {
                tokensToRemove.add(blackListedToken);
            }
        }

        this.blackList.removeAll(tokensToRemove);
    }

    /**
     * Finds the membership number associated with a particular token.
     *
     * @param untrimmedToken    A String which is the token to get the membership number from.
     * @return  A String which is the membership number associated with this token.
     */
    public String getMemberNumberForToken(String untrimmedToken) {
        String token = trimPrefix(untrimmedToken);

        DecodedJWT decodedJWT = JWT.require(algorithm)
                .build()
                .verify(token);
        return decodedJWT.getClaim("number").asString();
    }

    /**
     * A function which allows the issuing of security tokens to administrators.  Tokens are prefixed to make them
     * suitable to be included in HTTP headers.
     *
     * @param adminPass The administrator code that this token is to be associated with.
     * @return  A String which is the security token issued to this admin.
     */
    public String issueAdminSecurityToken(String adminPass) {
        tidyBlackList();

        String token = "";
        String currentTime = String.valueOf(System.currentTimeMillis());

        try {
            token = JWT.create()
                    .withIssuer(ISSUER_DETAILS)
                    .withClaim("member", false)
                    .withClaim("admin", true)
                    .withClaim("pass", adminPass)
                    .withClaim("issue_time", currentTime)
                    .sign(this.algorithm);
        }
        catch (JWTCreationException e) {
            cLog("TOKEN GENERATION EXCEPTION: " + e);
            //e.printStackTrace();
        }

        return this.PREFIX + token;
    }

    /**
     * A function which checks if admin security tokens are valid.  Checks if they are within their expiry time, not on
     * the blacklist, and that they were issued to an admin.
     *
     * @param token A String which is the token to be verified.
     * @return  A boolean which indicates if the given token was valid.
     */
    public boolean checkAdminToken(String token) {
        tidyBlackList();

        if (tokenIsOnBlacklist(token)) {
            cLog("TOKEN FOUND TO BE ON BLACKLIST");
            return false;
        }

        cLog("TOKEN NOT ON BLACKLIST");

        return adminTokenIsValid(token);
    }

    /**
     * A function which checks if a given token belongs ot an admin and is within its expiry time.
     *
     * @param token A String which is the token to check.
     * @return  A boolean which indicates if the token is an unexpired token belonging to an admin.
     */
    private boolean adminTokenIsValid(String token) {
        token = trimPrefix(token);

        try {
            JWTVerifier verifier = JWT.require(this.algorithm)
                    .withIssuer(ISSUER_DETAILS)
                    .withClaim("admin", true)
                    .build();

            verifier.verify(token);
        }
        catch (JWTVerificationException e) {
            cLog("JWT ADMIN VERIFICATION FAILURE: " + e);
            //e.printStackTrace();
            return false;
        }

        cLog("TOKEN IS VERIFIED ADMIN");

        return tokenHasNotTimedOut(token);
    }
}
