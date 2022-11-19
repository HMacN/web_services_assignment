// An interface to the business logic, living in the service sub-package.

package welcome.service;

import java.util.List;

import welcome.model.*;

public interface WelcomeService {

    // Adds a welcome to the database, or overwrites an existing one.
    void addWelcome(Welcome welcome);

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
    void removeWelcome(String lang);

}
