// Implementation of the business logic, living in the service sub-package.
// Discoverable for auto-configuration, thanks to the @Component annotation.

package welcome.service;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import welcome.model.*;

@Component
public class WelcomeServiceImpl implements WelcomeService {

    // Very simple in-memory database; key is the lang field of class Welcome.
    // We have to be careful with this 'database'. In order to avoid objects
    // in the database being mutated accidentally, we must always copy objects
    // before insertion and retrieval.
    private Map<String, Welcome> db;

    public WelcomeServiceImpl() {
        db = new HashMap<>();
    }

    // Adds a welcome to the database.
    public void addWelcome(Welcome welcome) throws WelcomeForThisLanguageAlreadyExistsException
    {
        // Check for any null values.
        if (welcome != null && welcome.getLang() != null) {
            // Check language welcome does not already exist.
            if (this.db.containsKey(welcome.getLang())) {
                throw new WelcomeForThisLanguageAlreadyExistsException();
            }
            
            // copying welcome to isolate objects in the database from changes
            welcome = new Welcome(welcome);
            db.put(welcome.getLang(), welcome);
        }
    }

    // Modifies a welcome in the database.
    public void updateWelcome(Welcome welcome) throws LanguageDoesNotExistException
    {
        // Check for any null values.
        if (welcome != null && welcome.getLang() != null) {
            // Check language welcome does not already exist.
            if (!this.db.containsKey(welcome.getLang())) {
                throw new LanguageDoesNotExistException();
            }

            // copying welcome to isolate objects in the database from changes
            welcome = new Welcome(welcome);
            db.put(welcome.getLang(), welcome);
        }
    }

    // Returns a welcome in language lang, personalised to name.
    // Parameter lang must not be null, but name may be null.
    public Welcome getWelcome(String lang, String name) {
        Welcome welcome = db.get(lang);
        if (welcome == null) {
            return null;
        }
        // copying welcome to protect objects in the database from changes
        welcome = new Welcome(welcome);
        if (name != null) {
            welcome.setMsg(welcome.getMsg() + ", " + name);
        }
        return welcome;
    }

    // Return a list of all welcomes.
    public List<Welcome> getAllWelcomes() {
        //throw new UnsupportedOperationException();

        LinkedList<Welcome> storedWelcomes = new LinkedList<>();
        LinkedList<Welcome> safeToReturnWelcomes = new LinkedList<>();

        storedWelcomes.addAll(db.values());

        // Copy welcomes to protect database
        for (Welcome original : storedWelcomes)
        {
            Welcome clone = new Welcome(original);
            safeToReturnWelcomes.add(clone);
        }

        return safeToReturnWelcomes;
    }

    // Removes an existing welcome.
    public void removeWelcome(String lang) throws LanguageDoesNotExistException {
        // Check for any null values.
        if (lang != null ) {
            if (this.db.containsKey(lang)) {
                this.db.remove(lang);
            } else {
                throw new LanguageDoesNotExistException();
            }
        }
    }
}
