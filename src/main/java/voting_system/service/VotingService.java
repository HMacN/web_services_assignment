// An interface to the business logic, living in the service sub-package.

package voting_system.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import voting_system.controller.LoginAttempt;
import voting_system.model.*;

public interface VotingService
{

    // Adds a welcome to the database.
    void addWelcome(Welcome welcome) throws WelcomeForThisLanguageAlreadyExistsException;

    //Updates a welcome in the database.
    void updateWelcome(Welcome welcome) throws LanguageDoesNotExistException;

    // Returns true if there is a welcome in language lang.
    default boolean hasWelcome(String lang) {
        return getWelcome(lang) != null;
    }

    // Returns a welcome in language lang; null if there is none.
    default Welcome getWelcome(String lang) {
        return getWelcome(lang, null);
    }

    // Returns a welcome in language lang, personalised to name.
    Welcome getWelcome(String lang, String name);

    // Returns a list of all welcomes in the database.
    List<Welcome> getAllWelcomes();

    // Removes the welcome for language lang from the database.
    void removeWelcome(String lang) throws LanguageDoesNotExistException;

    ResponseEntity<String> logoutMember();

    ResponseEntity<String> attemptMemberLogin(LoginAttempt loginAttempt);

    ResponseEntity<String> castVote(Member member, Candidate candidate);

    ResponseEntity<String> withdrawVote(Member member);
}
