package voting_system.controller;

import org.springframework.http.HttpStatus;

/**
 * A class to hold any exceptions which are to be converted into responses to the client.  This will be thrown at
 * points in the code where the client requests are found to be non-viable, and then converted to HTTP responses by the
 * Controller.
 */
public class ToBeConvertedToHTTPResponseException extends Throwable {
    public final String MESSAGE;    // The message to be presented to the client.
    public final HttpStatus STATUS; // The HTTP Response code to be sent to the client.

    /**
     * A constructor.
     *
     * @param message   A String which is the message to be presented to the client.
     * @param status    A String which is the HTTP Response code to be sent to the client.
     */
    public ToBeConvertedToHTTPResponseException(String message, HttpStatus status) {
        this.MESSAGE = message;
        this.STATUS = status;
    }
}
