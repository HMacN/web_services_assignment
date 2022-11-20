// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.

package welcome.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import welcome.model.*;
import welcome.service.*;

import java.util.List;

@RestController
@CrossOrigin
public class WelcomeController {

    // The WelcomeController depends on the WelcomeService, so it needs to keep a reference to it.
    private WelcomeService ws;

    // The fact that the constructor for the WelcomeController requires a
    // WelcomService argument tells Spring to auto-configure a WelcomeService
    // and pass it to the constructor. This is called "Dependency Injection",
    // and it (a) saves boilerplate code, and (b) makes it easy to swap
    // components. (We can change the WelcomeService implementation without
    // changing any code in the rest of the system.)
    public WelcomeController(WelcomeService ws) {
        this.ws = ws;
    }

    @GetMapping("/ding/{lang}")
    public Welcome getWelcome(@PathVariable String lang, @RequestParam(required=false) String name) {
        Welcome welcome = ws.getWelcome(lang, name);
        if (welcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return welcome;
    }

    @GetMapping("/ding")
    public List<Welcome> getAllWelcomes() {
        return ws.getAllWelcomes();
    }

    @PostMapping("/ding")
    public void postNewWelcome(@RequestBody Welcome newWelcome) throws WelcomeForThisLanguageAlreadyExistsException {
        ws.addWelcome(newWelcome);
        ws.addWelcome(newWelcome);
    }

    @PutMapping("/ding/{lang}")
    public void putNewWelcome(@RequestBody Welcome welcome, @PathVariable String lang) throws LanguageDoesNotExistException, LanguagesDontMatchException {
        // Check languages match
        if (!welcome.getLang().equals(lang)) {
            throw new LanguagesDontMatchException();
        }

        ws.updateWelcome(welcome);
    }
}

@ControllerAdvice
class LanguageAlreadyExistsAdvice {

    @ResponseBody
    @ExceptionHandler(value = WelcomeForThisLanguageAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WelcomeForThisLanguageAlreadyExistsException languageNotFoundHandler(WelcomeForThisLanguageAlreadyExistsException ex) {
        return ex;
    }
}

@ControllerAdvice
class LanguageDoesNotExistAdvice {

    @ResponseBody
    @ExceptionHandler(value = LanguageDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public LanguageDoesNotExistException languageNotFoundHandler(LanguageDoesNotExistException ex) {
        return ex;
    }
}

@ControllerAdvice
class LanguagesDontMatchAdvice {

    @ResponseBody
    @ExceptionHandler(value = LanguagesDontMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public LanguagesDontMatchException languageNotFoundHandler(LanguagesDontMatchException ex) {
        return ex;
    }
}
